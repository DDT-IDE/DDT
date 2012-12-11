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
import java.util.ArrayList;

import melnorme.utilbox.misc.ArrayUtil;
import dtool.tests.DToolBaseTest;
import dtool.tests.DToolTestResources;
import dtool.tests.SimpleParser;

public class DeeSourceBasedTest extends DToolBaseTest {
	
	private static final String KEY_SOURCE_TESTS = "//#SOURCE_TESTS ";
	private static final String KEY_SPLIT_SOURCE_TEST = "//#SPLIT_SOURCE_TEST";
	
	public String[] getSourceBasedTests(File file) {
		String[] sourceBasedTests = getSourceBasedTests(readStringFromFileUnchecked(file));
		testsLogger.println("-- " + getClass().getSimpleName() + 
			" on file: " + DToolTestResources.resourceFileToString(file) + " ("+sourceBasedTests.length+")");
		return sourceBasedTests;
	}
	
	public static String[] getSourceBasedTests(String fileSource) {
		
		int expectedTestCount = -1;
		String keywordMarker = null;
		
		SimpleParser simpleParser = new SimpleParser(fileSource);
		
		if(simpleParser.tryConsume(KEY_SOURCE_TESTS)) {
			expectedTestCount = simpleParser.consumeInteger();
			keywordMarker = simpleParser.consumeNonWhiteSpace();
			
			consumeToNewline(simpleParser);
		}
		
		ArrayList<String> testCases = new ArrayList<String>();
		
		do {
			simpleParser.consume(KEY_SPLIT_SOURCE_TEST);
			consumeToNewline(simpleParser);
			
			String unprocessedTestSource = simpleParser.consumeUntil(KEY_SPLIT_SOURCE_TEST);
			if(keywordMarker == null) {
				// dont process variations
				testCases.add(unprocessedTestSource);
			} else {
				TemplatedSourceProcessor.processSource(unprocessedTestSource, keywordMarker, testCases);
			}
			
		} while(simpleParser.lookAhead() != -1);
		
		assertTrue(expectedTestCount == -1 || testCases.size() == expectedTestCount);
		
		return ArrayUtil.createFrom(testCases, String.class);
	}
	
	public static void consumeToNewline(SimpleParser simpleParser) {
		assertTrue(simpleParser.seekToNewLine(), "No newline");
	}
	
}