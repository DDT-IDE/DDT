/*******************************************************************************
 * Copyright (c) 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.sourcegen;

import static dtool.util.NewUtils.assertNotNull_;
import static dtool.util.NewUtils.emptyToNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.misc2.ChainedIterator2;
import melnorme.utilbox.misc2.CopyableListIterator;
import melnorme.utilbox.misc2.ICopyableIterator;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.tests.SimpleParser;

/**
 * Generates multiple source cases from a templated source, using an embedded markup language. 
 */
public class TemplatedSourceProcessor2 {
	
	@SuppressWarnings("serial")
	public class TemplatedSourceException extends Exception {
		public int errorOffset;
		public TemplatedSourceException(int errorOffset) {
			this.errorOffset = errorOffset;
		}
	}
	
	public static AnnotatedSource[] processTemplatedSource(String marker, String source) 
		throws TemplatedSourceException {
		TemplatedSourceProcessor2 tsp = new TemplatedSourceProcessor2();
		return tsp.processSource(marker, source);
	}
	
	protected String kMARKER;
	protected String[] kMARKER_array;
	protected final Map<String, TspExpansionElement> globalExpansions = new HashMap<String, TspExpansionElement>();
	protected final ArrayList<AnnotatedSource> genCases = new ArrayList<AnnotatedSource>();
	
	public TemplatedSourceProcessor2() { }
	
	public Map<String, TspExpansionElement> getGlobalExpansions() {
		return globalExpansions;
	}
	
	public void addGlobalExpansions(Map<String, TspExpansionElement> newGlobalExpansions) {
		for (Entry<String, TspExpansionElement> entry : newGlobalExpansions.entrySet()) {
			assertTrue(globalExpansions.containsKey(entry.getKey()) == false);
			globalExpansions.put(entry.getKey(), entry.getValue());
		}
	}
	
	public ArrayList<AnnotatedSource> getGenCases() {
		return genCases;
	}
	
	public AnnotatedSource[] processSource_unchecked(String defaultMarker, String unprocessedSource) {
		try {
			return processSource(defaultMarker, unprocessedSource);
		} catch(TemplatedSourceException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	public AnnotatedSource[] processSource(String defaultMarker, String fileSource) 
		throws TemplatedSourceException {
		
		SimpleParser parser = new SimpleParser(fileSource);
		
		final String[] splitKeywords = { "#:HEADER", "Ⓗ", "#:SPLIT", "━━", "▂▂", "▃▃"};
		
		do {
			boolean isHeader = false;
			String keyMarker = defaultMarker;
			
			int alt = parser.tryConsume(splitKeywords);
			if(alt != SimpleParser.EOF) {
				if(alt == 0 || alt == 1) {
					isHeader = true;
				}
				checkError(parser.seekToNewLine() == false, parser);
				Matcher matcher = Pattern.compile("→(.)").matcher(parser.getLastConsumedString());
				if(matcher.find()) {
					keyMarker = matcher.group(1);
				}
			} else {
				assertTrue(parser.getSourcePosition() == 0);
			}
			
			parser.consumeUntilAny(splitKeywords);
			
			String unprocessedCaseSource = parser.getLastConsumedString();
			processSplitCaseSource(unprocessedCaseSource, isHeader, keyMarker);
		} while(!parser.lookaheadIsEOF());
		
		return ArrayUtil.createFrom(getGenCases(), AnnotatedSource.class);
	}
	
	protected ArrayList<TspElement> parseSource(String unprocessedSource) throws TemplatedSourceException {
		ArrayList<TspElement> unprocessedSourceElements = new ArrayList<TspElement>(); 
		
		SimpleParser parser = new SimpleParser(unprocessedSource);
		while(true) {
			TspElement tspElem = parseElement(parser);
			if(tspElem == null) {
				break;
			}
			unprocessedSourceElements.add(tspElem);
		}
		return unprocessedSourceElements;
	}
	
	protected static abstract class TspElement {
		public String getElementType() { return null; };
	}
	
	protected class TspStringElement extends TspElement {
		public static final String RAW_TEXT = "TextElement";
		public final String producedText;
		public final String elemType;
		
		protected TspStringElement(String source) {
			this(source, RAW_TEXT);
		}
		protected TspStringElement(String producedText, String elemType) {
			this.producedText = assertNotNull_(producedText);
			this.elemType = elemType;
		}
		@Override
		public String getElementType() {
			return elemType;
		}
		@Override
		public String toString() {
			return producedText;
		}
	}
	
	protected class TspCommand extends TspElement {
		public final String command;
		
		protected TspCommand(String command) {
			this.command = assertNotNull_(command);
		}
	}
	
	protected TspElement parseElement(SimpleParser parser) throws TemplatedSourceException {
		return parseElementWithCustomStarts(parser, kMARKER_array);
	}
	
	protected TspElement parseElementWithCustomStarts(SimpleParser parser, String... tokenStarts)
		throws TemplatedSourceException {
		if(parser.lookaheadIsEOF()) {
			return null;
		}
		
		int alt = parser.consumeUntilAny(tokenStarts);
		final String string = parser.getLastConsumedString();
		
		if(!string.isEmpty()) {
			return new TspStringElement(string);
		}
		
		String tokenStart = tokenStarts[alt];
		parser.consume(tokenStart);
		
		if(!tokenStart.equals(kMARKER)) {
			return new TspStringElement(parser.getLastConsumedString(), tokenStart);
		}
		
		if(parser.tryConsume(kMARKER)) {
			return new TspStringElement(kMARKER);
		}
		for (String escapableTokenStart : tokenStarts) {
			if(parser.tryConsume(escapableTokenStart)) {
				return new TspStringElement(escapableTokenStart);
			}
		}
		if(parser.lookAhead() == '{' || parser.lookAhead() == '@') {
			return parseExpansionCommand(parser); 
		} else if(parser.lookAhead() == '?') {
			return parseIfElseExpansionCommand(parser); 
		} else if(Character.isJavaIdentifierStart(parser.lookAhead())) {
			return parseMetadataElement(parser);
		}
		
		reportError(parser.getSourcePosition());
		return null;
	}
	
	protected void reportError(final int offset) throws TemplatedSourceException {
		throw new TemplatedSourceException(offset);
	}
	
	protected void checkError(boolean condition, SimpleParser parser) throws TemplatedSourceException {
		if(condition) {
			reportError(parser.getSourcePosition());
		}
	}
	
	protected class TspExpansionElement extends TspElement {
		public final String expansionId; 
		public final String pairedExpansionId; 
		public final ArrayList<Argument> arguments; 
		public final boolean dontOuputSource;
		public final boolean anonymousExpansion;
		
		public TspExpansionElement(String expansionId, String pairedExpansionId, ArrayList<Argument> arguments, 
			boolean anonymousExpansion, boolean dontOuputSource) {
			this.expansionId = expansionId;
			this.pairedExpansionId = pairedExpansionId;
			this.arguments = arguments;
			this.anonymousExpansion = anonymousExpansion;
			this.dontOuputSource = dontOuputSource;
		}
		
		@Override
		public String toString() {
			return "EXPANSION["+(anonymousExpansion?"^":"")+(dontOuputSource?"!":"")+
				StringUtil.nullAsEmpty(expansionId)+
				(pairedExpansionId == null ? "" : "("+pairedExpansionId+")")+
				(arguments == null ? "" : "{"+StringUtil.collToString(arguments, "#,#")+"}")+
				"]";
		}
	}
	
	@SuppressWarnings("serial")
	protected class Argument extends ArrayList<TspElement> { 
		@Override
		public String toString() {
			return "ARGUMENT["+StringUtil.collToString(this, "")+"]";
		}
	}
	
	protected TspElement parseExpansionCommand(SimpleParser parser) throws TemplatedSourceException {
		assertTrue(parser.lookAhead() == '{' || parser.lookAhead() == '@');
		
		String expansionId = null;
		boolean defineOnly = false; 
		boolean anonymousExpansion = false;
		
		if(parser.tryConsume("@")) {
			if(parser.tryConsume("^")) {
				anonymousExpansion = true;
			}
			expansionId = emptyToNull(parser.consumeAlphaNumericUS(false));
			if(anonymousExpansion) {
				checkError(expansionId == null, parser);
			} 
			if(parser.tryConsume("!")) {
				checkError(anonymousExpansion, parser);
				defineOnly = true;
			}
		}
		
		ArrayList<Argument> arguments = null;
		int alt = parser.tryConsume(OPEN_DELIMS);
		if(alt != -1) {
			String closeDelim = CLOSE_DELIMS[alt];
			arguments = parseArgumentList(parser, closeDelim);
		}
		checkError(defineOnly && arguments == null, parser);
		
		String pairedExpansionId = null;
		if(parser.tryConsume("(")) {
			pairedExpansionId = consumeDelimitedId(parser, ")");
		}
		
		// This is just an optional separator, useful when expansion only has an id, like: #@EXP•BLA
		parser.tryConsume("•"); 
		
		checkError(expansionId == null && pairedExpansionId == null && arguments == null, parser);
		return new TspExpansionElement(expansionId, pairedExpansionId, arguments, anonymousExpansion, defineOnly);
	}
	
	protected String consumeDelimitedId(SimpleParser parser, String closeDelim) throws TemplatedSourceException {
		String pairedExpansionId = emptyToNull(parser.consumeAlphaNumericUS(false));
		checkError(pairedExpansionId == null, parser);
		checkError(parser.tryConsume(closeDelim) == false, parser);
		return pairedExpansionId;
	}
	
	protected ArrayList<Argument> parseArgumentList(SimpleParser parser, String closeDelim) 
		throws TemplatedSourceException {
		String argSep = closeDelim.equals("}") ? "," : "●"; 
		return parseArgumentList(parser, argSep, closeDelim, false);
	}
	
	protected ArrayList<Argument> parseArgumentList(SimpleParser parser, String argumentSep, String listEnd,
		boolean eofTerminates) throws TemplatedSourceException {
		assertNotNull(listEnd);
		ArrayList<Argument> arguments = new ArrayList<Argument>();
		
		assertTrue(!eofTerminates || argumentSep == null);
		
		boolean uniformArgSyntax = false;
		if(argumentSep != null && parser.tryConsume("►")) {
			uniformArgSyntax = true;
			checkError(parser.tryConsumeNewlineRule() == false, parser);
		}
		
		Argument argument = new Argument();
		while(true) {
			TspElement element = parseElementWithCustomStarts(parser, 
				(argumentSep != null ? argumentSep : listEnd), listEnd, kMARKER);
			// The above code may result in a call with duplicate listEnd arguments, 
			// that stil works despite looking strange
			
			if(element == null) {
				checkError(!eofTerminates, parser);
				checkError(uniformArgSyntax, parser);
				break;
			} else if(element.getElementType() == listEnd) {
				checkError(uniformArgSyntax, parser);
				break;
			} else if(argumentSep != null && element.getElementType() == argumentSep) {
				if(uniformArgSyntax) {
					SimpleParser tempParser = parser.copyState();
					if(tempParser.seekToNewLine() && 
						tempParser.getLastConsumedString().trim().isEmpty() &&
						tempParser.seekWhiteSpace().tryConsume(listEnd)) {
						parser.resetToPosition(tempParser.getSourcePosition());
						break;
					}
				}
				
				arguments.add(argument);
				argument = new Argument();
			} else {
				argument.add(element);
			}
		}
		arguments.add(argument);
		return arguments;
	}
	
	protected class TspMetadataElement extends TspElement {
		public final String tag; 
		public final String value; 
		public final Argument associatedElements; 
		public final boolean outputSource;
		
		public TspMetadataElement(String tag, String value, Argument associatedElements, boolean outputSource) {
			this.tag = tag;
			this.value = value;
			this.associatedElements = associatedElements;
			this.outputSource = outputSource;
		}
	}
	
	protected static final String[] OPEN_DELIMS  = {"{","«","〈","《","「","『","【","〔","〖","〚" };
	protected static final String[] CLOSE_DELIMS = {"}","»","〉","》","」","』","】","〕","〗","〛"} ;
	
	protected TspMetadataElement parseMetadataElement(SimpleParser parser) throws TemplatedSourceException {
		String name = parser.consumeAlphaNumericUS(false);
		assertTrue(!name.isEmpty());
		
		String value = parser.tryConsume("(") ? consumeDelimitedString(parser, ")", false) : null;
		
		boolean outputSource = true;
		Argument sourceValue = null;
		boolean colonSyntaxConsumed = false;
		
		if(value == null && parser.lookAhead() == ':') {
			String id = SimpleParser.readAlphaNumericUS(parser.getSource(), parser.getSourcePosition()+1);
			if(id.length() != 0) {
				value = id;
				parser.consumeAmount(id.length() + 1);
				colonSyntaxConsumed = true;
			}
		}
		
		int alt = parser.tryConsume(OPEN_DELIMS);
		if(alt != -1) {
			String closeDelim = CLOSE_DELIMS[alt];
			sourceValue = parseArgument(parser, closeDelim, false);
		} else if(colonSyntaxConsumed == false && parser.tryConsume(":")) {
			checkError(parser.tryConsumeNewlineRule() == false, parser);
			sourceValue = parseArgument(parser, "#:END", true);
			if(!parser.lookaheadIsEOF()) {
				parser.seekToNewLine();
			}
			outputSource = false;
		}
		
		return new TspMetadataElement(name, value, sourceValue, outputSource);
	}
	
	protected Argument parseArgument(SimpleParser parser, String listEnd, boolean eofTerminates)
		throws TemplatedSourceException {
		ArrayList<Argument> argumentList = parseArgumentList(parser, null, listEnd, eofTerminates);
		assertTrue(argumentList.size() == 1);
		return argumentList.get(0);
	}
	
	protected String consumeDelimitedString(SimpleParser parser, String closeSep, boolean eofTerminates) 
		throws TemplatedSourceException {
		StringBuilder value = new StringBuilder();
		
		final String[] alts = new String[]{closeSep, kMARKER};
		while(true) {
			int alt = parser.consumeUntilAny(alts);
			value.append(parser.getLastConsumedString());
			
			if(parser.lookaheadIsEOF()) {
				checkError(!eofTerminates, parser); // Unterminated
				break;
			} else if(alt == 0) {
				parser.consume(closeSep);
				break;
			} else if(alt == 1) {
				parser.consume(kMARKER);
				if(parser.tryConsume("#")) {
					value.append("#");
				} else if(parser.tryConsume(closeSep)) {
					value.append(closeSep);
				} else {
					reportError(parser.getSourcePosition()); // Invalid Escape
				}
			} else {
				assertFail();
			}
		}
		return value.toString();
	}
	
	protected TspIfElseExpansionElement parseIfElseExpansionCommand(SimpleParser parser) 
		throws TemplatedSourceException {
		assertTrue(parser.lookAhead() == '?');
		parser.consume("?");
		
		String mdConditionId = emptyToNull(parser.consumeAlphaNumericUS(false));
		checkError(mdConditionId == null, parser);
		
		boolean invert = parser.tryConsume("!");
		
		ArrayList<Argument> arguments = null;
		int alt = parser.tryConsume(OPEN_DELIMS);
		if(alt == -1) {
			reportError(parser.getSourcePosition());
		} else {
			arguments = parseArgumentList(parser, CLOSE_DELIMS[alt]);
		}
		
		checkError(arguments.size() > 2, parser);
		
		Argument argElse = arguments.size() == 1 ? null: arguments.get(1);
		return new TspIfElseExpansionElement(mdConditionId, invert, arguments.get(0), argElse);
	}
	
	protected class TspIfElseExpansionElement extends TspElement {
		public final String mdConditionId; 
		public final boolean invert;
		public final Argument argThen; 
		public final Argument argElse;
		
		public TspIfElseExpansionElement(String mdConditionId, boolean invert, Argument argThen, Argument argElse) {
			this.mdConditionId = mdConditionId;
			this.invert = invert;
			this.argThen = argThen;
			this.argElse = argElse;
		}
		
		@Override
		public String toString() {
			return "IF?【"+ StringUtil.nullAsEmpty(mdConditionId)+"{"+argThen+","+argElse+"}"+"】";
		}
	}
	
	// --------------------- Generation phase ---------------------
	
	protected void processSplitCaseSource(String caseSource, boolean isHeader, String keyMarker) 
		throws TemplatedSourceException {
		this.kMARKER = keyMarker;
		this.kMARKER_array = new String[]{ keyMarker };
		ArrayList<TspElement> sourceElements = parseSource(caseSource);
		ProcessingState processingState = new ProcessingState(isHeader, caseSource);
		processCaseContents(processingState, new CopyableListIterator<TspElement>(sourceElements));
	}
	
	protected class ProcessingState {
		protected final boolean isHeaderCase;
		protected final String originalSource;
		protected StringBuilder sourceSB = new StringBuilder();
		protected final ArrayList<MetadataEntry> metadata = new ArrayList<MetadataEntry>();
		protected final Map<String, TspExpansionElement> expansionDefinitions = 
			new HashMap<String, TspExpansionElement>();
		protected final Map<String, Integer> activeExpansions = new HashMap<String, Integer>();
		
		public ProcessingState(boolean isHeaderCase, String originalSource) {
			this.isHeaderCase = isHeaderCase;
			this.originalSource = originalSource;
		}
		
		public ProcessingState(boolean isHeaderCase, String originalSrc, String source, List<MetadataEntry> metadata,
			Map<String, Integer> activeExpansions, Map<String, TspExpansionElement> expansionDefinitions) {
			this.isHeaderCase = isHeaderCase;
			this.originalSource = originalSrc;
			this.sourceSB.append(source);
			this.metadata.addAll(metadata);
			this.activeExpansions.putAll(activeExpansions);
			this.expansionDefinitions.putAll(expansionDefinitions);
		}
		
		@Override
		public ProcessingState clone() {
			String source = sourceSB.toString();
			return new ProcessingState(isHeaderCase, originalSource, source, metadata, activeExpansions,
				expansionDefinitions);
		}
		
		public TspExpansionElement getExpansion(String expansionId) {
			if(!isHeaderCase) {
				TspExpansionElement expansionElement = expansionDefinitions.get(expansionId);
				if(expansionElement != null) {
					return expansionElement;
				}
			}
			return globalExpansions.get(expansionId);
		}
		
		public void putExpansion(String expansionId, TspExpansionElement expansionElement) {
			if(!isHeaderCase) {
				expansionDefinitions.put(expansionId, expansionElement);
			} else {
				globalExpansions.put(expansionId, expansionElement);
			}
		}
	}
	
	protected void processCaseContents(ProcessingState sourceCase, ICopyableIterator<TspElement> elementStream) 
		throws TemplatedSourceException {
		assertNotNull(sourceCase);
		
		while(elementStream.hasNext()) {
			TspElement tspElem = elementStream.next();
			elementStream = elementStream.optimizedSelf();
			
			if(tspElem instanceof TspExpansionElement) {
				TspExpansionElement expansionElem = (TspExpansionElement) tspElem;
				
				boolean endProcessing = processExpansionElement(sourceCase, elementStream, expansionElem);
				if(endProcessing) {
					return;
				}
			} else if(tspElem instanceof TspStringElement) {
				TspStringElement stringElem = (TspStringElement) tspElem;
				sourceCase.sourceSB.append(stringElem.producedText);
			} else if(tspElem instanceof TspMetadataElement) {
				final TspMetadataElement mdElem = (TspMetadataElement) tspElem;
				
				StringBuilder originalSB = sourceCase.sourceSB;
				
				int metadataIx = sourceCase.metadata.size();
				sourceCase.metadata.add(new TemporaryMetadataEntry(mdElem));
				final TspMetadataEndElement mdEndElem = new TspMetadataEndElement(mdElem, originalSB, metadataIx);
				
				if(mdElem.associatedElements != null) {
					Argument sourceArgument = mdElem.associatedElements;
					
					if(mdElem.outputSource == false) {
						sourceCase.sourceSB = new StringBuilder(); // Create a temporary source output
					}
					
					ICopyableIterator<TspElement> mdArgIter = ChainedIterator2.create(
						CopyableListIterator.create(sourceArgument),
						CopyableListIterator.create(Collections.<TspElement>singletonList(mdEndElem))
					);
					elementStream = ChainedIterator2.create(mdArgIter, elementStream);
				} else {
					processMetadataEndElem(sourceCase, mdEndElem);
				}
			} else if(tspElem instanceof TspIfElseExpansionElement) {
				TspIfElseExpansionElement tspIfElse = (TspIfElseExpansionElement) tspElem;
				
				Argument sourceArgument = tspIfElse.invert ? tspIfElse.argThen : tspIfElse.argElse;
				
				for (MetadataEntry mde : sourceCase.metadata) {
					if(mde != null && mde.name.equals(tspIfElse.mdConditionId)) {
						sourceArgument = tspIfElse.invert ? tspIfElse.argElse : tspIfElse.argThen;
						break;
					}
				}
				if(sourceArgument != null) {
					ICopyableIterator<TspElement> mdArgIter = CopyableListIterator.create(sourceArgument);
					elementStream = ChainedIterator2.create(mdArgIter, elementStream);
				}
			} else if(tspElem instanceof TspMetadataEndElement) {
				processMetadataEndElem(sourceCase, (TspMetadataEndElement) tspElem);
			} else {
				assertFail();
			}
		}
		
		addFullyProcessedSourceCase(sourceCase);
	}
	
	protected class TemporaryMetadataEntry extends MetadataEntry {
		public TemporaryMetadataEntry(TspMetadataElement mdElem) {
			super(mdElem.tag, mdElem.value, null, -1);
		}
	}
	
	protected class TspMetadataEndElement extends TspElement {
		public final TspMetadataElement mdElem;
		public final StringBuilder originalSB;
		public final int metadataIx;
		public final int offset;
		
		public TspMetadataEndElement(TspMetadataElement mdElem, StringBuilder originalSB, int metadataIx) {
			this.mdElem = mdElem;
			this.originalSB = originalSB;
			this.offset = originalSB.length();
			this.metadataIx = metadataIx;
		}
		
		@Override
		public String toString() {
			return "<MD-END:"+offset+">";
		}
	}
	
	public void processMetadataEndElem(ProcessingState sourceCase, TspMetadataEndElement mdEndElem) {
		int offset = mdEndElem.offset;
		String associatedSource = null;
		
		TspMetadataElement mdElem = mdEndElem.mdElem;
		if(mdElem.associatedElements != null) {
			if(mdElem.outputSource) {
				associatedSource = sourceCase.sourceSB.substring(offset, sourceCase.sourceSB.length());
			} else {
				associatedSource = sourceCase.sourceSB.toString();
				sourceCase.sourceSB = mdEndElem.originalSB; // Restore original source output
				offset = -1;
			}
		}
		
		MetadataEntry mde = new MetadataEntry(mdElem.tag, mdElem.value, associatedSource, offset);
		assertTrue(sourceCase.metadata.get(mdEndElem.metadataIx) instanceof TemporaryMetadataEntry);
		sourceCase.metadata.set(mdEndElem.metadataIx, mde);
	}
	
	protected void addFullyProcessedSourceCase(ProcessingState caseState) {
		if(caseState.isHeaderCase == false) {
			String source = caseState.sourceSB.toString();
			genCases.add(new AnnotatedSource(source, caseState.originalSource, caseState.metadata));
		}
	}
	
	protected boolean processExpansionElement(ProcessingState sourceCase, ICopyableIterator<TspElement> elementStream,
		TspExpansionElement expansionElem) throws TemplatedSourceException {
		
		final String expansionId = expansionElem.expansionId;
		checkError(sourceCase.isHeaderCase && expansionId == null, sourceCase);
		
		checkError(expansionElem.anonymousExpansion && expansionElem.dontOuputSource, sourceCase);
		checkError(expansionElem.expansionId == null && expansionElem.arguments == null, sourceCase);
		
		ArrayList<Argument> arguments = expansionElem.arguments;
		TspExpansionElement definedExpansionElem = null;
		if(expansionId != null) {
			definedExpansionElem = sourceCase.getExpansion(expansionId);
			
			if(arguments == null) {
				checkError(definedExpansionElem == null, sourceCase);
				arguments = definedExpansionElem.arguments;
			} else {
				// We allow a "redefinition" only if the element is exactly the same
				checkError(definedExpansionElem != null && definedExpansionElem != expansionElem, sourceCase);
				sourceCase.putExpansion(expansionId, expansionElem);
			}
		}
		
		if((expansionElem.dontOuputSource && expansionId != null) || sourceCase.isHeaderCase) {
			 // Definition-only must not have a paired expansion
			//XXX: maybe it could, it could make sense as additional feature
			checkError(expansionElem.pairedExpansionId != null, sourceCase);
			return false;
		}
		
		Integer pairedExpansionIx = null;
		TspExpansionElement referredExpansion = null;
		if(expansionElem.pairedExpansionId == null) {
			if(definedExpansionElem != null && expansionElem.anonymousExpansion == false) {
				pairedExpansionIx = sourceCase.activeExpansions.get(expansionId);
				// The result is usually null, but it can be a valid index in certain situations
				// where this defined exp has been "redefined"
			}
		} else {
			// Paired expansion referral.
			
			referredExpansion = sourceCase.getExpansion(expansionElem.pairedExpansionId);
			checkError(referredExpansion == null, sourceCase); // If referred, then it must be defined
			
			pairedExpansionIx = sourceCase.activeExpansions.get(expansionElem.pairedExpansionId);
			
			checkError(arguments.size() != referredExpansion.arguments.size(), sourceCase);
		}
		
		String idToActivate = expansionElem.anonymousExpansion ? null : expansionId;
		String pairedIdToActivate = expansionElem.pairedExpansionId;
		
		if(pairedExpansionIx != null) {
			int ix = pairedExpansionIx;
			processArgument(sourceCase, elementStream, idToActivate, pairedIdToActivate, arguments.get(ix), ix);
		} else {
			for (int ix = 0; ix < arguments.size(); ix++) {
				processArgument(sourceCase, elementStream, idToActivate, pairedIdToActivate, arguments.get(ix), ix);
			}
		}
		return true;
	}
	
	protected void checkError(boolean errorCondition, ProcessingState sourceCase) throws TemplatedSourceException {
		if(errorCondition) {
			reportError(sourceCase); 
		}
	}
	
	protected void reportError(ProcessingState sourceCase) throws TemplatedSourceException {
		reportError(sourceCase.sourceSB.length());
	}
	
	protected void processArgument(ProcessingState sourceCase, ICopyableIterator<TspElement> elementStream,
		String expansionId, String pairingId, Argument argument, int index) throws TemplatedSourceException {
		ProcessingState newState = sourceCase.clone();
		activateId(newState, expansionId, index);
		activateId(newState, pairingId, index);
		
		ICopyableIterator<TspElement> newElements = (argument == null) ? 
			elementStream.copyState() : 
			ChainedIterator2.create(CopyableListIterator.create(argument), elementStream.copyState())
			;
		processCaseContents(newState, newElements);
	}
	
	public void activateId(ProcessingState newState, String expansionId, int index) {
		if(expansionId != null) {
			Integer oldValue = newState.activeExpansions.put(expansionId, index);
			assertTrue(oldValue == null || oldValue == index); 
		}
	}
	
}