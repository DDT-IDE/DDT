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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import melnorme.lang.tooling.ast.SourceElement;
import melnorme.lang.tooling.engine.OverloadedNamedElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.ResolutionLookup;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.core.fntypes.Function;
import melnorme.utilbox.core.fntypes.Predicate;
import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.definitions.EArcheType;
import dtool.ast.references.NamedReference;
import dtool.engine.ResolvedModule;


public abstract class CommonLookupTest extends CommonNodeSemanticsTest {
	
	protected ResolutionLookup testLookup(ResolvedModule resolvedModule, Predicate<INamedElement> checker) {
		return testLookup(resolvedModule, "/*M*/", checker);
	}
	protected ResolutionLookup testLookup(ResolvedModule resolvedModule, String offsetMarker, 
			Predicate<INamedElement> checker) {
		return testLookup_______(resolvedModule, offsetMarker, checker);
	}
	private final ResolutionLookup testLookup_______(ResolvedModule resolvedModule, String offsetMarker,
			Predicate<INamedElement> checker) {
		ResolutionLookup lookup = doResolutionLookup(resolvedModule, offsetMarker);
		runChecker(checker, lookup);
		return lookup;
	}
	
	protected void runChecker(Predicate<INamedElement> checker, ResolutionLookup lookup) {
		INamedElement matchedElement = lookup.getMatchedElement();
		assertNotNull(matchedElement);
		checker.evaluate(matchedElement);
	}
	
	protected ResolutionLookup doResolutionLookup(ResolvedModule resolvedModule, String offsetMarker) {
		PickedElement<NamedReference> pick = pickElement(resolvedModule, offsetMarker, NamedReference.class);
		return doResolutionLookup(pick);
	}
	
	protected ResolutionLookup doResolutionLookup(PickedElement<NamedReference> pick) {
		NamedReference pickedNode = pick.element;
		return pickedNode.getSemantics(pick.context).doResolutionLookup(false);
	}
	
	
	
	/* -----------------  ----------------- */
	
	protected static Object[] elementToStringArray(Collection<INamedElement> overloadedElements) {
		Object[] results = ArrayUtil.map(overloadedElements, new Function<INamedElement, String>() {
			@Override
			public String evaluate(INamedElement namedElement) {
				return namedElementToString(namedElement);
			}
		});
		return results;
	}
	
	protected static String namedElementToString(INamedElement namedElement) {
		if(namedElement instanceof SourceElement) {
			SourceElement sourceElement = (SourceElement) namedElement;
			return sourceElement.toStringAsCode();
		} else {
			return namedElement.toString();
		}
	}
	
	protected static Predicate<INamedElement> checkNameConflict(final String... expectedResults) {
		return new Predicate<INamedElement>() {
			@Override
			public boolean evaluate(INamedElement matchedElement) {
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
	
	protected Predicate<INamedElement> checkSingleResult(final String expectedResult) {
		return new Predicate<INamedElement>() {
			@Override
			public boolean evaluate(INamedElement matchedElement) {
				assertAreEqual(namedElementToString(matchedElement), expectedResult);
				
				return true;
			}
		};
	}
	
}