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

import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.engine.NotAValueErrorElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.references.CommonQualifiedReference;
import dtool.resolver.CommonDefUnitSearch;

public abstract class TypeSemantics extends ConcreteElementSemantics {
	
	public TypeSemantics(IConcreteNamedElement typeElement) {
		super(typeElement);
	}
	
	protected final IConcreteNamedElement getTypeElement() {
		return elementRes.result;
	}
	
	@Override
	public final INamedElement resolveTypeForValueContext(ISemanticContext mr) {
		return new NotAValueErrorElement(getTypeElement());
	}
	
	public static void resolveSearchInReferredContainer(CommonDefUnitSearch search, IResolvable resolvable) {
		if(resolvable == null) {
			return;
		}
		
		ISemanticContext mr = search.getModuleResolver();
		Collection<INamedElement> containers = resolvable.findTargetDefElements(mr, true);
		CommonQualifiedReference.resolveSearchInMultipleContainers(containers, search);
	}
	
}