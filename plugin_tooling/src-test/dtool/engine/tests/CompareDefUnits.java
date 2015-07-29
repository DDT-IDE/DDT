/*******************************************************************************
 * Copyright (c) 2012, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine.tests;

import static dtool.engine.tests.DefUnitResultsChecker.strmap;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Function;

import dtool.tests.CommonDToolTest;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.tests.CommonTestUtils;

/**
 * This is an old version of what {@link DefUnitResultsChecker} does now
 */
public class CompareDefUnits extends CommonDToolTest {
	
	public static Function<INamedElement, String> fnDefUnitToStringAsElement(final int prefixLen) {
		return new Function<INamedElement, String>() {
			@Override
			public String apply(INamedElement defUnit) {
				return defUnit == null ? null : defUnit.getExtendedName().substring(prefixLen);
			}
		};
	}
	
	public static Function<INamedElement, String> fnElementToFullyQualifiedName() {
		return new Function<INamedElement, String>() {
			@Override
			public String apply(INamedElement obj) {
				return obj == null ? null : obj.getFullyQualifiedName();
			}
		};
	}
	
	// TODO need to add code to DefUnitResultsChecker to removed function names
	public static void checkResults(Collection<? extends INamedElement> originalResults, String[] expectedProposalsArr) {
		//new DefUnitResultsChecker(originalResults).simpleCheckResults(expectedProposalsArr);
		
		LinkedList<INamedElement> results = new LinkedList<>(originalResults);
		DefUnitResultsChecker.removeIgnoredElements(results, false, true);
		
		HashSet<String> expectedProposals = hashSet(expectedProposalsArr);
		HashSet<String> resultProposals = hashSet(strmap(results, fnDefUnitToStringAsElement(0)));
		
		assertTrue(resultProposals.contains(null) == false);
		
		assertEqualSet(resultProposals, expectedProposals);
	}
	
	public static void assertEqualSet(Set<?> result, Set<?> expected) {
		CommonTestUtils.assertEqualSet(result, expected);
	}
	
}