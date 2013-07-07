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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StreamUtil;
import melnorme.utilbox.misc2.ChainedIterator2;
import melnorme.utilbox.misc2.CopyableListIterator;
import melnorme.utilbox.misc2.ICopyableIterator;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.tests.SimpleParser;
import dtool.util.NewUtils;

/**
 * Generates multiple source cases from a templated source, using an embedded markup language. 
 */
public class TemplatedSourceProcessor extends TemplatedSourceProcessorParser {
	
	public static AnnotatedSource[] processTemplatedSource(String marker, String source) 
		throws TemplatedSourceException {
		TemplatedSourceProcessor tsp = new TemplatedSourceProcessor();
		return tsp.processSource(marker, source);
	}
	
	protected final Map<String, TspExpansionElement> globalExpansions = new HashMap<String, TspExpansionElement>();
	protected final ArrayList<AnnotatedSource> genCases = new ArrayList<AnnotatedSource>();
	
	public TemplatedSourceProcessor() { }
	
	public Map<String, TspExpansionElement> getGlobalExpansions() {
		return globalExpansions;
	}
	
	public void addGlobalExpansions(Map<String, TspExpansionElement> newGlobalExpansions) {
		NewUtils.addNew(globalExpansions, newGlobalExpansions);
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
	
	protected static final String[] splitKeywords = { "#:HEADER", "Ⓗ", "#:SPLIT", "━━", "▂▂", "▃▃"};
	
	public static boolean isTSPSourceStart(Reader reader) throws IOException {
		String sourceIntro = new String(StreamUtil.readCharAmountFromReader(reader, 10));
		SimpleParser parser = new SimpleParser(sourceIntro);
		return parser.tryConsume(splitKeywords) > 0;
	}
	
	public AnnotatedSource[] processSource(String defaultMarker, String fileSource) 
		throws TemplatedSourceException {
		
		SimpleParser parser = new SimpleParser(fileSource);
		
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
	
	protected void processSplitCaseSource(String caseSource, boolean isHeader, String keyMarker) 
		throws TemplatedSourceException {
		ArrayList<TspElement> sourceElements = parseSplitCase(caseSource, keyMarker);
		ProcessingState processingState = new ProcessingState(isHeader, caseSource);
		processCaseContents(processingState, new CopyableListIterator<TspElement>(sourceElements));
	}
	
	// --------------------- Generation phase ---------------------
	
	protected void checkError(boolean errorCondition, ProcessingState sourceCase) throws TemplatedSourceException {
		if(errorCondition) {
			reportError(sourceCase); 
		}
	}
	
	protected void reportError(ProcessingState sourceCase) throws TemplatedSourceException {
		reportError(sourceCase.sourceSB.length());
	}
	
	public class ProcessingState {
		
		public final boolean isHeaderCase;
		public final String originalSource;
		protected StringBuilder sourceSB = new StringBuilder();
		protected TemporaryMetadataEntry topLevelMDE = null;
		public final ArrayList<MetadataEntry> metadata = new ArrayList<MetadataEntry>();
		public final Map<String, TspExpansionElement> expansionDefinitions = 
			new HashMap<String, TspExpansionElement>();
		public final Map<String, Integer> activeExpansions = new HashMap<String, Integer>();
		
		public ProcessingState(boolean isHeaderCase, String originalSource) {
			this.isHeaderCase = isHeaderCase;
			this.originalSource = originalSource;
		}
		
		public ProcessingState(boolean isHeaderCase, String originalSrc, StringBuilder sourceSB, 
			TemporaryMetadataEntry topLevelMDE, List<MetadataEntry> metadata, Map<String, Integer> activeExpansions,
			Map<String, TspExpansionElement> expansionDefinitions) {
			this.isHeaderCase = isHeaderCase;
			this.originalSource = originalSrc;
			this.sourceSB.append(sourceSB.toString());
			this.topLevelMDE = topLevelMDE;
			this.metadata.addAll(metadata);
			this.activeExpansions.putAll(activeExpansions);
			this.expansionDefinitions.putAll(expansionDefinitions);
		}
		
		@Override
		public ProcessingState clone() {
			return new ProcessingState(isHeaderCase, originalSource, sourceSB, topLevelMDE, metadata, 
				activeExpansions, expansionDefinitions);
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
				
				int metadataIx = sourceCase.metadata.size();
				TemporaryMetadataEntry temporaryParentMDE = new TemporaryMetadataEntry(mdElem);
				sourceCase.metadata.add(temporaryParentMDE);
				final TspMetadataEndElement mdEndElem = new TspMetadataEndElement(mdElem, sourceCase, metadataIx);
				
				if(mdElem.childElements != null) {
					Argument sourceArgument = mdElem.childElements;
					
					if(mdElem.outputSource == false) {
						sourceCase.sourceSB = new StringBuilder(); // Create a temporary source output
						sourceCase.topLevelMDE = temporaryParentMDE;
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
			} else if(tspElem instanceof TspCommandElement) {
				TspCommandElement tspCommandElement = (TspCommandElement) tspElem;
				assertTrue(tspCommandElement.name.equals(TspCommandElement.DISCARD_CASE));
				return;
			} else {
				assertFail();
			}
		}
		
		addFullyProcessedSourceCase(sourceCase);
	}
	
	protected class TemporaryMetadataEntry extends MetadataEntry {
		public TemporaryMetadataEntry(TspMetadataElement mdElem) {
			super(mdElem.tag, mdElem.value, null, 0, null, false);
		}
	}
	
	protected static class TspMetadataEndElement extends TspElement {
		public final TspMetadataElement mdElem;
		public final int offset;
		public final int metadataIx;
		private final StringBuilder originalSB;
		public final TemporaryMetadataEntry originalTopLevelMDE;
		public final ProcessingState originalSourceCase;
		
		public TspMetadataEndElement(TspMetadataElement mdElem, ProcessingState sourceCase,  int metadataIx) {
			this.mdElem = mdElem;
			this.offset = sourceCase.sourceSB.length();
			this.metadataIx = metadataIx;
			this.originalSourceCase = sourceCase;
			this.originalSB = sourceCase.sourceSB;
			if(mdElem.childElements != null && mdElem.outputSource == false) {
				this.originalTopLevelMDE = sourceCase.topLevelMDE;
			} else {
				this.originalTopLevelMDE = null;
			}
		}
		
		public StringBuilder getOriginalSB(ProcessingState sourceCase) {
			if(originalSourceCase == sourceCase) {
				return originalSB;
			} else {
				return new StringBuilder(originalSB); // An expansion has occurred so we need to duplicate
			}
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
		if(mdElem.childElements != null) {
			if(mdElem.outputSource) {
				associatedSource = sourceCase.sourceSB.substring(offset, sourceCase.sourceSB.length());
			} else {
				associatedSource = sourceCase.sourceSB.toString();
				sourceCase.sourceSB = mdEndElem.getOriginalSB(sourceCase); // Restore original source output
				sourceCase.topLevelMDE = mdEndElem.originalTopLevelMDE;
			}
		}
		boolean sourceWasIncluded = mdElem.outputSource;
		
		MetadataEntry mde = new MetadataEntry(mdElem.tag, mdElem.value, associatedSource, offset, 
			sourceCase.topLevelMDE, sourceWasIncluded);
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
		checkError(expansionElem.expansionId == null && expansionElem.dontOuputSource, sourceCase);
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
				putExpansion(sourceCase, expansionId, expansionElem);
			}
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
			if(expansionElem.anonymousExpansion) {
			} else {
				checkError(referredExpansion == null, sourceCase); // If referred, then it must be defined
				checkError(arguments.size() != referredExpansion.arguments.size(), sourceCase);
			}
			
			pairedExpansionIx = sourceCase.activeExpansions.get(expansionElem.pairedExpansionId);
		}
		
		if(expansionElem.dontOuputSource || sourceCase.isHeaderCase) {
			// TODO:  situation where there is a pairedExpansionId has no test cases that test it
			return false;
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
	
	protected void putExpansion(ProcessingState sourceCase, final String expansionId, 
		TspExpansionElement expansionElem) {
		sourceCase.putExpansion(expansionId, expansionElem);
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