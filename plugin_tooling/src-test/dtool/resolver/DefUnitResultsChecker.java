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
package dtool.resolver;

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
	
	public DefUnitResultsChecker(Collection<? extends INamedElement> resultDefUnits) {
		this.resultElements = CollectionUtil.createLinkedList(resultDefUnits);
	}
	
	public DefUnitResultsChecker removeIgnoredDefUnits(boolean ignoreDummyResults, boolean ignorePrimitives) {
		removeIgnoredDefUnits(resultElements, ignoreDummyResults, ignorePrimitives, false);
		return this;
	}
	
	public void removeIgnoredDefUnits(boolean ignoreDummyResults, boolean ignorePrimitives, boolean ignoreIntrinsics) {
		removeIgnoredDefUnits(resultElements, ignoreDummyResults, ignorePrimitives, ignoreIntrinsics);
	}
	
	public static void removeIgnoredDefUnits(LinkedList<INamedElement> resultDefUnits, 
			boolean ignoreDummyResults, boolean ignorePrimitives) {
		removeIgnoredDefUnits(resultDefUnits, ignoreDummyResults, ignorePrimitives, false);
	}
	
	public static void removeIgnoredDefUnits(LinkedList<INamedElement> resultDefUnits, 
		boolean ignoreDummyResults, boolean ignorePrimitives, boolean ignoreIntrinsics) {
		for (Iterator<INamedElement> iterator = resultDefUnits.iterator(); iterator.hasNext(); ) {
			INamedElement defElement = iterator.next();
			
			if(ignoreDummyResults && 
				(defElement.getName().equals("_dummy") || defElement.getName().endsWith("_ignore"))) {
				iterator.remove();
			} else if(ignorePrimitives && defElement instanceof IPrimitiveDefUnit) {
				iterator.remove();
			} else if(ignoreIntrinsics && defElement.isLanguageIntrinsic()) {
				iterator.remove();
			} else if(defElement instanceof ErrorElement) {
				iterator.remove();
			}
		}
	}
	
	public DefUnitResultsChecker removeStdLibObjectDefUnits() {
		for (Iterator<INamedElement> iterator = resultElements.iterator(); iterator.hasNext(); ) {
			INamedElement namedElement = iterator.next();
			
			String moduleName = namedElement.getModuleFullyQualifiedName();
			if(areEqual(moduleName, "object")) {
				iterator.remove();
				resultStdLibElements.add(namedElement);
			}
		}
		return this;
	}
	
	public void simpleCheckResults(String... expectedResults) {
		removeIgnoredDefUnits(true, true);
		removeStdLibObjectDefUnits();
		checkResults(expectedResults, null);
	}
	
	public void checkResults(String[] expectedResults) {
		checkResults(expectedResults, null);
	}
	
	public void checkResults(String[] expectedResults, Map<String, MetadataEntry> markers) {
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
				if(defNode.defname.getEndPos() == marker.offset || defNode.defname.getStartPos() == marker.offset) {
					iterator.remove();
					return;
				}
			}
		}
		assertFail();
	}
	
}