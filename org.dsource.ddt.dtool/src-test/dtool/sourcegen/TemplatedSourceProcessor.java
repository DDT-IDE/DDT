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

import static dtool.sourcegen.TemplatedSourceProcessor.StandardErrors.MISMATCHED_VARIATION_SIZE;
import static dtool.sourcegen.TemplatedSourceProcessor.StandardErrors.UNDEFINED_REFER;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc2.ChainedIterator2;
import melnorme.utilbox.misc2.CopyableListIterator;
import melnorme.utilbox.misc2.ICopyableIterator;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.sourcegen.TemplatedSourceProcessorParser.Argument;
import dtool.sourcegen.TemplatedSourceProcessorParser.TemplatedSourceException;
import dtool.sourcegen.TemplatedSourceProcessorParser.TspCommandElement;
import dtool.sourcegen.TemplatedSourceProcessorParser.TspElement;
import dtool.sourcegen.TemplatedSourceProcessorParser.TspExpansionElement;
import dtool.sourcegen.TemplatedSourceProcessorParser.TspIfElseExpansionElement;
import dtool.sourcegen.TemplatedSourceProcessorParser.TspMetadataElement;
import dtool.sourcegen.TemplatedSourceProcessorParser.TspStringElement;
import dtool.util.NewUtils;

/**
 * Generates multiple source cases from a templated source, using an embedded markup language. 
 */
public class TemplatedSourceProcessor extends SplitProcessor {
	
	public static AnnotatedSource[] processTemplatedSource(String marker, String source) 
		throws TemplatedSourceException {
		TemplatedSourceProcessor tsp = new TemplatedSourceProcessor();
		return tsp.processSource(marker, source);
	}
	
	protected final Map<String, TspExpansionElement> globalExpansions = new HashMap<>();
	protected final ArrayList<AnnotatedSource> genCases = new ArrayList<>();
	protected final TemplatedSourceProcessorParser tspParser = new TemplatedSourceProcessorParser() {
		@Override
		protected void handleParserError(TemplatedSourceException tse) throws TemplatedSourceException {
			handleError(tse);
		};
	};
	
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
	
	public AnnotatedSource[] processSource(String defaultMarker, String fileSource) 
		throws TemplatedSourceException {
		splitSourceCases(defaultMarker, fileSource);
		return ArrayUtil.createFrom(getGenCases(), AnnotatedSource.class);
	}
	
	public AnnotatedSource[] processSource_unchecked(String defaultMarker, String unprocessedSource) {
		try {
			return processSource(defaultMarker, unprocessedSource);
		} catch(TemplatedSourceException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	protected void checkError(boolean errorCondition, ProcessingState sourceCase) throws TemplatedSourceException {
		if(errorCondition) {
			reportError(sourceCase); 
		}
	}
	
	protected void reportError(ProcessingState sourceCase) throws TemplatedSourceException {
		tspParser.reportError(sourceCase.sourceSB.length());
	}
	
	protected void checkError(boolean errorCondition, StandardErrors errorType, TspExpansionElement expansion) 
		throws TemplatedSourceException {
		if(errorCondition) {
			String str = expansion.expansionId != null ? expansion.expansionId : "";
			str += expansion.pairedExpansionId != null ? ":"+expansion.pairedExpansionId : "";
			handleError(new TemplatedSourceProcessingException(-1, errorType, str));
		}
	}
	
	public static enum StandardErrors {
		REDEFINITION,
		UNDEFINED_REFER,
		NO_ARGUMENTS, 
		MISMATCHED_VARIATION_SIZE,
	}
	
	@SuppressWarnings("serial")
	public static class TemplatedSourceProcessingException extends TemplatedSourceException {
		public final StandardErrors errorString;
		public final Object errorObject;
		
		public TemplatedSourceProcessingException(int errorOffset, StandardErrors errorString, Object errorObject) {
			super(errorOffset);
			this.errorString = errorString;
			this.errorObject = errorObject;
		}
	}
	
	// --------------------- Generation phase ---------------------

	@Override
	protected void processSplitCaseSource(String caseSource, boolean isHeader, String keyMarker) 
		throws TemplatedSourceException {
		ArrayList<TspElement> sourceElements = tspParser.parseSplitCase(caseSource, keyMarker);
		ProcessingState processingState = new ProcessingState(isHeader, caseSource);
		processCaseContents(processingState, new CopyableListIterator<TspElement>(sourceElements));
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
				elementStream = processMetadataElement(sourceCase, elementStream, mdElem);
			} else if(tspElem instanceof TspIfElseExpansionElement) {
				TspIfElseExpansionElement tspIfElse = (TspIfElseExpansionElement) tspElem;
				elementStream = processIfElseExpansion(sourceCase, elementStream, tspIfElse);
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
	
	protected void addFullyProcessedSourceCase(ProcessingState caseState) {
		if(caseState.isHeaderCase == false) {
			String source = caseState.sourceSB.toString();
			genCases.add(new AnnotatedSource(source, caseState.originalSource, caseState.metadata));
		}
	}
	
	public ICopyableIterator<TspElement> processMetadataElement(ProcessingState sourceCase,
		ICopyableIterator<TspElement> elementStream, final TspMetadataElement mdElem) {
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
		return elementStream;
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
				checkError(definedExpansionElem == null, UNDEFINED_REFER, expansionElem);
				arguments = definedExpansionElem.arguments;
			} else {
				// We allow a "redefinition" only if the element is exactly the same
				checkError(definedExpansionElem != null && definedExpansionElem != expansionElem, 
					StandardErrors.REDEFINITION, expansionElem);
				putExpansion(sourceCase, expansionId, expansionElem);
			}
		}
		checkError(arguments.size() == 0, StandardErrors.NO_ARGUMENTS, expansionElem);
		
		if(expansionElem.defineOnly || sourceCase.isHeaderCase) {
			// TODO:  situation where there is a pairedExpansionId has no test cases that test it
			return false;
		}
		
		String secondaryIdToActivate = expansionElem.anonymousExpansion ? null : expansionId;
		String pairedIdToActivate = null;
		
		Integer pairedExpansionIx = null;
		TspExpansionElement referredExpansion = null;
		if(expansionElem.pairedExpansionId == null) {
			if(definedExpansionElem != null && expansionElem.anonymousExpansion == false) {
				pairedIdToActivate = definedExpansionElem.pairedExpansionId;
				if(pairedIdToActivate == null) {
					pairedIdToActivate = expansionId;
				}
			}
		} else {
			// Paired expansion referral.
			
			referredExpansion = sourceCase.getExpansion(expansionElem.pairedExpansionId);
			if(expansionElem.anonymousExpansion) {
			} else {
				 // If referred, then it must be defined
				checkError(referredExpansion == null, UNDEFINED_REFER, expansionElem);
				checkError(arguments.size() != referredExpansion.arguments.size(), 
					MISMATCHED_VARIATION_SIZE, expansionElem);
			}
			pairedIdToActivate = expansionElem.pairedExpansionId;
		}
		
		if(pairedIdToActivate != null) {
			pairedExpansionIx = sourceCase.activeExpansions.get(pairedIdToActivate);
		}
		
		if(pairedExpansionIx != null) {
			if(sourceCase.activeExpansions.get(secondaryIdToActivate) != null) {
				secondaryIdToActivate = null;
			}
			pairedIdToActivate = null;
			int ix = pairedExpansionIx;
			processArgument(sourceCase, elementStream, secondaryIdToActivate, null, arguments.get(ix), ix);
		} else {
			for (int ix = 0; ix < arguments.size(); ix++) {
				processArgument(sourceCase, elementStream, secondaryIdToActivate, pairedIdToActivate, arguments.get(ix), ix);
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
	
	public ICopyableIterator<TspElement> processIfElseExpansion(ProcessingState sourceCase,
		ICopyableIterator<TspElement> elementStream, TspIfElseExpansionElement tspIfElse) {
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
		return elementStream;
	}
	
}