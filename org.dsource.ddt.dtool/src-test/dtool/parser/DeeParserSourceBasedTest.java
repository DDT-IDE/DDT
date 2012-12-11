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

import dtool.tests.DToolTestResources;
import dtool.tests.SimpleParser;

@RunWith(Parameterized.class)
public class DeeParserSourceBasedTest extends DeeSourceBasedTest {
	
	protected static final String TESTFILESDIR = "dtool.parser/parser-tests";
	
	private static final int PARSER_SOURCE_BASED_TESTS_COUNT = 5;
	private static int splitTestCount = 0;
	
	@Parameters
	public static Collection<Object[]> filesToParse() throws IOException {
		return getTestFilesFromFolderAsParameterList(DToolTestResources.getTestResource(TESTFILESDIR));
	}
	
	protected final File file;
	
	public DeeParserSourceBasedTest(File file) {
		this.file = file;
	}
	
	@Test
	public void runSourceBasedTests() throws IOException {
		String[] splitSourceBasedTests = getSourceBasedTests(file);
		for (String testString : splitSourceBasedTests) {
			splitTestCount++;
			runSourceBasedTest(testString);
		}
	}
	
	@AfterClass
	public static void checkTestCount() {
		assertTrue(splitTestCount == PARSER_SOURCE_BASED_TESTS_COUNT);
	}
	
	protected static final String TEST_KEYWORD = "//#PARSERTEST#";
	
	public void runSourceBasedTest(String testSource) {
		SimpleParser simpleParser = new SimpleParser(testSource);
		
		String parseSource = simpleParser.consumeUntil(TEST_KEYWORD);
		String expectedGenSource = parseSource;
		
		if(!simpleParser.lookaheadIsEOF()) {
			simpleParser.seekToNewLine();
			expectedGenSource = simpleParser.restOfInput(); 
		}
		
		DeeParserTest.runParserTest(parseSource, expectedGenSource);
	}
	
}