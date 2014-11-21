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
package melnorme.lang.tooling.engine.intrinsics;

import java.util.Collection;

import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.engine.AbstractElement;
import melnorme.lang.tooling.engine.resolver.IResolvable;
import melnorme.lang.tooling.engine.resolver.IResolvableSemantics;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.CollectionUtil;
import dtool.resolver.DefUnitSearch;
import dtool.resolver.ReferenceResolver;

/**
 * Helper reference class.
 */
public class ModuleQualifiedReference extends AbstractElement implements IResolvable {
	
	public final String moduleFullName;
	public final String elementName;
	
	public ModuleQualifiedReference(String moduleFullName, String elementName) {
		this.moduleFullName = moduleFullName;
		this.elementName = elementName;
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendStrings(moduleFullName, ".", elementName);
	}
	
	/* -----------------  ----------------- */
	
	protected final IResolvableSemantics semantics = new ResolvableSemantics(this) {
		
		@Override
		public Collection<INamedElement> findTargetDefElements(ISemanticContext mr, boolean findOneOnly) {
			INamedElement module = ReferenceResolver.findModuleUnchecked(mr, moduleFullName);
			if(module == null) 
				return null;
			
			DefUnitSearch search = new DefUnitSearch(elementName, null, -1, findOneOnly, mr);
			module.resolveSearchInMembersScope(search);
			return search.getMatchedElements();
		}
		
	};
	
	@Override
	public IResolvableSemantics getSemantics() {
		return semantics;
	}
	
	public final INamedElement findTargetDefElement(ISemanticContext moduleResolver) {
		Collection<INamedElement> namedElems = findTargetDefElements(moduleResolver, true);
		return CollectionUtil.getFirstElementOrNull(namedElems);
	}
	
	@Override
	public Collection<INamedElement> findTargetDefElements(ISemanticContext mr, boolean findFirstOnly) {
		return getSemantics().findTargetDefElements(mr, true);
	}
	
	@Override
	public Collection<INamedElement> resolveTypeOfUnderlyingValue(ISemanticContext mr) {
		return ResolvableSemantics.resolveTypeOfUnderlyingValue(mr, findTargetDefElements(mr, true));
	}
	
}