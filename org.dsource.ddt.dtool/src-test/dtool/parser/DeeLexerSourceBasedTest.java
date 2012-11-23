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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dtool.tests.DToolBaseTest;
import dtool.tests.DToolTestResources;
import dtool.tests.MiscDeeTestUtils;

@RunWith(Parameterized.class)
public class DeeLexerSourceBasedTest extends DToolBaseTest {
	
	protected static final String TESTFILESDIR = "dtool.parser/lexer-tests";
	
	private static final int LEXER_SOURCE_BASED_TESTS_COUNT = 86;
	protected static int splitTestCount = 0; 
	
	@Parameters
	public static Collection<Object[]> filesToParse() throws IOException {
		return getTestFilesFromFolderAsParameterList(DToolTestResources.getTestResource(TESTFILESDIR));
	}
	
	protected static final String LEXERTEST_KEYWORD = "/+#LEXERTEST";
	protected final File file;
	
	public DeeLexerSourceBasedTest(File file) {
		this.file = file;
	}
	
	@Test
	public void runSourceBasedTests() throws IOException {
		String[] splitSourceBasedTests = enteringSourceBasedTest(file);
		for (String testString : splitSourceBasedTests) {
			splitTestCount++;
			runLexerSourceBasedTest(testString);
		}
	}
	
	@AfterClass
	public static void checkTestCount() {
		assertTrue(splitTestCount == LEXER_SOURCE_BASED_TESTS_COUNT);
	}
	
	public static final String ANY_UNTIL_NEWLINE_REGEX = "[^\\\r\\\n]*\\\r?\\\n";
	
	
	public void runLexerSourceBasedTest(String testSource) {
		int lexerSourceEnd = testSource.indexOf(LEXERTEST_KEYWORD);
		assertTrue(lexerSourceEnd != -1);
		int index = lexerSourceEnd + LEXERTEST_KEYWORD.length();
		
		Matcher matcher = MiscDeeTestUtils.matchRegexp(ANY_UNTIL_NEWLINE_REGEX, testSource, index);
		String expectedTokensList = testSource.substring(matcher.end());
		expectedTokensList = expectedTokensList.replaceFirst("(\\\r?\\\n)?\\+/(?s).*", "");
		
		String[] expectedTokensStr = expectedTokensList.split("(,(\\\r?\\\n)?)");
		
		DeeTokens[] expectedTokens = new DeeTokens[expectedTokensStr.length];
		for (int i = 0; i < expectedTokensStr.length; i++) {
			String expectedTokenStr = expectedTokensStr[i].trim();
			try {
				if(expectedTokenStr.equals("*")) {
					expectedTokens[i] = null;
				} else {
					if(expectedTokenStr.equals("ID")) {
						expectedTokenStr = DeeTokens.IDENTIFIER.name();
					} else if(expectedTokenStr.equals("WS") || expectedTokenStr.equals("_")) {
						expectedTokenStr = DeeTokens.WHITESPACE.name();
					}
					DeeTokens expectedToken = DeeTokens.valueOf(expectedTokenStr);
					expectedTokens[i] = expectedToken;
				}
			} catch(IllegalArgumentException e) {
				assertFail();
			}
		}
		
		String source = testSource.substring(0, lexerSourceEnd);
		DeeLexerTest.testLexerTokenizing(source, expectedTokens);
	}
	
}