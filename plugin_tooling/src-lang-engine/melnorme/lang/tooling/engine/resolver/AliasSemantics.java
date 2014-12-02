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

import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.NotAValueErrorElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.CollectionUtil;

public abstract class AliasSemantics extends NamedElementSemantics<ConcreteElementResult> {
	
	public AliasSemantics(INamedElement element, ISemanticContext context) {
		super(element, context);
	}
	
	@Override
	public final ConcreteElementResult resolveConcreteElement(ISemanticContext context) {
		return getElementResolution(context);
	}
	
	@Override
	protected ConcreteElementResult createResolution(ISemanticContext context) {
		return new ConcreteElementResult(resolveConcreteElement(context, getAliasTarget()));
	}
	
	public static IConcreteNamedElement resolveConcreteElement(ISemanticContext sr, IResolvable aliasTarget) {
		if(aliasTarget == null) {
			return null;
		}
		INamedElement result = aliasTarget.getSemantics().resolveTargetElement(sr).getSingleResult();
		if(result == null) {
			return null;
		}
		return result.resolveConcreteElement(sr);
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonScopeLookup search) {
		TypeSemantics.resolveSearchInReferredContainer(search, getAliasTarget());
	}
	
	@Override
	public INamedElement resolveTypeForValueContext(ISemanticContext mr) {
		IResolvable aliasTarget = getAliasTarget();
		if(aliasTarget != null) {
			return CollectionUtil.getFirstElementOrNull(aliasTarget.getSemantics().resolveTypeOfUnderlyingValue(mr));
		}
		return null;
	}
	
	protected abstract IResolvable getAliasTarget();
	
	public abstract static class TypeAliasSemantics extends AliasSemantics {
		
		public TypeAliasSemantics(INamedElement aliasDef, ISemanticContext context) {
			super(aliasDef, context);
		}
		
		@Override
		public INamedElement resolveTypeForValueContext(ISemanticContext mr) {
			// TODO fix leak here, this element should be created only once per resolution.
			return new NotAValueErrorElement(element, getAliasTarget());
		};
		
	}
	
}