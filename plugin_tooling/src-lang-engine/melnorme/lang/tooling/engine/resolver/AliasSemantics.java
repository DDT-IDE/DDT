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
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;

public abstract class AliasSemantics extends NamedElementSemantics {
	
	public AliasSemantics(INamedElement element, PickedElement<?> pickedElement) {
		super(element, pickedElement);
	}
	
	@Override
	protected IConcreteNamedElement doResolveConcreteElement() {
		IConcreteNamedElement result = resolveAliasTarget(context);
		return result != null ?
				result :
				ErrorElement.newNotFoundError(element, null);
	}
	
	protected IConcreteNamedElement getResolvedConcreteElement() {
		return getElementResolution().result;
	}
	
	protected abstract IConcreteNamedElement resolveAliasTarget(ISemanticContext context);
	
	@Override
	public final void resolveSearchInMembersScope(CommonScopeLookup search) {
		search.evaluateInMembersScope(getResolvedConcreteElement());
	}
	
	@Override
	public INamedElement resolveTypeForValueContext() {
		return getResolvedConcreteElement().resolveTypeForValueContext(context);
	}
	
	/* -----------------  ----------------- */
	
	public abstract static class RefAliasSemantics extends AliasSemantics {

		public RefAliasSemantics(INamedElement element, PickedElement<?> pickedElement) {
			super(element, pickedElement);
		}
		
		@Override
		protected IConcreteNamedElement resolveAliasTarget(ISemanticContext context) {
			return resolveAliasTarget(getAliasTarget());
		}
		
		protected abstract IReference getAliasTarget();
		
		protected IConcreteNamedElement resolveAliasTarget(IReference aliasTarget) {
			if(aliasTarget == null) {
				return null;
			}
			INamedElement namedElement = aliasTarget.getSemantics(context).resolveTargetElement().getSingleResult();
			return resolveConcreteElement(namedElement);
		}
		
	}
	
	public abstract static class TypeAliasSemantics extends RefAliasSemantics {
		
		protected final NotAValueErrorElement notAValueError;
		
		public TypeAliasSemantics(INamedElement aliasElement, PickedElement<?> pickedElement) {
			super(aliasElement, pickedElement);
			this.notAValueError = new NotAValueErrorElement(aliasElement);
		}
		
		@Override
		public INamedElement resolveTypeForValueContext() {
			return notAValueError;
		};
		
	}
	
}