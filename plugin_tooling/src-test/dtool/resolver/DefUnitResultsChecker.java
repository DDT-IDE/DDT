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

import melnorme.lang.tooling.ast_actual.ILangNamedElement;
import melnorme.utilbox.core.fntypes.Function;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.CollectionUtil;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.tests.CommonTestUtils;
import dtool.ast.definitions.DefUnit;
import dtool.ast.util.NamedElementUtil;
import dtool.engine.common.intrinsics.CommonLanguageIntrinsics.IPrimitiveDefUnit;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;

public class DefUnitResultsChecker extends CommonTestUtils {
	
	protected LinkedList<ILangNamedElement> resultDefUnits;
	
	public DefUnitResultsChecker(Collection<? extends ILangNamedElement> resultDefUnits) {
		this.resultDefUnits = CollectionUtil.createLinkedList(resultDefUnits);
	}
	
	public void removeIgnoredDefUnits(boolean ignoreDummyResults, boolean ignorePrimitives) {
		removeIgnoredDefUnits(resultDefUnits, ignoreDummyResults, ignorePrimitives, false);
	}
	
	public void removeIgnoredDefUnits(boolean ignoreDummyResults, boolean ignorePrimitives, boolean ignoreIntrinsics) {
		removeIgnoredDefUnits(resultDefUnits, ignoreDummyResults, ignorePrimitives, ignoreIntrinsics);
	}
	
	public static void removeIgnoredDefUnits(LinkedList<ILangNamedElement> resultDefUnits, 
			boolean ignoreDummyResults, boolean ignorePrimitives) {
		removeIgnoredDefUnits(resultDefUnits, ignoreDummyResults, ignorePrimitives, false);
	}
	
	public static void removeIgnoredDefUnits(LinkedList<ILangNamedElement> resultDefUnits, 
		boolean ignoreDummyResults, boolean ignorePrimitives, boolean ignoreIntrinsics) {
		for (Iterator<ILangNamedElement> iterator = resultDefUnits.iterator(); iterator.hasNext(); ) {
			ILangNamedElement defElement = iterator.next();
			
			if(ignoreDummyResults && 
				(defElement.getName().equals("_dummy") || defElement.getName().endsWith("_ignore"))) {
				iterator.remove();
			} else if(ignorePrimitives && defElement instanceof IPrimitiveDefUnit) {
				iterator.remove();
			} else if(ignoreIntrinsics && defElement.isLanguageIntrinsic()) {
				iterator.remove();
			}
		}
	}
	
	public void removeStdLibObjectDefUnits() {
		for (Iterator<ILangNamedElement> iterator = resultDefUnits.iterator(); iterator.hasNext(); ) {
			ILangNamedElement defElement = iterator.next();
			
			String moduleName = defElement.getModuleFullyQualifiedName();
			if(areEqual(moduleName, "object")) {
				iterator.remove();
			}
		}
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
				DefUnitResultsChecker.removeDefUnitByMarker(resultDefUnits, marker);
			} else {
				removeDefUnit(expectedTarget);
			}
		}
		
		if(!resultDefUnits.isEmpty()) {
			String resultDefUnitsStr = collToString(strmap(resultDefUnits,
				CompareDefUnits.fnElementToFullyQualifiedName()), "\n");
			System.out.println("--- Unexpected elements ("+resultDefUnits.size()+") : ---\n" + resultDefUnitsStr);
		}
		assertTrue(resultDefUnits.isEmpty());
	}
	
	public void removeDefUnit(String expectedTarget) {
		String moduleName = StringUtil.segmentUntilMatch(expectedTarget, "/");
		String defUnitModuleQualifiedName = StringUtil.substringAfterMatch(expectedTarget, "/");
		
		boolean removed = false;
		if(moduleName == null ) {
			for (Iterator<ILangNamedElement> iterator = resultDefUnits.iterator(); iterator.hasNext(); ) {
				ILangNamedElement element = iterator.next();
				
				if(element.getName().equals(expectedTarget)) {
					iterator.remove();
					removed = true;
				}
			}
		} else {
			String expectedFullyTypedQualification = moduleName + 
				(defUnitModuleQualifiedName != null ? "/" + defUnitModuleQualifiedName : "");
			
			for (Iterator<ILangNamedElement> iterator = resultDefUnits.iterator(); iterator.hasNext(); ) {
				ILangNamedElement element = iterator.next();
				
				String defUnitTypedQualification = NamedElementUtil.getElementTypedQualification(element);
				if(defUnitTypedQualification.equals(expectedFullyTypedQualification)) {
					iterator.remove();
					removed = true;
				} else {
					continue; // Not a match
				}
			}
		}
		if(removed == false) {
			System.out.println(" > Not Found: " + expectedTarget);
			assertFail(); // Must find a matching result
		}
	}
	
	
	
	/* ------ */
	
	public static <T> String[] strmap(Collection<T> coll, Function<? super T, String> evalFunction) {
		return ArrayUtil.map(coll, evalFunction, String.class);
	}
	
	public static void removeDefUnitByMarker(Collection<ILangNamedElement> resolvedDefUnits, MetadataEntry marker) {
		for (Iterator<ILangNamedElement> iterator = resolvedDefUnits.iterator(); iterator.hasNext(); ) {
			ILangNamedElement element = iterator.next();
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