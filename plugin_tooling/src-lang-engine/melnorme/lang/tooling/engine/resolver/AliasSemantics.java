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

import melnorme.lang.tooling.engine.ErrorElement.SyntaxErrorElement;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.references.NamedReference;

public abstract class AliasSemantics extends NamedElementSemantics {
	
	public AliasSemantics(INamedElement element, PickedElement<?> pickedElement) {
		super(element, pickedElement);
	}
	
	@Override
	protected IConcreteNamedElement doResolveConcreteElement() {
		return resolveAliasTarget_nonNull();
	}
	
	protected IConcreteNamedElement getResolvedConcreteElement() {
		return getElementResolution().result;
	}
	
	protected abstract IConcreteNamedElement resolveAliasTarget_nonNull();
	
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
		protected IConcreteNamedElement resolveAliasTarget_nonNull() {
			IReference aliasTarget = getAliasTarget();
			if(isSyntaxError(aliasTarget)) {
				return new SyntaxErrorElement(element, ErrorElement.quoteDoc("Missing reference."));
			}
			
			return ReferenceSemantics.resolveConcreteElement(aliasTarget, context);
		}
		
		protected static boolean isSyntaxError(IReference aliasTarget) {
			if(aliasTarget == null)
				return true;
			
			if(aliasTarget instanceof NamedReference) {
				NamedReference namedReference = (NamedReference) aliasTarget;
				return namedReference.isMissingCoreReference();
			}
			return false;
		}
		
		protected abstract IReference getAliasTarget();
		
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