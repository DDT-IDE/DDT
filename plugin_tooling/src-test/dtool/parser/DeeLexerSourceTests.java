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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dtool.parser.common.AbstractLexerTest.TokenChecker;
import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;

@RunWith(Parameterized.class)
public class DeeLexerSourceTests extends CommonTemplatedSourceBasedTest {
	
	protected static final String TESTFILESDIR = "lexer";
	
	@Parameters(name="{index}: {0}")
	public static Collection<Object[]> testFilesList() throws IOException {
		return createTestFileParameters(TESTFILESDIR, null);
	}
	
	public DeeLexerSourceTests(String testDescription, File file) {
		super(testDescription, file);
	}
	
	@Test
	public void runLexerSourceBaseTests() throws Exception { runLexerSourceBaseTests$(); }
	public void runLexerSourceBaseTests$() throws Exception {
		for (AnnotatedSource testCase : getTestCasesFromFile(null)) {
			runAnnotatedSourceTest(testCase);
		}
	}
	
	@Override
	protected void runAnnotatedSourceTest(AnnotatedSource testCase) {
		MetadataEntry lexerTestMde = assertNotNull(testCase.findMetadata("LEXERTEST"));
		String expectedTokenList = assertNotNull(lexerTestMde.sourceValue);
		runLexerSourceBasedTest(testCase.source, expectedTokenList);
	}
	
	public void runLexerSourceBasedTest(String testSource, String expectedTokensList) {
		String[] expectedTokensStr = expectedTokensList.split("(,(\\\r?\\\n)?)\\s*");
		
		TokenChecker[] expectedTokens = new TokenChecker[expectedTokensStr.length];
		for (int i = 0; i < expectedTokensStr.length; i++) {
			expectedTokens[i] = createTokenChecker(expectedTokensStr[i].trim());
		}
		
		DeeLexerTest.testLexerTokenizing(testSource, expectedTokens);
	}
	
	public static TokenChecker createTokenChecker(String expectedTokenStr) {
		try {
			DeeLexerErrors expectedError = null;
			
			int errorMark = expectedTokenStr.indexOf('!');
			if(errorMark != -1) {
				expectedError = parseExpectedError(expectedTokenStr.substring(errorMark+1));
				expectedTokenStr = expectedTokenStr.substring(0, errorMark);
			}
			
			if(expectedTokenStr.equals("*")) {
				return new TokenChecker(null, expectedError);
			} else {
				DeeTokens expectedToken = DeeTokens.valueOf(transformTokenNameAliases(expectedTokenStr));
				return new TokenChecker(expectedToken, expectedError);
			}
		} catch(IllegalArgumentException e) {
			throw assertFail();
		}
	}
	
	public static String transformTokenNameAliases(String expectedTokenName) {
		if(expectedTokenName.equals("EOL")) {
			return DeeTokens.LINE_END.name();
		} else if(expectedTokenName.equals("ID")) {
			return DeeTokens.IDENTIFIER.name();
		} else if(expectedTokenName.equals("WS") || expectedTokenName.equals("_")) {
			return DeeTokens.WHITESPACE.name();
		} else if(expectedTokenName.equals("ERROR")) {
			return DeeTokens.INVALID_TOKEN.name();
		} else if(expectedTokenName.equals("CHAR_LITERAL")) {
			return DeeTokens.CHARACTER.name();
		} else if(expectedTokenName.equals("FLOAT_DEC")) {
			return DeeTokens.FLOAT_DECIMAL.name();
		}
		return expectedTokenName;
	}
	
	protected static final Map<String, DeeLexerErrors> strToErrorType = new HashMap<String, DeeLexerErrors>();
	
	static {
		for (DeeLexerErrors lexerErrorType : DeeLexerErrors.values()) {
			switch (lexerErrorType) {
			case INVALID_CHARACTERS: strToErrorType.put("xC", lexerErrorType); break;
			
			case COMMENT_NOT_TERMINATED: strToErrorType.put("Cx", lexerErrorType); break;
			case COMMENTNESTED_NOT_TERMINATED: strToErrorType.put("CNx", lexerErrorType); break;
			
			case STRING_NOT_TERMINATED__REACHED_EOF: strToErrorType.put("Sx", lexerErrorType); break;
			case STRING_DELIM_NO_DELIMETER: strToErrorType.put("SDxD", lexerErrorType); break;
			case STRING_DELIM_NOT_PROPERLY_TERMINATED: strToErrorType.put("SDx", lexerErrorType); break;
			case STRING_DELIM_ID_NOT_PROPERLY_FORMED: strToErrorType.put("SDxID", lexerErrorType); break;
			
			case CHAR_LITERAL_NOT_TERMINATED__REACHED_EOF: strToErrorType.put("CHxF", lexerErrorType); break;
			case CHAR_LITERAL_NOT_TERMINATED__REACHED_EOL: strToErrorType.put("CHxL", lexerErrorType); break;
			case CHAR_LITERAL_EMPTY: strToErrorType.put("CHx0", lexerErrorType); break;
			case CHAR_LITERAL_SIZE_GREATER_THAN_ONE: strToErrorType.put("CH_L", lexerErrorType); break;
			
			case INT_LITERAL_BINARY__INVALID_DIGITS: strToErrorType.put("IBx", lexerErrorType); break;
			case INT_LITERAL_OCTAL__INVALID_DIGITS: strToErrorType.put("IOx", lexerErrorType); break;
			case INT_LITERAL__HAS_NO_DIGITS: strToErrorType.put("Ix", lexerErrorType); break;
			
			case FLOAT_LITERAL__EXP_HAS_NO_DIGITS: strToErrorType.put("FxD", lexerErrorType); break;
			case FLOAT_LITERAL__HEX_HAS_NO_EXP: strToErrorType.put("FxE", lexerErrorType); break;
			
			case SPECIAL_TOKEN_LINE_BAD_FORMAT: strToErrorType.put("STLx", lexerErrorType); break;
			case SPECIAL_TOKEN_INVALID: strToErrorType.put("STx", lexerErrorType); break;
			
			}
		}
	}
	
	public static DeeLexerErrors parseExpectedError(String string) {
		assertTrue(!string.isEmpty());
		return assertNotNull(strToErrorType.get(string));
	}
	
}