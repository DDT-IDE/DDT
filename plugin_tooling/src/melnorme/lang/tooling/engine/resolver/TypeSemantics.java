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

import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.references.CommonQualifiedReference;
import dtool.resolver.CommonDefUnitSearch;

public abstract class TypeSemantics extends AbstractNamedElementSemantics {
	
	protected INamedElement typeElement;
	
	public TypeSemantics(INamedElement typeElement) {
		this.typeElement = typeElement;
	}
	
	@Override
	public final INamedElement resolveTypeForValueContext(IModuleResolver mr) {
		return new NotAValueErrorElement(typeElement);
	}
	
	public static void resolveSearchInReferredContainer(CommonDefUnitSearch search, IResolvable resolvable) {
		if(resolvable == null) {
			return;
		}
		
		IModuleResolver mr = search.getModuleResolver();
		Collection<INamedElement> containers = resolvable.findTargetDefElements(mr, true);
		CommonQualifiedReference.resolveSearchInMultipleContainers(containers, search);
	}
	
}