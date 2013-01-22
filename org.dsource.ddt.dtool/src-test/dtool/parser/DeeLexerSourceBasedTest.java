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

import static dtool.util.NewUtils.assertNotNull_;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dtool.parser.DeeLexerTest.TokenChecker;
import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.tests.DToolTestResources;

@RunWith(Parameterized.class)
public class DeeLexerSourceBasedTest extends DeeSourceBasedTest {
	
	protected static final String TESTFILESDIR = "dtool.parser/lexer-tests";
	
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
		for (AnnotatedSource testCase : getSourceBasedTests(file)) {
			MetadataEntry lexerTestMde = assertNotNull_(testCase.findMetadata("LEXERTEST"));
			runLexerSourceBasedTest(testCase.source, assertNotNull_(lexerTestMde.associatedSource));
		}
	}
	
	public void runLexerSourceBasedTest(String testSource, String expectedTokensList) {
		String[] expectedTokensStr = expectedTokensList.split("(,(\\\r?\\\n)?)\\s*");
		
		TokenChecker[] expectedTokens = new TokenChecker[expectedTokensStr.length];
		for (int i = 0; i < expectedTokensStr.length; i++) {
			expectedTokens[i] = TokenChecker.create(expectedTokensStr[i].trim());
		}
		
		DeeLexerTest.testLexerTokenizing(testSource, expectedTokens);
	}
	
}