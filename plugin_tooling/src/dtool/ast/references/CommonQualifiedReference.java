/*******************************************************************************
 * Copyright (c) 2011, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.references;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;

import melnorme.lang.tooling.ast_actual.ILangNamedElement;
import melnorme.lang.tooling.bundles.IModuleResolver;
import dtool.ast.expressions.Resolvable.ITemplateRefNode;
import dtool.resolver.CommonDefUnitSearch;


/**
 * Common class for qualified references 
 * There are two: normal qualified references and Module qualified references.
 */
public abstract class CommonQualifiedReference extends NamedReference implements ITemplateRefNode {
	
	public final RefIdentifier qualifiedId;
	
	public CommonQualifiedReference(RefIdentifier qualifiedId) {
		this.qualifiedId = parentize(assertNotNull(qualifiedId));
	}
	
	/** Return the qualified name (the name reference on the right side). */
	public RefIdentifier getQualifiedName() {
		return qualifiedId;
	}
	
	@Override
	public String getCoreReferenceName() {
		return qualifiedId.getCoreReferenceName();
	}
	
	public abstract int getDotOffset();
	
	public abstract Collection<ILangNamedElement> findRootDefUnits(IModuleResolver moduleResolver);
	
	@Override
	public void performRefSearch(CommonDefUnitSearch search) {
		performQualifiedRefSearch(search);
	}
	
	public void performQualifiedRefSearch(CommonDefUnitSearch search) {
		Collection<ILangNamedElement> defunits = findRootDefUnits(search.getModuleResolver());
		CommonQualifiedReference.resolveSearchInMultipleContainers(defunits, search);
	}
	
	public static void resolveSearchInMultipleContainers(Collection<ILangNamedElement> containers, 
			CommonDefUnitSearch search) {
		if(containers == null)
			return;
		
		for (ILangNamedElement container : containers) {
			if(search.isFinished())
				return;
			container.resolveSearchInMembersScope(search);
		}
		
	}
	
}