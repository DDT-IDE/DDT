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

import static dtool.tests.DToolTestResources.getTestResource;
import static dtool.util.NewUtils.assertNotNull_;
import static dtool.util.NewUtils.isValidStringRange;
import static dtool.util.NewUtils.removeRange;
import static dtool.util.NewUtils.replaceRange;
import static java.util.Collections.unmodifiableMap;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;
import static melnorme.utilbox.misc.CollectionUtil.filter;

import java.io.File;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import melnorme.utilbox.core.Predicate;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.Pair;
import melnorme.utilbox.misc.StringUtil;

import org.junit.BeforeClass;
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
public class DeeParserSourceBasedTest extends DeeTemplatedSourceBasedTest {
	
	protected static final String TESTFILESDIR = "dtool.parser/";
	
	protected static Map<String, TspExpansionElement> commonDefinitions = new HashMap<String, TspExpansionElement>();
	
	@BeforeClass
	public static void initCommonDefinitions() throws IOException {
		List<File> commonDefsFileList = getDeeModuleList(getTestResource(TESTFILESDIR));
		commonDefsFileList = filter(commonDefsFileList, new ParserTestFilesFilter(){{filterHeaders = false;}});
		
		testsLogger.println(">>>>>>============ " + DeeParserSourceBasedTest.class.getSimpleName() 
			+ " COMMON DEFINITIONS FILES: ============" );
		for (File headerFile : commonDefsFileList) {
			testsLogger.println(headerFile);
			TemplatedSourceProcessor tsp = new TestsTemplateSourceProcessor() {
				@Override
				protected void addFullyProcessedSourceCase(ProcessingState caseState) {
					assertTrue(caseState.isHeaderCase);
				}
			};
			tsp.processSource_unchecked("#", readStringFromFileUnchecked(headerFile));
			assertTrue(tsp.getGenCases().size() == 0);
			NewUtils.addNew(commonDefinitions, tsp.getGlobalExpansions());
		}
		testsLogger.println("<<<<<<" );
	}
	
	@Parameters(name="{index}: {0}")
	public static Collection<Object[]> filesToParse() throws IOException {
		return toParameterList(true,
			filter(getDeeModuleList(getTestResource(TESTFILESDIR)), new ParserTestFilesFilter()));
	}
	
	protected static class ParserTestFilesFilter implements Predicate<File> {
		boolean filterHeaders = true;
		@Override
		public boolean evaluate(File file) {
			if(file.getName().endsWith("_TODO")) return true;
			if(file.getParentFile().getName().equals("0_common")) return filterHeaders;
			if(file.getName().contains(".export.") || file.getName().contains(".EXPORT.")) return filterHeaders;
			if(file.getName().endsWith(".tsp")) return !filterHeaders;
			throw assertFail();
		}
	}
	
	
	public DeeParserSourceBasedTest(String testUIDescription, File file) {
		super(testUIDescription, file);
	}
	
	@Test
	public void runSourceBasedTests() throws Exception { runSourceBasedTests$(); }
	public void runSourceBasedTests$() throws Exception {
		AnnotatedSource[] sourceBasedTests = getSourceBasedTests(commonDefinitions);
		HashSet<String> printSources = new HashSet<String>();
		Pattern trimStartNewlines = Pattern.compile("(^(\\n|\\r\\n)*)|((\\n|\\r\\n)*$)");
		
		int originalTemplateChildCount = -1;
		for (AnnotatedSource testCase : sourceBasedTests) {
			
			boolean printTestCaseSource = testCase.findMetadata("comment", "NO_STDOUT") == null;
			
			if(!printSources.contains(testCase.originalTemplatedSource)) {
				printCaseEnd(originalTemplateChildCount);
				originalTemplateChildCount = 0;
				testsLogger.println(">> ----------- Parser tests TEMPLATE ("+file.getName()+") : ----------- <<");
				testsLogger.print(testCase.originalTemplatedSource);
				if(printTestCaseSource) {
					testsLogger.println(" ----------- Parser source tests: ----------- ");
				}
			}
			if(printTestCaseSource) {
				testsLogger.println(trimStartNewlines.matcher(testCase.source).replaceAll(""));
			}
			printSources.add(testCase.originalTemplatedSource);
			
			runSourceBasedTest(testCase);
			originalTemplateChildCount++;
		}
		printCaseEnd(originalTemplateChildCount);
		testsLogger.println();
	}
	
	public void printCaseEnd(int originalTemplateChildCount) {
		if(originalTemplateChildCount > 10 && originalTemplateChildCount != -1) {
			testsLogger.println("<< ^^^ Previous case count: " + originalTemplateChildCount);
		}
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
	
	public static class ParserErrorExt extends ParserError {
		public String sourceCorrectionForMissingTokens;
		
		public ParserErrorExt(ParserError error, String correctionForMissingTokens) {
			super(error.errorType, error.sourceRange, error.msgErrorSource, error.msgData);
			this.sourceCorrectionForMissingTokens = correctionForMissingTokens;
		}
		
	}
	
	public void runSourceBasedTest(AnnotatedSource testSource) {
		checkOffsetInvariant(testSource);
		
		final String DEFAULT_VALUE = "##DEFAULT VALUE";
		
		final String fullSource = testSource.source;
		final LexElementSource lexSource = new DeeParser(fullSource).lexSource;
		
		String parsedSource = fullSource;
		String expectedRemainingSource = null;
		String parseRule = null;
		
		String expectedPrintedSource = DEFAULT_VALUE;
		NamedNodeElement[] expectedStructure = null;
		boolean allowAnyErrors = false;
		boolean ignoreFurtherErrorMDs = false;
		
		ArrayList<ParserError> expectedErrors = new ArrayList<ParserError>();
		HashMap<String, MetadataEntry> additionalMetadata = new HashMap<String, MetadataEntry>();
		List<ParserErrorExt> errorCorrectionMetadata = new ArrayList<ParserErrorExt>();
		
		for (MetadataEntry mde : testSource.metadata) {
			if(mde.name.equals("AST_SOURCE_EXPECTED")) {
				assertTrue(expectedPrintedSource == DEFAULT_VALUE);
				if(areEqual(mde.value, "NoCheck")) {
					expectedPrintedSource = null;
				} else {
					expectedPrintedSource = assertNotNull_(mde.sourceValue);
				}
				ignoreFurtherErrorMDs = true;
			} else if(mde.name.equals("PARSE")){
				parseRule = mde.value;
			} else if(mde.name.equals("parser") && areEqual(mde.value, "IgnoreRest")){
				int pos = mde.getOffsetFromNoLength();
				if(expectedRemainingSource == null) {
					parsedSource = fullSource.substring(0, pos);
					expectedRemainingSource = fullSource.substring(pos);
					ignoreFurtherErrorMDs = true;
				}
			} else if(mde.name.equals("STRUCTURE_EXPECTED")) {
				assertTrue(expectedStructure == null);
				expectedStructure = parseExpectedStructure(mde.sourceValue);
			} else if(mde.name.equals("error") || mde.name.equals("ERROR")){
				if(ignoreFurtherErrorMDs) 
					continue;
				
				ParserError error = decodeError(lexSource, mde);
				expectedErrors.add(error);
				
				if(getErrorTypeFromMDE(mde) == ParserErrorTypes.INVALID_TOKEN_CHARACTERS) {
					errorCorrectionMetadata.add(new ParserErrorExt(error, null));
				} else if(getErrorTypeFromMDE(mde) == ParserErrorTypes.EXPECTED_TOKEN) {
					if(mde.sourceValue != null) {
						assertTrue(mde.sourceWasIncluded == false || mde.sourceValue.isEmpty());
					}
					errorCorrectionMetadata.add(new ParserErrorExt(error, mde.sourceValue));
				}
				
			} else if(mde.name.equals("parser") && areEqual(mde.value, "AllowAnyErrors")){
				allowAnyErrors = true;
			} else if(mde.name.equals("test") && areEqual(mde.value, "IGNORE_BREAK_CHECK")){
				expectedRemainingSource = DeeParserTest.DONT_CHECK;
			} else if(areEqual(mde.value, "test")){
				additionalMetadata.put(mde.name, mde);
			} else {
				if(areEqual(mde.value, "flag")) {
					additionalMetadata.put(mde.name, mde);
				} else if(!areEqual(mde.name, "comment")) {
					assertFail("Unknown metadata");
				}
			}
		}
		
		// Do error correction for toStringAsCode
		if(expectedPrintedSource == DEFAULT_VALUE) {
			expectedPrintedSource = calcExpectedToStringAsCode(parsedSource, errorCorrectionMetadata);
		}
		
		if(allowAnyErrors) {
			expectedErrors = null;
		}
		
		new DeeParserTest(fullSource).runParserTest______________________(parseRule, expectedRemainingSource, 
			expectedPrintedSource, expectedStructure, expectedErrors, unmodifiableMap(additionalMetadata));
	}
	
	public static String calcExpectedToStringAsCode(String parseSource, List<ParserErrorExt> errorInfo) {
		int modifyDelta = 0;
		
		String correctedParseSource = parseSource;
		for (ParserErrorExt error : errorInfo) {
			if(error.errorType == ParserErrorTypes.INVALID_TOKEN_CHARACTERS) {
				SourceRange sr = error.sourceRange;
				int offset = sr.getOffset() + modifyDelta;
				assertTrue(isValidStringRange(correctedParseSource, offset, sr.getLength()));
				correctedParseSource = removeRange(correctedParseSource, offset , sr.getLength());
				modifyDelta -= sr.getLength();
			}
			if(error.errorType == ParserErrorTypes.EXPECTED_TOKEN) {
				
				String rpl = error.sourceCorrectionForMissingTokens;
				if(rpl == null) {
					DeeTokens expectedToken = (DeeTokens) error.msgData;
					if(expectedToken.getSourceValue() == null) 
						continue;
					rpl = expectedToken.getSourceValue();
				}
				
				int offset = error.sourceRange.getEndPos() + modifyDelta;
				assertTrue(isValidStringRange(correctedParseSource, offset, 0));
				correctedParseSource = replaceRange(correctedParseSource, offset, 0, rpl);
				modifyDelta += rpl.length();
			}
		}
		return correctedParseSource;
	}
	
	protected static final Map<String,ParserErrorTypes> errorNameToType = NewUtils.initMap(
		Pair.create("ITC", ParserErrorTypes.INVALID_TOKEN_CHARACTERS),
		Pair.create("MT", ParserErrorTypes.MALFORMED_TOKEN),
		Pair.create("MTC", ParserErrorTypes.MALFORMED_TOKEN),
		Pair.create("EXP", ParserErrorTypes.EXPECTED_TOKEN),
		Pair.create("EXPRULE", ParserErrorTypes.EXPECTED_RULE),
		Pair.create("SE", ParserErrorTypes.SYNTAX_ERROR),
		Pair.create("<SE", ParserErrorTypes.SYNTAX_ERROR),
		Pair.create("BAD_LINKAGE_ID", ParserErrorTypes.INVALID_EXTERN_ID),
		Pair.create("REQPARENS", ParserErrorTypes.EXP_MUST_HAVE_PARENTHESES),
		Pair.create("TYPE_AS_EXP_VALUE", ParserErrorTypes.TYPE_USED_AS_EXP_VALUE),
		Pair.create("INV_QUALIFIER", ParserErrorTypes.INVALID_QUALIFIER),
		Pair.create("NO_TPL_SINGLE_ARG", ParserErrorTypes.NO_CHAINED_TPL_SINGLE_ARG)
	);
	
	public static ParserErrorTypes getErrorTypeFromMDE(MetadataEntry mde) {
		ParserErrorTypes result = errorNameToType.get(mde.value);
		if(result != null) {
			return result;
		}
		String errorType = StringUtil.upUntil(mde.value, "_");
		return assertNotNull_(errorNameToType.get(errorType));
	}
	
	public static ParserError decodeError(LexElementSource lexSource, MetadataEntry mde) {
		String errorTypeStr = StringUtil.upUntil(mde.value, "_");
		String errorParam = NewUtils.fromIndexOf("_", mde.value);
		
		SourceRange errorRange = mde.getSourceRange();
		String errorSource = null;
		
		ParserErrorTypes errorType = getErrorTypeFromMDE(mde);
		switch (errorType) {
		case INVALID_TOKEN_CHARACTERS:
			return new ParserError(ParserErrorTypes.INVALID_TOKEN_CHARACTERS, errorRange, mde.sourceValue, null);
		case MALFORMED_TOKEN:
			errorParam = DeeLexerSourceBasedTest.parseExpectedError(errorParam).toString();
			return createErrorToken(ParserErrorTypes.MALFORMED_TOKEN, mde, lexSource, true, errorParam);
		case EXPECTED_TOKEN:
			String expectedTokenStr = DeeLexerSourceBasedTest.transformTokenNameAliases(errorParam);
			DeeTokens expectedToken = DeeTokens.valueOf(expectedTokenStr);
			return createErrorToken(ParserErrorTypes.EXPECTED_TOKEN, mde, lexSource, true, expectedToken);
		case EXPECTED_RULE:
			errorParam = getExpectedRuleName(errorParam);
			return createErrorToken(ParserErrorTypes.EXPECTED_RULE, mde, lexSource, true, errorParam);
		case SYNTAX_ERROR:
			errorParam = getExpectedRuleName(errorParam);
			boolean tokenBefore = errorTypeStr.equals("<SE");
			return createErrorToken(ParserErrorTypes.SYNTAX_ERROR, mde, lexSource, tokenBefore, errorParam);
		case INVALID_EXTERN_ID:
			return createErrorToken(ParserErrorTypes.INVALID_EXTERN_ID, mde, lexSource, true, null);
		case EXP_MUST_HAVE_PARENTHESES: 
			errorParam = errorParam == null ? DeeParserTest.DONT_CHECK : errorParam;
			errorSource = assertNotNull_(mde.sourceValue);
			return new ParserError(errorType, errorRange, errorSource, errorParam);
		case TYPE_USED_AS_EXP_VALUE:
		case INVALID_QUALIFIER:
		case NO_CHAINED_TPL_SINGLE_ARG:
			errorSource = assertNotNull_(mde.sourceValue);
			return new ParserError(errorType, errorRange, errorSource, null);
		}
		throw assertFail();
	}
	
	public static ParserError createErrorToken(ParserErrorTypes errorTypeTk, MetadataEntry mde, 
		LexElementSource lexSource, boolean tokenBefore, Object errorParam) {
		Token lastToken = tokenBefore 
			? findLastEffectiveTokenBeforeOffset(mde.offset, lexSource)
			: findNextEffectiveTokenAfterOffset(mde.offset, lexSource);
			
		SourceRange errorRange = lastToken.getSourceRange();
		String errorSource = lastToken.source;
		return new ParserError(errorTypeTk, errorRange, errorSource, errorParam);
	}
	
	public static String getExpectedRuleName(String errorParam) {
		if(errorParam.equals("decl")) {
			errorParam = DeeParser.RULE_DECLARATION.name;
		} else if(errorParam.equals("exp")) {
			errorParam = DeeParser.RULE_EXPRESSION.name;
		} else if(errorParam.equals("ref")) {
			errorParam = DeeParser.RULE_REFERENCE.name;
		} else if(errorParam.equals("RoE")) {
			errorParam = DeeParser.RULE_TYPE_OR_EXP.name;
		} else if(errorParam.equals("TplArg")) {
			errorParam = DeeParser.RULE_TPL_SINGLE_ARG.name;
		}
		return errorParam;
	}
	
	public static Token findLastEffectiveTokenBeforeOffset(int offset, LexElementSource lexSource) {
		AbstractList<LexElement> lexElementList = lexSource.lexElementList;
		assertTrue(offset > 0 && offset <= lexElementList.get(lexElementList.size()-1).getEndPos());
		
		LexElement lastLexElement = LexElementSource.START_TOKEN;
		for (LexElement lexElement : lexElementList) {
			if(lexElement.getStartPos() >= offset)
				break;
			lastLexElement = lexElement;
		}
		return lastLexElement == null ? null : lastLexElement.token;
	}
	
	public static Token findNextEffectiveTokenAfterOffset(int offset, LexElementSource lexSource) {
		AbstractList<LexElement> lexElementList = lexSource.lexElementList;
		
		for (LexElement lexElement : lexElementList) {
			if(lexElement.isEOF()) {
				assertFail();
			}
			if(lexElement.getStartPos() >= offset) {
				return lexElement.token;
			}
		}
		throw assertFail();
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