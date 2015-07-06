/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine.analysis;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.engine.OverloadedNamedElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.Collection2;
import java.util.function.Function;
import java.util.function.Predicate;
import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.definitions.EArcheType;
import dtool.ast.references.NamedReference;
import dtool.engine.ResolvedModule;
import dtool.engine.util.NamedElementUtil;


public abstract class CommonLookupTest extends CommonNodeSemanticsTest {
	
	protected static ResolvedModule parseModule_WithRef(String source, String refName) {
		return parseModule_(source + mref(refName));
	}
	
	protected static String mref(String refName) {
		return " auto _ = " + refName + "/*M*/";
	}
	
	protected static void testLookup(ResolvedModule resolvedModule, Predicate<INamedElement> checker) {
		testLookup(resolvedModule, "/*M*/", checker);
	}
	protected static void testLookup(ResolvedModule resolvedModule, String offsetMarker, 
			Predicate<INamedElement> checker) {
		testLookup_______(resolvedModule, offsetMarker, checker);
	}
	private static void testLookup_______(ResolvedModule resolvedModule, String offsetMarker,
			Predicate<INamedElement> checker) {
		resolvedModule.getSemanticContext()._resetSemantics();
		
		INamedElement matchedElement = getReferenceResolvedElement(resolvedModule, offsetMarker);
		checker.test(matchedElement);
	}
	
	public static INamedElement getReferenceResolvedElement(ResolvedModule resolvedModule, String offsetMarker) {
		PickedElement<NamedReference> pick = pickElement(resolvedModule, offsetMarker, NamedReference.class);
		return pick.element.getSemantics(pick.context).resolveTargetElement_();
	}
	
	/* -----------------  ----------------- */
	
	protected static Object[] elementToStringArray(Collection2<INamedElement> overloadedElements) {
		Object[] results = ArrayUtil.map(overloadedElements, new Function<INamedElement, String>() {
			@Override
			public String apply(INamedElement namedElement) {
				return NamedElementUtil.namedElementToString(namedElement);
			}
		});
		return results;
	}
	
	protected static Predicate<INamedElement> checkNameConflict(final String... expectedResults) {
		return new Predicate<INamedElement>() {
			@Override
			public boolean test(INamedElement matchedElement) {
				OverloadedNamedElement overload = assertInstance(matchedElement, OverloadedNamedElement.class);
				assertEqualSet(
					hashSet(elementToStringArray(overload.getOverloadedElements())), 
					hashSet(expectedResults)
				);
				
				boolean isError = true;
				if(isError) {
					assertTrue(overload.getArcheType() == EArcheType.Error);
				} else {
					assertFail(); // TODO
				}
				
				return true;
			}
		};
	}
	
}