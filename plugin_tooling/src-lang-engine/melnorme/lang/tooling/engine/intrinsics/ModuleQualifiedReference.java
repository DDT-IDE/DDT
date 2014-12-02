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

import melnorme.lang.tooling.ast.AbstractElement;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.resolver.IResolvable;
import melnorme.lang.tooling.engine.resolver.IResolvableSemantics;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics;
import melnorme.lang.tooling.engine.scoping.ResolutionLookup;
import melnorme.lang.tooling.symbols.INamedElement;

/**
 * Helper reference class.
 */
public class ModuleQualifiedReference extends AbstractElement implements IResolvable {
	
	public final String moduleFullName;
	public final String elementName;
	
	public ModuleQualifiedReference(String moduleFullName, String elementName) {
		super(null);
		this.moduleFullName = moduleFullName;
		this.elementName = elementName;
	}
	
	@Override
	public String toStringAsCode() {
		return moduleFullName + "." + elementName;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public IResolvableSemantics getSemantics() {
		return semantics;
	}
	
	protected final IResolvableSemantics semantics = new ResolvableSemantics(this) {
		
		@Override
		public Collection<INamedElement> findTargetDefElements(ISemanticContext mr, boolean findOneOnly) {
			INamedElement module = ResolvableSemantics.findModuleUnchecked(mr, moduleFullName);
			if(module == null) 
				return null;
			
			ResolutionLookup search = new ResolutionLookup(elementName, null, -1, findOneOnly, mr);
			module.resolveSearchInMembersScope(search);
			return search.getMatchedElements();
		}
		
		@Override
		public Collection<INamedElement> resolveTypeOfUnderlyingValue(ISemanticContext mr) {
			return ResolvableSemantics.resolveTypeOfUnderlyingValue(mr, findTargetDefElements(mr, true));
		}
		
	};
	
	@Override
	public final Collection<INamedElement> findTargetDefElements(ISemanticContext mr) {
		return getSemantics().findTargetDefElements(mr, true);
	}
	
	@Override
	public final Collection<INamedElement> findTargetDefElements(ISemanticContext mr, boolean findFirstOnly) {
		return getSemantics().findTargetDefElements(mr, true);
	}
	
}