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
import java.util.Arrays;
import java.util.List;

import dtool.tests.AnnotatedSource;
import dtool.tests.SimpleParser;
import dtool.tests.AnnotatedSource.MetadataEntry;

/**
 * Generates multiple source cases from a templated source, using a simple markup language. 
 */
public class TemplatedSourceProcessor {
	
	protected final ArrayList<AnnotatedSource> genCases = new ArrayList<AnnotatedSource>();
	protected String keyMARKER;
	
	public TemplatedSourceProcessor(String keyMARKER) {
		this.keyMARKER = keyMARKER;
	}
	
	public static TemplatedSourceProcessor processSource(String unprocessedSource, String keyMARKER) {
		TemplatedSourceProcessor tsp = new TemplatedSourceProcessor(keyMARKER);
		tsp.processSource(unprocessedSource);
		return tsp;
	}
	
	public ArrayList<AnnotatedSource> getGenCases() {
		return genCases;
	}
	
	public void processSource(String unprocessedSource) {
		processSourceVariations(unprocessedSource, 0);
	}
	
	public void processSourceVariations(String unprocessedSource, int offset) {
		SimpleParser sourceParser = new SimpleParser(unprocessedSource);
		
		sourceParser.consumeAmount(offset);
		
		while(true) {
			sourceParser.consumeUntil(keyMARKER);
			
			if(sourceParser.lookaheadIsEOF()) {
				// No variations
				processSourceLinearPhase(unprocessedSource);
				return;
			}
			int offsetBeforeMarker = sourceParser.getSourcePosition();
			sourceParser.consume(keyMARKER);
			
			if(sourceParser.tryConsume(keyMARKER)) { //escaped keyMarker
				// Reproduce the escaped marker, for second phase
			} else if(sourceParser.tryConsumeKeyword("EOF")) {
			} else if(sourceParser.tryConsumeKeyword("NL")) {
				List<String> variations = Arrays.asList("\r", "\n", "\r\n");
				expandSourceCases(sourceParser, offsetBeforeMarker, variations);
				return;
			} else if(sourceParser.tryConsume("{")) {
				processExpansionList(sourceParser, offsetBeforeMarker, "}");
				return;
			} else if(sourceParser.tryConsume("(")) {
				processExpansionList(sourceParser, offsetBeforeMarker, ")");
				return;
			} else if(sourceParser.tryConsume("@")) {
				//continue
			} else if(sourceParser.tryConsume("//")) {
				sourceParser.seekToNewLine();
				//continue
			} else {
				assertFail();
			}
		}
	}
	
	public void processExpansionList(SimpleParser sourceParser, int offsetBeforeMarker, String listEnd) {
		ArrayList<String> arguments = readArguments(sourceParser, listEnd);
		assertTrue(!arguments.isEmpty(), "Need at least one argument");
		
		expandSourceCases(sourceParser, offsetBeforeMarker, arguments);
	}
	
	public void expandSourceCases(SimpleParser sourceParser, int offsetBeforeMarker, Iterable<String> variations) {
		String sourceSoFar = sourceParser.getSource().substring(0, offsetBeforeMarker);
		
		for (String variation : variations) {
			String fullSource = sourceSoFar + variation + sourceParser.restOfInput();
			processSourceVariations(fullSource, sourceSoFar.length());
		}
	}
	
	public ArrayList<String> readArguments(SimpleParser testSourceParser, String listEnd) {
		ArrayList<String> arguments = new ArrayList<String>();
		
		String argument = "";
		while(true) {
			final String[] ALTERNATIVES = {",", listEnd, keyMARKER};
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
	
	public void processSourceLinearPhase(String unprocessedSource) {
		SimpleParser sourceParser = new SimpleParser(unprocessedSource);
		
		ArrayList<MetadataEntry> metadata = new ArrayList<MetadataEntry>();
		
		StringBuilder processedSource = new StringBuilder();
		
		boolean eofReached = false;
		
		while(true) {
			String sourceBeforeMarker = sourceParser.consumeUntil(keyMARKER);
			appendSource(processedSource, sourceBeforeMarker, eofReached);
			
			if(sourceParser.lookaheadIsEOF()) {
				addFullyProcessedSource(processedSource.toString(), metadata);
				return;
			}
			
			sourceParser.consume(keyMARKER);
			
			if(sourceParser.tryConsume(keyMARKER)) { //escaped keyMarker
				appendSource(processedSource, keyMARKER, eofReached);
			} else if(sourceParser.tryConsumeKeyword("EOF")) {
				eofReached = true;
			} else if(sourceParser.tryConsume("//")) {
				readMetaData(sourceParser, metadata, processedSource, eofReached, true);
			} else if(sourceParser.tryConsume("@")) {
				readMetaData(sourceParser, metadata, processedSource, eofReached, false);
			} else {
				assertFail();
			}
		}
	}
	
	public void readMetaData(SimpleParser sourceParser, ArrayList<MetadataEntry> metadata,
		StringBuilder processedSource, boolean eofReached, boolean endOfLineFormat) {
		
		String name = sourceParser.consumeAlphaNumericUS(false);
		String extraValue = null;
		if(sourceParser.tryConsume(":")) { 
			extraValue = sourceParser.consumeAlphaNumericUS(false);
		}
		String sourceValue = null;
		if(endOfLineFormat) {
			sourceParser.seekToNewLine();
			sourceValue = sourceParser.consumeRestOfInput();
		} else if(sourceParser.tryConsume("{")) {
			sourceValue = sourceParser.consumeUntil("}");
			sourceParser.consume("}");
		}
		
		int mdOffset = eofReached ? -1 : processedSource.length();
		MetadataEntry mde = new MetadataEntry(name, extraValue, sourceValue, mdOffset);
		metadata.add(mde);
		
		if(sourceValue != null) {
			appendSource(processedSource, sourceValue, eofReached);
		}
	}
	
	public void appendSource(StringBuilder sb, String string, boolean eofReached) {
		if(eofReached == false) {
			sb.append(string);
		}
	}
	
	protected void addFullyProcessedSource(String source, ArrayList<MetadataEntry> metadata) {
		genCases.add(new AnnotatedSource(source, metadata));
	}
	
}