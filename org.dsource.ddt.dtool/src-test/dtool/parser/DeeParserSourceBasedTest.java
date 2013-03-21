/*******************************************************************************
 * Copyright (c) 2012, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.parser;

import static dtool.parser.DeeParserTest.runParserTest______________________;
import static dtool.tests.DToolTestResources.getTestResource;
import static dtool.util.NewUtils.assertNotNull_;
import static dtool.util.NewUtils.removeRange;
import static dtool.util.NewUtils.replaceRange;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

import melnorme.utilbox.core.Predicate;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dtool.ast.SourceRange;
import dtool.parser.DeeParserTest.NamedNodeElement;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.sourcegen.TemplateSourceProcessorParser.TspExpansionElement;
import dtool.sourcegen.TemplatedSourceProcessor;
import dtool.tests.SimpleParser;
import dtool.util.NewUtils;

@RunWith(Parameterized.class)
public class DeeParserSourceBasedTest extends DeeSourceBasedTest {
	
	protected static final String TESTFILESDIR = "dtool.parser/";
	
	protected static Map<String, TspExpansionElement> commonDefinitions = new HashMap<String, TspExpansionElement>();
	
	@Parameters
	public static Collection<Object[]> filesToParse() throws IOException {
		ArrayList<File> commonHeaderFileList = getDeeModuleList(getTestResource(TESTFILESDIR+"/_common"), true);
		
		for (File headerFile : commonHeaderFileList) {
			TemplatedSourceProcessor tsp = new TestsTemplateSourceProcessor();
			tsp.processSource_unchecked("#", readStringFromFileUnchecked(headerFile));
			NewUtils.addNew(commonDefinitions, tsp.getGlobalExpansions());
		}
		
		return toFnParameterList(getDeeModuleList(getTestResource(TESTFILESDIR), true), new Predicate<File>() {
			@Override
			public boolean evaluate(File file) {
				if(file.getName().endsWith("_TODO") 
					|| file.getParentFile().getName().equals("_common")
					|| file.getName().endsWith(".d") == false)
					return true;
				return false;
			}
		});
	}
	
	protected final File file;
	
	public DeeParserSourceBasedTest(File file) {
		this.file = file;
	}
	
	@Test
	public void runSourceBasedTests() throws Exception { runSourceBasedTests$(); }
	public void runSourceBasedTests$() throws Exception {
		AnnotatedSource[] sourceBasedTests = getSourceBasedTests(file, commonDefinitions);
		HashSet<String> printSources = new HashSet<String>();
		Pattern trimStartNewlines = Pattern.compile("(^(\\n|\\r\\n)*)|((\\n|\\r\\n)*$)");
		
		for (AnnotatedSource testCase : sourceBasedTests) {
			
			if(testCase.findMetadata("comment", "NO_STDOUT") == null) {
				if(!printSources.contains(testCase.originalTemplatedSource)) {
					testsLogger.println(">> ----------- Parser source test: ----------- <<");
				}
				testsLogger.println(trimStartNewlines.matcher(testCase.source).replaceAll(""));
			} 
			else if(!printSources.contains(testCase.originalTemplatedSource)){
				testsLogger.println(">> ----------- Parser source test (TEMPLATE): ----------- <<");
				testsLogger.println(testCase.originalTemplatedSource);
			}
			printSources.add(testCase.originalTemplatedSource);
			runSourceBasedTest(testCase);
		}
		testsLogger.println();
	}
	
	public static void checkOffsetInvariant(AnnotatedSource testSource) {
		int mdOffset = 0;
		for (MetadataEntry mde : testSource.metadata) {
			if(mde.offset != -1) {
				assertTrue(mde.offset >= mdOffset);
			}
			mdOffset = mde.offset;
		}
	}
	
	public void runSourceBasedTest(AnnotatedSource testSource) {
		checkOffsetInvariant(testSource);
		
		final String DEFAULT_VALUE = "##DEFAULT VALUE";
		
		final String parseSource = testSource.source;
		String parseRule = null;
		String expectedRemainingSource = null;
		String expectedGenSource = DEFAULT_VALUE;
		NamedNodeElement[] expectedStructure = null;
		boolean allowAnyErrors = false;
		boolean ignoreFurtherErrorMDs = false;
		
		String calcExpectedSource = parseSource;
		int modifyDelta = 0;
		
		ArrayList<ParserError> expectedErrors = new ArrayList<ParserError>();
		
		for (MetadataEntry mde : testSource.metadata) {
			if(mde.name.equals("AST_SOURCE_EXPECTED")) {
				assertTrue(expectedGenSource == DEFAULT_VALUE);
				if(areEqual(mde.value, "NoCheck")) {
					expectedGenSource = null;
				} else {
					expectedGenSource = assertNotNull_(mde.sourceValue);
				}
				ignoreFurtherErrorMDs = true;
			} else if(mde.name.equals("PARSE")){
				parseRule = mde.value;
			} else if(mde.name.equals("parser") && areEqual(mde.value, "IgnoreRest")){
				int pos = mde.getOffsetFromNoLength();
				if(expectedRemainingSource == null) {
					expectedRemainingSource = parseSource.substring(pos);
					calcExpectedSource = calcExpectedSource.substring(0, pos + modifyDelta);
					ignoreFurtherErrorMDs = true;
				}
			} else if(mde.name.equals("AST_STRUCTURE_EXPECTED")) {
				assertTrue(expectedStructure == null);
				expectedStructure = parseExpectedStructure(mde.sourceValue);
			} else if(mde.name.equals("error") || mde.name.equals("ERROR")){
				if(ignoreFurtherErrorMDs) 
					continue;
				
				ParserError error = decodeError(parseSource, mde);
				expectedErrors.add(error);
				
				SourceRange sr = mde.getSourceRange();
				int offset = sr.getOffset(); 
				if(error.errorType == ParserErrorTypes.INVALID_TOKEN_CHARACTERS) {
					calcExpectedSource = removeRange(calcExpectedSource, offset + modifyDelta, sr.getLength());
					modifyDelta -= sr.getLength();
				}
				if(error.errorType == ParserErrorTypes.EXPECTED_TOKEN) {
					String rpl;
					if(mde.sourceValue != null) {
						assertTrue(mde.sourceWasIncluded == false || mde.sourceValue.isEmpty());
						rpl = mde.sourceValue;
					} else {
						DeeTokens expectedToken = DeeTokens.valueOf(error.msgData.toString());
						if(expectedToken.getSourceValue() == null) 
							continue;
						rpl = expectedToken.getSourceValue();
					}
					
					calcExpectedSource = replaceRange(calcExpectedSource, offset + modifyDelta, 0, rpl);
					modifyDelta += rpl.length();
				}
				
			} else if(mde.name.equals("parser") && mde.value.equals("AllowAnyErrors")){
				allowAnyErrors = true;
			} else {
				if(!(areEqual(mde.value, "flag") || areEqual(mde.name, "comment"))) {
					assertFail("Unknown metadata");
				}
			}
		}
		
		if(expectedGenSource == DEFAULT_VALUE) {
			expectedGenSource = calcExpectedSource;
		}
		
		runParserTest______________________(
			parseSource, parseRule, expectedRemainingSource, expectedGenSource, expectedStructure, 
			expectedErrors, allowAnyErrors);
	}
	
	public ParserError decodeError(String parseSource, MetadataEntry mde) {
		String errorType = StringUtil.upUntil(mde.value, "_");
		String errorParam = NewUtils.fromIndexOf("_", mde.value);
		
		DeeLexer deeLexer = new DeeLexer(parseSource);
		
		SourceRange errorRange = mde.getSourceRange();
		
		if(errorType.equals("ITC")) {
			return new ParserError(ParserErrorTypes.INVALID_TOKEN_CHARACTERS, errorRange, mde.sourceValue, null);
		} else if(errorType.equals("MT") || errorType.equals("MTC")) {
			errorParam = DeeLexerSourceBasedTest.parseExpectedError(errorParam).toString();
			return createErrorToken(ParserErrorTypes.MALFORMED_TOKEN, mde, deeLexer, true, errorParam);
		} else if(errorType.equals("EXP")) {
			String expectedTokenStr = DeeLexerSourceBasedTest.transformTokenNameAliases(errorParam);
			return createErrorToken(ParserErrorTypes.EXPECTED_TOKEN, mde, deeLexer, true, expectedTokenStr);
		} else if(errorType.equals("EXPRULE")) {
			errorParam = getExpectedRuleName(errorParam);
			return createErrorToken(ParserErrorTypes.EXPECTED_RULE, mde, deeLexer, true, errorParam);
		} else if(errorType.equals("SE") || errorType.equals("<SE")) {
			errorParam = getExpectedRuleName(errorParam);
			boolean tokenBefore = errorType.equals("<SE");
			return createErrorToken(ParserErrorTypes.SYNTAX_ERROR, mde, deeLexer, tokenBefore, errorParam);
		} else if(mde.value.equals("BAD_LINKAGE_ID")) {
			return createErrorToken(ParserErrorTypes.INVALID_EXTERN_ID, mde, deeLexer, true, null);
		} else if(errorType.equals("REQPARENS")) {
			String errorSource = assertNotNull_(mde.sourceValue);
			errorParam = errorParam == null ? DeeParserTest.DONT_CHECK : errorParam;
			return new ParserError(ParserErrorTypes.EXP_MUST_HAVE_PARENTHESES, errorRange, errorSource, errorParam);
		} else if(mde.value.equals("TYPE_AS_EXP_VALUE")) {
			String errorSource = assertNotNull_(mde.sourceValue);
			return new ParserError(ParserErrorTypes.TYPE_USED_AS_EXP_VALUE, errorRange, errorSource, null);
		} else if(mde.value.equals("INV_QUALIFIER")) {
			String errorSource = assertNotNull_(mde.sourceValue);
			return new ParserError(ParserErrorTypes.INVALID_QUALIFIER, errorRange, errorSource, null);
		} else if(mde.value.equals("NO_TPL_SINGLE_ARG")) {
			String errorSource = assertNotNull_(mde.sourceValue);
			return new ParserError(ParserErrorTypes.NO_CHAINED_TPL_SINGLE_ARG, errorRange, errorSource, null);
		} else{
			throw assertFail();
		}
	}
	
	public ParserError createErrorToken(ParserErrorTypes errorTypeTk, MetadataEntry mde, DeeLexer deeLexer,
		boolean tokenBefore, String errorParam) {
		Token lastToken = tokenBefore 
			? findLastEffectiveTokenBeforeOffset(mde.offset, deeLexer)
			: findNextEffectiveTokenAfterOffset(mde.offset, deeLexer);
			
		SourceRange errorRange = AbstractParser.sr(lastToken);
		String errorSource = lastToken.source;
		return new ParserError(errorTypeTk, errorRange, errorSource, errorParam);
	}
	
	public String getExpectedRuleName(String errorParam) {
		if(errorParam.equals("decl")) {
			errorParam = DeeParser.RULE_DECLARATION.name;
		} else if(errorParam.equals("exp")) {
			errorParam = DeeParser.RULE_EXPRESSION.name;
		} else if(errorParam.equals("ref")) {
			errorParam = DeeParser.RULE_REFERENCE.name;
		} else if(errorParam.equals("RoE")) {
			errorParam = DeeParser.RULE_REF_OR_EXP.name;
		} else if(errorParam.equals("TplArg")) {
			errorParam = DeeParser.RULE_TPL_SINGLE_ARG.name;
		}
		return errorParam;
	}
	
	public Token findLastEffectiveTokenBeforeOffset(int offset, DeeLexer deeLexer) {
		assertTrue(offset <= deeLexer.source.length());
		
		Token lastNonIgnoredToken = null;
		while(true) {
			Token token = deeLexer.next();
			if(token.getStartPos() >= offset) {
				assertNotNull(lastNonIgnoredToken);
				deeLexer.reset(lastNonIgnoredToken.startPos);
				break;
			}
			if(token.type.isParserIgnored) {
				continue;
			}
			lastNonIgnoredToken = token;
		}
		return lastNonIgnoredToken;
	}
	
	public Token findNextEffectiveTokenAfterOffset(int offset, DeeLexer deeLexer) {
		assertTrue(offset <= deeLexer.source.length());
		
		while(true) {
			Token token = deeLexer.next();
			if(token.type == DeeTokens.EOF) {
				assertFail();
			}
			if(token.type.isParserIgnored) {
				continue;
			}
			if(token.getStartPos() >= offset) {
				return token;
			}
			assertTrue(token.getEndPos() <= offset);
		}
	}
	
	protected NamedNodeElement[] parseExpectedStructure(String source) {
		SimpleParser parser = new SimpleParser(source);
		NamedNodeElement[] namedElements = readNamedElementsList(parser);
		assertTrue(parser.lookaheadIsEOF() || parser.lookAhead() == '$');
		return namedElements;
	}
	
	public static NamedNodeElement[] readNamedElementsList(SimpleParser parser) {
		ArrayList<NamedNodeElement> elements = new ArrayList<NamedNodeElement>();
		
		while(true) {
			String id;
			NamedNodeElement[] children = null;
			
			parser.seekWhiteSpace();
			if(parser.tryConsume("*")) {
				id = NamedNodeElement.IGNORE_ALL;
			} else {
				if(parser.tryConsume("?")) {
					id = NamedNodeElement.IGNORE_NAME;
				} else {
					id = parser.consumeAlphaNumericUS(true);
					if(id.isEmpty()) {
						break;
					}
					parser.seekWhiteSpace();
				}
				if(parser.tryConsume("(")) {
					children = readNamedElementsList(parser);
					parser.seekWhiteSpace();
					assertTrue(parser.tryConsume(")") || parser.lookAhead() == '$');
				} else {
					children = new NamedNodeElement[0];
				}
			}
			elements.add(new NamedNodeElement(id, children));
		}
		return ArrayUtil.createFrom(elements, NamedNodeElement.class);
	}
	
}