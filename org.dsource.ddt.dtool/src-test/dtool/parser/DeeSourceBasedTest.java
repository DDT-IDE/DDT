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
import java.util.ArrayList;

import melnorme.utilbox.misc.ArrayUtil;
import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.TemplatedSourceProcessor2;
import dtool.tests.DToolBaseTest;
import dtool.tests.DToolTestResources;
import dtool.tests.SimpleParser;

public class DeeSourceBasedTest extends DToolBaseTest {
	
	private static final String KEY_SOURCE_TESTS = "//#SOURCE_TESTS ";
	private static final String KEY_SPLIT_SOURCE_TEST = "//#SPLIT_SOURCE_TEST";
	
	public AnnotatedSource[] getSourceBasedTests(File file) {
		AnnotatedSource[] sourceBasedTests = getSourceBasedTestCases(readStringFromFileUnchecked(file));
		testsLogger.println("-- " + getClass().getSimpleName() + 
			" on file: " + DToolTestResources.resourceFileToString(file) + " ("+sourceBasedTests.length+")");
		return sourceBasedTests;
	}
	
	public static AnnotatedSource[] getSourceBasedTestCases(String fileSource) {
		if(!fileSource.startsWith(KEY_SOURCE_TESTS)) {
			TemplatedSourceProcessor2 tsp = new TemplatedSourceProcessor2() {
				@Override
				protected void reportError(int offset) throws TemplatedSourceException {
					assertFail();
				}
			};
			return tsp.processSource_unchecked("#", fileSource);
		}
		
		int expectedTestCount = -1;
		String keywordMarker = null;
		
		SimpleParser simpleParser = new SimpleParser(fileSource);
		
		if(simpleParser.tryConsume(KEY_SOURCE_TESTS)) {
			simpleParser.seekSpaceChars();
			if(Character.isDigit(simpleParser.lookAhead())) {
				expectedTestCount = simpleParser.consumeInteger(true);
				keywordMarker = simpleParser.consumeNonWhiteSpace(true);
			}
			
			consumeToNewline(simpleParser);
		}
		
		ArrayList<AnnotatedSource> testCases = new ArrayList<AnnotatedSource>();
		
		do {
			simpleParser.consume(KEY_SPLIT_SOURCE_TEST);
			consumeToNewline(simpleParser);
			
			String unprocessedTestSource = simpleParser.consumeUntil(KEY_SPLIT_SOURCE_TEST);
			if(keywordMarker == null) {
				// dont process variations
				testCases.add(new AnnotatedSource(unprocessedTestSource));
			} else {
				testCases.addAll(
					TemplatedSourceProcessor.processSource(unprocessedTestSource, keywordMarker).getGenCases());
			}
			
		} while(simpleParser.lookAhead() != -1);
		
		assertTrue(expectedTestCount == -1 || testCases.size() == expectedTestCount);
		
		return ArrayUtil.createFrom(testCases, AnnotatedSource.class);
	}
	
	public static void consumeToNewline(SimpleParser simpleParser) {
		assertTrue(simpleParser.seekToNewLine(), "No newline");
	}
	
}