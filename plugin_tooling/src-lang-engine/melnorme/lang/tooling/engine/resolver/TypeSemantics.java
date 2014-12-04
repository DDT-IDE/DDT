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
package melnorme.lang.tooling.engine.resolver;

import java.util.Collection;

import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.NotAValueErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.references.CommonQualifiedReference;

public abstract class TypeSemantics extends ConcreteElementSemantics {
	
	protected final NotAValueErrorElement notAValueError;
	
	public TypeSemantics(IConcreteNamedElement typeElement, PickedElement<?> pickedElement) {
		super(typeElement, pickedElement);
		notAValueError = new NotAValueErrorElement(typeElement, typeElement);
	}
	
	protected final IConcreteNamedElement getTypeElement() {
		return elementRes.result;
	}
	
	@Override
	public final INamedElement resolveTypeForValueContext() {
		return notAValueError;
	}
	
	public static void resolveSearchInScope(CommonScopeLookup search, IScopeElement scope) {
		if(scope != null) {
			search.evaluateScope(scope);
		}
	}
	
	public static void resolveSearchInReferredContainer(CommonScopeLookup search, IResolvable resolvable) {
		if(resolvable == null) {
			return;
		}
		
		ISemanticContext mr = search.modResolver;
		Collection<INamedElement> containers = resolvable.findTargetDefElements(mr);
		CommonQualifiedReference.resolveSearchInMultipleContainers(containers, search);
	}
	
}