/*******************************************************************************
 * Copyright (c) 2012, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine.tests;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;
import static melnorme.utilbox.misc.StringUtil.collToString;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import melnorme.lang.tooling.ast.SourceElement;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.intrinsics.CommonLanguageIntrinsics.IPrimitiveDefUnit;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.core.fntypes.Function;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.CollectionUtil;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.tests.CommonTestUtils;
import dtool.ast.definitions.DefUnit;
import dtool.engine.util.NamedElementUtil;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;

public class DefUnitResultsChecker extends CommonTestUtils {
	
	protected final LinkedList<INamedElement> resultElements;
	protected final LinkedList<INamedElement> resultStdLibElements = CollectionUtil.createLinkedList();
	
	public DefUnitResultsChecker(Iterable<? extends INamedElement> resultDefUnits) {
		this.resultElements = CollectionUtil.createLinkedList(resultDefUnits);
	}
	
	public DefUnitResultsChecker removeIgnoredDefUnits(boolean ignoreDummyResults, boolean ignorePrimitives) {
		removeIgnoredElements(resultElements, ignoreDummyResults, ignorePrimitives, false);
		return this;
	}
	
	public void removeIgnoredElements(boolean ignoreDummyResults, boolean ignorePrimitives, boolean ignoreIntrinsics) {
		removeIgnoredElements(resultElements, ignoreDummyResults, ignorePrimitives, ignoreIntrinsics);
	}
	
	public static void removeIgnoredElements(LinkedList<INamedElement> resultDefUnits, 
			boolean ignoreDummyResults, boolean ignorePrimitives) {
		removeIgnoredElements(resultDefUnits, ignoreDummyResults, ignorePrimitives, false);
	}
	
	public static void removeIgnoredElements(LinkedList<INamedElement> resultDefUnits, 
		boolean ignoreDummy, boolean ignorePrimitives, boolean ignoreInstrinsics) {
		for (Iterator<INamedElement> iterator = resultDefUnits.listIterator(); iterator.hasNext(); ) {
			INamedElement defElement = iterator.next();
			
			if(ignoreDummy && 
				(defElement.getName().equals("_dummy") || defElement.getName().endsWith("_ignore"))) {
				iterator.remove();
			} else if(ignorePrimitives && defElement instanceof IPrimitiveDefUnit) {
				iterator.remove();
			} else if(ignoreInstrinsics && defElement.isBuiltinElement()) {
				iterator.remove();
			} else if(defElement instanceof ErrorElement) {
				iterator.remove();
			}
		}
	}
	
	public DefUnitResultsChecker removeStdLibObjectDefUnits() {
		resultStdLibElements.addAll(removeStdLibObjectElements(resultElements));
		return this;
	}
	
	public static LinkedList<INamedElement> removeStdLibObjectElements(LinkedList<INamedElement> elements) {
		LinkedList<INamedElement> removedElements = CollectionUtil.createLinkedList();
		
		for (Iterator<INamedElement> iterator = elements.listIterator(); iterator.hasNext(); ) {
			INamedElement namedElement = iterator.next();
			
			String moduleName = namedElement.getModuleFullName();
			if(areEqual(moduleName, "object")) {
				iterator.remove();
				removedElements.add(namedElement);
			}
		}
		return removedElements;
	}
	
	public void simpleCheckResults(String... expectedResults) {
		removeIgnoredDefUnits(true, true);
		removeStdLibObjectDefUnits();
		checkResults(expectedResults, null);
	}
	
	public void checkResults(String[] expectedResults) {
		checkResults(expectedResults, null);
	}
	
	public void checkDefaultResults(String[] expectedResults) {
		removeStdLibObjectDefUnits();
		removeIgnoredElements(true, true, false);
		checkResults(expectedResults, null);
	}
	
	public void checkResults(String[] expectedResults, Map<String, MetadataEntry> markers) {
		assertNotNull(expectedResults);
		HashSet<String> expectedResultsDeduplicated = hashSet(expectedResults);
		
		for (String expectedTarget : expectedResultsDeduplicated) {
			if(expectedTarget.startsWith("@") ) {
				String markerName = expectedTarget.substring(1);
				MetadataEntry marker = assertNotNull(markers.get(markerName));
				DefUnitResultsChecker.removeDefUnitByMarker(resultElements, marker);
			} else {
				removeDefUnit(expectedTarget);
			}
		}
		
		if(!resultElements.isEmpty()) {
			String resultDefUnitsStr = collToString(strmap(resultElements,
				CompareDefUnits.fnElementToFullyQualifiedName()), "\n");
			System.out.println("--- Unexpected elements ("+resultElements.size()+") : ---\n" + resultDefUnitsStr);
		}
		assertTrue(resultElements.isEmpty());
	}
	
	public void removeDefUnit(String expectedElement) {
		boolean removed = removeExpectedElement(expectedElement, resultElements);
		if(removed == false) {
			removed = removeExpectedElement(expectedElement, resultStdLibElements);
		}
		if(removed == false) {
			System.out.println(" > Not Found: " + expectedElement);
			assertFail(); // Must find a matching result
		}
	}
	
	public static boolean removeExpectedElement(String expectedElement, Iterable<INamedElement> iterable) {
		String moduleName = StringUtil.segmentUntilMatch(expectedElement, "/");
		String defUnitModuleQualifiedName = StringUtil.substringAfterMatch(expectedElement, "/");
		
		boolean removed = false;
		if(moduleName == null ) {
			for (Iterator<INamedElement> iterator = iterable.iterator(); iterator.hasNext(); ) {
				INamedElement element = iterator.next();
				
				if(element.getName().equals(expectedElement)) {
					iterator.remove();
					removed = true;
				}
			}
		} else {
			String expectedFullyTypedQualification = moduleName + 
				(defUnitModuleQualifiedName != null ? "/" + defUnitModuleQualifiedName : "");
			
			for (Iterator<INamedElement> iterator = iterable.iterator(); iterator.hasNext(); ) {
				INamedElement element = iterator.next();
				
				String defUnitTypedQualification = NamedElementUtil.getElementTypedLabel(element);
				if(defUnitTypedQualification.equals(expectedFullyTypedQualification)) {
					iterator.remove();
					removed = true;
				} else {
					continue; // Not a match
				}
			}
		}
		return removed;
	}
	
	
	
	/* ------ */
	
	public static <T> String[] strmap(Collection<T> coll, Function<? super T, String> evalFunction) {
		return ArrayUtil.map(coll, evalFunction, String.class);
	}
	
	public static void removeDefUnitByMarker(Collection<INamedElement> resolvedDefUnits, MetadataEntry marker) {
		for (Iterator<INamedElement> iterator = resolvedDefUnits.iterator(); iterator.hasNext(); ) {
			INamedElement element = iterator.next();
			if(element instanceof DefUnit) {
				DefUnit defNode = (DefUnit) element;
				if(defNode.defName.getEndPos() == marker.offset || defNode.defName.getStartPos() == marker.offset) {
					iterator.remove();
					return;
				}
			}
		}
		assertFail();
	}
	
	public void checkNamedElements(String... expectedResults) {
		
		removeStdLibObjectElements(resultElements);
		removeIgnoredElements(resultElements, true, true, true);
		
		@SuppressWarnings("unused")
		String resultsString = StringUtil.toString(resultElements, "\n", new Function<INamedElement, String>() {
			@Override
			public String evaluate(INamedElement obj) {
				if(obj instanceof SourceElement) {
					return NamedElementUtil.getElementTypedLabel(obj, true);
				}
				return obj.toString();
			}
		});
		
		for(String expectedLabel : expectedResults) {
			if(removedNamedElement(expectedLabel)) {
				continue;
			}
			assertFail("Not found: " + expectedLabel);
		}
	}
	
	protected boolean removedNamedElement(String expectedLabel) {
		for (Iterator<INamedElement> iterator = resultElements.listIterator(); iterator.hasNext(); ) {
			INamedElement namedElement = iterator.next();
			if(matchesLabel(namedElement, expectedLabel)) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}
	
	public static boolean matchesLabel(INamedElement element, String expectedLabel) {
		if(expectedLabel == null) {
			return (element == null);
		}
		assertNotNull(element);
		
		String elementLabel;
		if(expectedLabel.startsWith("$")) {
			elementLabel = "$" + NamedElementUtil.getElementTypedLabel(element, true);
		} else {
			elementLabel = NamedElementUtil.namedElementToString(element);
		}
		return areEqual(elementLabel, expectedLabel);
	}
	
}