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

import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.CollectionUtil;
import dtool.ast.expressions.Resolvable;
import dtool.resolver.CommonDefUnitSearch;

public abstract class AliasSemantics extends AbstractNamedElementSemantics {
	
	public AliasSemantics() {
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		TypeSemantics.resolveSearchInReferredContainer(search, getAliasTarget());
	}
	
	@Override
	public INamedElement resolveTypeForValueContext(IModuleResolver mr) {
		return resolveTypeForValueContext_Alias(mr, getAliasTarget());
	}
	
	protected abstract Resolvable getAliasTarget();
	
	public static INamedElement resolveTypeForValueContext_Alias(IModuleResolver mr, Resolvable alias) {
		Resolvable aliasTarget = alias;
		if(aliasTarget != null) {
			return CollectionUtil.getFirstElementOrNull(aliasTarget.resolveTypeOfUnderlyingValue(mr));
		}
		return null;
	}
	
	
	public abstract static class TypeAliasSemantics extends AliasSemantics {
		
		protected INamedElement aliasDef;
		
		public TypeAliasSemantics(INamedElement aliasDef) {
			this.aliasDef = aliasDef;
		}
		
		@Override
		public INamedElement resolveTypeForValueContext(IModuleResolver mr) {
			return new NotAValueErrorElement(aliasDef);
		};
		
	}
	
}