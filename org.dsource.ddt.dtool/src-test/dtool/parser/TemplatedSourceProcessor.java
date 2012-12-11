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

import java.util.ArrayList;
import java.util.List;

import dtool.tests.CommonTestUtils;
import dtool.tests.SimpleParser;

/**
 * Generates multiple source cases from a templated source, using a simple markup language. 
 */
public class TemplatedSourceProcessor extends CommonTestUtils {
	
	public static void processSource(String unprocessedTestSource, String keyMARKER, ArrayList<String> testsCases) {
		TemplatedSourceProcessor.processSource(unprocessedTestSource, 0, keyMARKER, testsCases);
	}
	
	public static void processSource(String unprocessedSource, int offset, String keyMARKER,
		ArrayList<String> testCases) {
		SimpleParser testSourceParser = new SimpleParser(unprocessedSource);
		
		StringBuilder processedSource = new StringBuilder();
		processedSource.append(testSourceParser.consumeAmount(offset));
		
		while(true) {
			String sourceBeforeMarker = testSourceParser.consumeUntil(keyMARKER);
			if(testSourceParser.lookaheadIsEOF()) {
				testCases.add(processedSource + sourceBeforeMarker);
				return;
			}
			
			testSourceParser.consume(keyMARKER);
			processedSource.append(sourceBeforeMarker);
			
			if(testSourceParser.tryConsume(keyMARKER)) { //escaped keyMarker
				processedSource.append(keyMARKER);
			} else if(testSourceParser.tryConsumeKeyword("NL")) {
				List<String> variations = list("\r", "\n", "\r\n");
				expandSourceCases(processedSource, variations, testSourceParser, keyMARKER, testCases);
				return;
			} else if(testSourceParser.tryConsume("{")) {
				ArrayList<String> arguments = readArguments(keyMARKER, testSourceParser);
				assertTrue(!arguments.isEmpty(), "Need at least one argument");
				
				expandSourceCases(processedSource, arguments, testSourceParser, keyMARKER, testCases);
				return;
			}
		}
	}
	
	public static void expandSourceCases(StringBuilder processedSourceSB, Iterable<String> variations,
		SimpleParser testSourceParser, String keyMARKER, ArrayList<String> testCases) {
		for (String variation : variations) {
			String processedSource = processedSourceSB.toString();
			String fullSource = processedSource + variation + testSourceParser.restOfInput();
			processSource(fullSource, processedSource.length(), keyMARKER, testCases);
		}
	}
	
	public static ArrayList<String> readArguments(String keyMARKER, SimpleParser testSourceParser) {
		ArrayList<String> arguments = new ArrayList<String>();
		
		String argument = "";
		while(true) {
			final String[] ALTERNATIVES = {",", "}", keyMARKER};
			int alt = testSourceParser.consumeUntilAny(ALTERNATIVES);
			
			argument = argument + testSourceParser.getLastConsumedString();
			
			if(alt == 0) {
				arguments.add(argument);
				testSourceParser.consume(",");
				argument = "";
			} else if(alt == 1) {
				arguments.add(argument);
				testSourceParser.consume("}");
				break;
			} else if(alt == 2) {
				testSourceParser.consume(keyMARKER);
				
				if(testSourceParser.tryConsume(",")) {
					argument = argument + ",";
				} else if(testSourceParser.tryConsume("}")) {
					argument = argument + "}";
				} else if(testSourceParser.tryConsume(keyMARKER)) {
					argument = argument + keyMARKER + keyMARKER;
				} else {
					argument = argument + keyMARKER;
				}
				
			} else {
				assertFail("Reached EOF");
			}
		}
		
		return arguments;
	}
	
}