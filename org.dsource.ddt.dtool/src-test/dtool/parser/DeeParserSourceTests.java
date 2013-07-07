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

import static dtool.util.NewUtils.isValidStringRange;
import static dtool.util.NewUtils.replaceRange;
import static dtool.util.NewUtils.substringRemoveEnd;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.io.File;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.Pair;
import melnorme.utilbox.misc.StringUtil;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dtool.ast.SourceRange;
import dtool.parser.DeeParserTester.NamedNodeElement;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.sourcegen.TemplateSourceProcessorParser.TspExpansionElement;
import dtool.tests.SimpleParser;
import dtool.util.NewUtils;

@RunWith(Parameterized.class)
public class DeeParserSourceTests extends DeeTemplatedSourceBasedTest {
	
	public static final String TESTFILESDIR = "parser";
	
	protected static Map<String, TspExpansionElement> commonDefinitions = new HashMap<>();
	
	@BeforeClass
	public static void initCommonDefinitions() throws IOException {
		addCommonDefinitions(DeeParserSourceTests.class, TESTFILESDIR, commonDefinitions);
	}
	
	@Parameters(name="{index}: {0}")
	public static Collection<Object[]> testFilesList() throws IOException {
		return createTestFileParameters(TESTFILESDIR);
	}
	
	public DeeParserSourceTests(String testUIDescription, File file) {
		super(testUIDescription, file);
	}
	
	@Test
	public void runSourceBasedTests() throws Exception { runSourceBasedTests$(); }
	public void runSourceBasedTests$() throws Exception {
		runAnnotatedTests(getTestCasesFromFile(commonDefinitions));
	}
	
	@Override
	public void runAnnotatedSourceTest(AnnotatedSource testSource) {
		final String DEFAULT_VALUE = "##DEFAULT VALUE";
		
		String fullSource = testSource.source;
		final LexElementSource lexSource = new DeeParser(fullSource).lexSource;
		
		String expectedParsedSource = fullSource;
		String expectedRemainingSource = null;
		String parseRule = null;
		
		String expectedPrintedSource = DEFAULT_VALUE;
		NamedNodeElement[] expectedStructure = null;
		boolean allowAnyErrors = false;
		boolean ignoreFurtherErrorMDs = false;
		
		ArrayList<ParserError> expectedErrors = new ArrayList<>();
		List<MetadataEntry> additionalMetadata = new ArrayList<>();
		List<StringCorrection> errorCorrectionMetadata = new ArrayList<>();
		
		for (MetadataEntry mde : testSource.metadata) {
			if(mde.name.equals("AST_SOURCE_EXPECTED")) {
				assertTrue(expectedPrintedSource == DEFAULT_VALUE);
				if(areEqual(mde.value, "NoCheck")) {
					expectedPrintedSource = null;
				} else {
					expectedPrintedSource = assertNotNull(mde.sourceValue);
				}
				ignoreFurtherErrorMDs = true;
			} else if(mde.name.equals("PARSE")){
				parseRule = mde.value;
			} else if(mde.name.equals("parser") && areEqual(mde.value, "CutRest")){
				int pos = assertNoLength(mde).offset;
				
				if(expectedRemainingSource == null) {
					fullSource = fullSource.substring(0, pos);
					expectedParsedSource = fullSource;
					expectedRemainingSource = "";
				} else {
					int endLengthToRemove = fullSource.length() - pos;
					fullSource = fullSource.substring(0, pos);
					expectedRemainingSource = substringRemoveEnd(expectedRemainingSource, endLengthToRemove);
				}
				ignoreFurtherErrorMDs = true;
				
			} else if(mde.name.equals("parser") && areEqual(mde.value, "IgnoreRest")){
				int pos = assertNoLength(mde).offset;
				if(expectedRemainingSource == null) {
					expectedParsedSource = fullSource.substring(0, pos);
					expectedRemainingSource = fullSource.substring(pos);
					ignoreFurtherErrorMDs = true;
				}
			} else if(mde.name.equals("STRUCTURE_EXPECTED")) {
				assertTrue(expectedStructure == null);
				expectedStructure = parseExpectedStructure(mde.sourceValue);
			} else if(mde.name.equals("error") || mde.name.equals("ERROR")){
				if(areEqual(mde.value, "-none-")) {
					int offset = ignoreFurtherErrorMDs ? expectedParsedSource.length() : mde.offset;
						
					errorCorrectionMetadata.add(new StringCorrection(offset, 0, mde.sourceValue));
					continue;
				}
				if(ignoreFurtherErrorMDs) 
					continue;
				
				ParserError error = decodeError(lexSource, mde);
				expectedErrors.add(error);
				
				if(getErrorTypeFromMDE(mde) == ParserErrorTypes.INVALID_TOKEN_CHARACTERS) {
					SourceRange sr = error.sourceRange;
					errorCorrectionMetadata.add(new StringCorrection(sr.getOffset(), sr.getLength(), ""));
				} else if(getErrorTypeFromMDE(mde) == ParserErrorTypes.EXPECTED_TOKEN) {
					assertTrue(mde.sourceValue == null || mde.sourceValue.isEmpty() || !mde.sourceWasIncluded);
					
					String rpl = mde.sourceValue;
					if(rpl == null) {
						DeeTokens expectedToken = (DeeTokens) error.msgData;
						if(!expectedToken.hasSourceValue()) 
							continue;
						rpl = expectedToken.getSourceValue();
					}
					
					errorCorrectionMetadata.add(new StringCorrection(error.sourceRange.getEndPos(), 0, rpl));
				}
				
			} else if(mde.name.equals("parser") && areEqual(mde.value, "AllowAnyErrors")){
				allowAnyErrors = true;
			} else if(mde.name.equals("test") && areEqual(mde.value, "IGNORE_BREAK_CHECK")){
				expectedRemainingSource = DeeParserTester.DONT_CHECK;
			} else if(areEqual(mde.value, "test")){
				additionalMetadata.add(mde);
			} else {
				if(!(areEqual(mde.value, "flag") || areEqual(mde.name, "comment"))) {
					assertFail("Unknown metadata");
				}
			}
		}
		
		// Do error correction for toStringAsCode
		if(expectedPrintedSource == DEFAULT_VALUE) {
			expectedPrintedSource = calcExpectedToStringAsCode(expectedParsedSource, errorCorrectionMetadata);
		}
		
		if(allowAnyErrors) {
			expectedErrors = null;
		}
		
		new DeeParserTester(fullSource, parseRule, expectedRemainingSource, expectedPrintedSource, expectedStructure, 
			expectedErrors, additionalMetadata).runParserTest______________________();
	}
	
	public static MetadataEntry assertNoLength(MetadataEntry mde) {
		assertTrue(mde.offset >= 0);
		assertTrue(mde.sourceValue == null);
		return mde;
	}
	
	public static class StringCorrection {
		public String rpl;
		public int offset;
		public int length;
		
		public StringCorrection(int offset, int length, String rpl) {
			this.rpl = rpl;
			this.offset = offset;
			this.length = length;
		}
		
	}
	
	public static String calcExpectedToStringAsCode(String parseSource, List<StringCorrection> errorCorrections) {
		int modifyDelta = 0;
		
		String correctedParseSource = parseSource;
		for (StringCorrection sc : errorCorrections) {
			
			int offset = sc.offset + modifyDelta;
			assertTrue(isValidStringRange(correctedParseSource, offset, sc.length));
			correctedParseSource = replaceRange(correctedParseSource, offset , sc.length, sc.rpl);
			modifyDelta += sc.rpl.length() - sc.length; // can be negative
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
		Pair.create("REQPARENS", ParserErrorTypes.EXP_MUST_HAVE_PARENTHESES),
		Pair.create("TYPE_AS_EXP_VALUE", ParserErrorTypes.TYPE_USED_AS_EXP_VALUE),
		Pair.create("NO_TPL_SINGLE_ARG", ParserErrorTypes.NO_CHAINED_TPL_SINGLE_ARG),
		Pair.create("BAD_LINKAGE_ID", ParserErrorTypes.INVALID_EXTERN_ID)
	);
	
	public static ParserErrorTypes getErrorTypeFromMDE(MetadataEntry mde) {
		try {
			return ParserErrorTypes.valueOf(mde.value);
		} catch (IllegalArgumentException e) {
			// continue
		}
		
		ParserErrorTypes result = errorNameToType.get(mde.value);
		if(result != null) {
			return result;
		}
		String errorType = StringUtil.substringUntilMatch(mde.value, "_");
		return assertNotNull(errorNameToType.get(errorType));
	}
	
	public static ParserError decodeError(LexElementSource lexSource, MetadataEntry mde) {
		String errorTypeStr = StringUtil.substringUntilMatch(mde.value, "_");
		String errorParam = StringUtil.segmentAfterMatch(mde.value, "_");
		
		SourceRange errorRange = mde.getSourceRange();
		String errorSource = null;
		
		ParserErrorTypes errorType = getErrorTypeFromMDE(mde);
		switch (errorType) {
		case INVALID_TOKEN_CHARACTERS:
			return new ParserError(ParserErrorTypes.INVALID_TOKEN_CHARACTERS, errorRange, mde.sourceValue, null);
		case MALFORMED_TOKEN:
			errorParam = DeeLexerSourceTests.parseExpectedError(errorParam).toString();
			return createErrorToken(ParserErrorTypes.MALFORMED_TOKEN, mde, lexSource, true, errorParam);
		case EXPECTED_TOKEN:
			String expectedTokenStr = DeeLexerSourceTests.transformTokenNameAliases(errorParam);
			DeeTokens expectedToken = DeeTokens.valueOf(expectedTokenStr);
			return createErrorToken(ParserErrorTypes.EXPECTED_TOKEN, mde, lexSource, true, expectedToken);
		case EXPECTED_RULE:
			errorParam = getExpectedRuleDescription(errorParam);
			return createErrorToken(ParserErrorTypes.EXPECTED_RULE, mde, lexSource, true, errorParam);
		case SYNTAX_ERROR:
			errorParam = getExpectedRuleDescription(errorParam);
			boolean tokenBefore = errorTypeStr.equals("<SE");
			return createErrorToken(ParserErrorTypes.SYNTAX_ERROR, mde, lexSource, tokenBefore, errorParam);
		case EXP_MUST_HAVE_PARENTHESES: 
			errorParam = errorParam == null ? DeeParserTester.DONT_CHECK : errorParam;
			errorSource = assertNotNull(mde.sourceValue);
			return new ParserError(errorType, errorRange, errorSource, errorParam);
		case TYPE_USED_AS_EXP_VALUE:
		case INIT_USED_IN_EXP:
		case NO_CHAINED_TPL_SINGLE_ARG:
			errorSource = assertNotNull(mde.sourceValue);
			return new ParserError(errorType, errorRange, errorSource, null);
		case INVALID_EXTERN_ID:
		case INVALID_SCOPE_ID:
		case INVALID_TRAITS_ID:
			if(mde.sourceValue != null) {
				return new ParserError(errorType, errorRange, mde.sourceValue, null);
			}
			return createErrorToken(errorType, mde, lexSource, true, null);
		case LAST_CATCH:
			return createErrorToken(ParserErrorTypes.LAST_CATCH, mde, lexSource, false, null);
		}
		throw assertFail();
	}
	
	public static ParserError createErrorToken(ParserErrorTypes errorTypeTk, MetadataEntry mde, 
		LexElementSource lexSource, boolean tokenBefore, Object errorParam) {
		IToken adjacentToken = tokenBefore 
			? findLastEffectiveTokenBeforeOffset(mde.offset, lexSource)
			: findNextEffectiveTokenAfterOffset(mde.offset, lexSource);
			
		SourceRange errorRange = adjacentToken.getSourceRange();
		String errorSource = adjacentToken.getSourceValue();
		return new ParserError(errorTypeTk, errorRange, errorSource, errorParam);
	}
	
	public static String getExpectedRuleDescription(String ruleId) {
		if(ruleId.equals("decl")) {
			return DeeParser.RULE_DECLARATION.description;
		} else if(ruleId.equals("exp")) {
			return DeeParser.RULE_EXPRESSION.description;
		} else if(ruleId.equals("ref")) {
			return DeeParser.RULE_REFERENCE.description;
		}
		
		assertTrue(DeeParser.getRule(ruleId) != null);
		return DeeParser.getRule(ruleId).description;
	}
	
	public static LexElement findLastEffectiveTokenBeforeOffset(int offset, LexElementSource lexSource) {
		AbstractList<LexElement> lexElementList = lexSource.lexElementList;
		assertTrue(offset > 0 && offset <= lexElementList.get(lexElementList.size()-1).getEndPos());
		
		LexElement lastLexElement = LexElementSource.START_TOKEN;
		for (LexElement lexElement : lexElementList) {
			if(lexElement.getStartPos() >= offset)
				break;
			lastLexElement = lexElement;
		}
		return lastLexElement == null ? null : lastLexElement;
	}
	
	public static LexElement findNextEffectiveTokenAfterOffset(int offset, LexElementSource lexSource) {
		AbstractList<LexElement> lexElementList = lexSource.lexElementList;
		
		for (LexElement lexElement : lexElementList) {
			if(lexElement.isEOF()) {
				assertFail();
			}
			if(lexElement.getStartPos() >= offset) {
				return lexElement;
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