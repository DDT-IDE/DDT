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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dtool.parser.DeeLexerTest.TokenChecker;
import dtool.tests.AnnotatedSource;
import dtool.tests.DToolTestResources;
import dtool.tests.SimpleParser;

@RunWith(Parameterized.class)
public class DeeLexerSourceBasedTest extends DeeSourceBasedTest {
	
	protected static final String TESTFILESDIR = "dtool.parser/lexer-tests";
	
	private static final int LEXER_SOURCE_BASED_TESTS_COUNT = 118;
	private static int splitTestCount = 0;
	
	@Parameters
	public static Collection<Object[]> filesToParse() throws IOException {
		return getTestFilesFromFolderAsParameterList(DToolTestResources.getTestResource(TESTFILESDIR));
	}
	
	protected final File file;
	
	public DeeLexerSourceBasedTest(File file) {
		this.file = file;
	}
	
	@Test
	public void runSourceBasedTests() throws IOException {
		for (AnnotatedSource testString : getSourceBasedTests(file)) {
			splitTestCount++;
			runLexerSourceBasedTest(testString.source);
		}
	}
	
	@AfterClass
	public static void checkTestCount() {
		assertTrue(splitTestCount == LEXER_SOURCE_BASED_TESTS_COUNT);
	}
	
	protected static final String LEXERTEST_KEYWORD = "/+#LEXERTEST";
	
	
	public void runLexerSourceBasedTest(String testSource) {
		SimpleParser simpleParser = new SimpleParser(testSource);
		
		String source = simpleParser.consumeUntil(LEXERTEST_KEYWORD);
		
		assertTrue(!simpleParser.lookaheadIsEOF());
		
		simpleParser.seekToNewLine();
		String expectedTokensList = simpleParser.restOfInput();
		expectedTokensList = expectedTokensList.replaceFirst("(\\\r?\\\n)?\\+/(?s).*", "");
		
		String[] expectedTokensStr = expectedTokensList.split("(,(\\\r?\\\n)?)");
		
		TokenChecker[] expectedTokens = new TokenChecker[expectedTokensStr.length];
		for (int i = 0; i < expectedTokensStr.length; i++) {
			expectedTokens[i] = TokenChecker.create(expectedTokensStr[i].trim());
		}
		
		DeeLexerTest.testLexerTokenizing(source, expectedTokens);
	}
	
}