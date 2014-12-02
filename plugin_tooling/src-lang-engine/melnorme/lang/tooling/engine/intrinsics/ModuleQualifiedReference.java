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
	public IResolvableSemantics getSemantics(ISemanticContext parentContext) {
		return new ResolvableSemantics(this, parentContext) {
		
		@Override
		public Collection<INamedElement> findTargetDefElements(boolean findOneOnly) {
			INamedElement module = ResolvableSemantics.findModuleUnchecked(context, moduleFullName);
			if(module == null) 
				return null;
			
			ResolutionLookup search = new ResolutionLookup(elementName, null, -1, findOneOnly, context);
			module.resolveSearchInMembersScope(search);
			return search.getMatchedElements();
		}
		
		@Override
		public Collection<INamedElement> resolveTypeOfUnderlyingValue() {
			return ResolvableSemantics.resolveTypeOfUnderlyingValue(context, findTargetDefElements(true));
		}
		
	};
	}
	
	@Override
	public final Collection<INamedElement> findTargetDefElements(ISemanticContext context) {
		return getSemantics(context).findTargetDefElements(true);
	}
	
	@Override
	public final Collection<INamedElement> findTargetDefElements(ISemanticContext context, boolean findFirstOnly) {
		return getSemantics(context).findTargetDefElements(true);
	}
	
}