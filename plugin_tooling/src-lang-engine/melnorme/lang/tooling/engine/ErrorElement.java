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
package melnorme.lang.tooling.engine;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.ArrayList;

import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.resolver.IReference;
import melnorme.lang.tooling.engine.resolver.IResolvable;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.NonValueConcreteElementSemantics;
import melnorme.lang.tooling.engine.resolver.TypeSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.AbstractNamedElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.ITypeNamedElement;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.MembersScopeElement;
import dtool.engine.analysis.DeeLanguageIntrinsics;

public class ErrorElement extends AbstractNamedElement implements IConcreteNamedElement {
	
	public static ElementDoc quoteDoc(String string) {
		// TODO: need to quote ddoc macros that might occur in string:
		return DeeLanguageIntrinsics.parseDDoc(string);
	}
	
	public static final String ERROR_PREFIX = "#";
	
	public static final String UNSUPPORTED__Name = ERROR_PREFIX + "Unsupported";
	public static final String LOOP_ERROR_ELEMENT__Name = ERROR_PREFIX + "LoopError";
	
	public static NotFoundErrorElement newNotFoundError(IReference reference) {
		return new NotFoundErrorElement(reference);
	}
	
	// Error for unsupported functionality
	public static Invalid_TypeErrorElement newUnsupportedError(ILanguageElement owner, ElementDoc doc) {
		return new Invalid_TypeErrorElement(UNSUPPORTED__Name, owner, null, doc);
	}
	
	public static Invalid_TypeErrorElement newLoopError(ILanguageElement owner, ElementDoc doc) {
		return new Invalid_TypeErrorElement(LOOP_ERROR_ELEMENT__Name, owner, null, doc);
	}
	
	/* -----------------  ----------------- */
	
	public ErrorElement(String name, ILanguageElement ownerElement, ElementDoc doc) {
		super(name, null, ownerElement, doc);
		assertNotNull(ownerElement);
		setElementReady();
	}
	
	@Override
	protected void doSetElementSemanticReady() {
	}
	
	@Override
	public String getNameInRegularNamespace() {
		return null;
	}
	
	@Override
	public String getFullyQualifiedName() {
		return getName();
	}
	
	@Override
	public INamedElement getContainingModuleNamespace() {
		return null;
	}
	
	@Override
	public INamedElement getParentNamespace() {
		return null;
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Error;
	}
	
	@Override
	public String toString() {
		return getExtendedName();
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new ErrorNamedElementSemantics(this, pickedElement);
	}
	
	public static class ErrorNamedElementSemantics extends NonValueConcreteElementSemantics {
		
		public ErrorNamedElementSemantics(IConcreteNamedElement element, PickedElement<?> pickedElement) {
			super(element, pickedElement);
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonScopeLookup search) {
			// Do nothing.
		}
		
	}
	
	/* -----------------  ----------------- */
	
	public static class SyntaxErrorElement extends ErrorElement {
		
		public static final String SYNTAX_ERROR__Name = ERROR_PREFIX + "SyntaxError";
		
		public SyntaxErrorElement(ILanguageElement ownerElement, ElementDoc doc) {
			super(SYNTAX_ERROR__Name, ownerElement, doc);
		}
		
	}
	
	public static class Invalid_ErrorElement extends ErrorElement {
		
		public final INamedElement invalidElement;
		
		public Invalid_ErrorElement(String name, ILanguageElement ownerElement, INamedElement invalidElement, 
				ElementDoc doc) {
			super(name, ownerElement, doc);
			this.invalidElement = invalidElement; // can be null
		}
		
		@Override
		public String getExtendedName() {
			return getName() + (invalidElement != null ? ":" + invalidElement.getExtendedName() : "");
		}
		
		@Override
		public String getFullyQualifiedName() {
			return getName() + (invalidElement != null ? ":" + invalidElement.getFullyQualifiedName() : "");
		}
		
	}
	
	public static class InvalidRefErrorElement extends Invalid_ErrorElement {
		
		public InvalidRefErrorElement(String name, IResolvable ownerElement, INamedElement invalidElement, 
				ElementDoc doc) {
			super(name, ownerElement, invalidElement, doc);
		}
		
	}
	
	/* -----------------  ----------------- */
	
	public static class Invalid_TypeErrorElement extends Invalid_ErrorElement implements ITypeNamedElement {
		
		public Invalid_TypeErrorElement(String name, ILanguageElement ownerElement, INamedElement invalidElement,
				ElementDoc doc) {
			super(name, ownerElement, invalidElement, doc);
		}
		
		@Override
		public TypeSemantics getSemantics(ISemanticContext parentContext) {
			return (TypeSemantics) super.getSemantics(parentContext);
		}
		@Override
		public TypeSemantics doCreateSemantics(PickedElement<?> pickedElement) {
			MembersScopeElement emptyMembers = new MembersScopeElement(new ArrayList<ILanguageElement>());
			return new TypeSemantics(this, pickedElement, emptyMembers) {
				@Override
				public INamedElement getTypeForValueContext_do() {
					return Invalid_TypeErrorElement.this;
				}
			};
		}
		
	}
	
	public static class NotATypeErrorElement extends Invalid_TypeErrorElement {
		
		public static final String ERROR_IS_NOT_A_TYPE = ERROR_PREFIX + "NotAType";
		
		public NotATypeErrorElement(IResolvable resolvable) {
			super(ERROR_IS_NOT_A_TYPE, resolvable, null,
				quoteDoc("Element is not a type: " + resolvable.toStringAsCode()));
		}
		
		public NotATypeErrorElement(IResolvable owner, IConcreteNamedElement invalidElement) {
			super(ERROR_IS_NOT_A_TYPE, owner, invalidElement,
				quoteDoc("Element is not a type: " + invalidElement.getExtendedName()));
		}
		
		public static String errorName(String nameSuffix) {
			return ERROR_IS_NOT_A_TYPE + ":" + nameSuffix;
		}
		
	}
	
	public static class NotAValueErrorElement extends Invalid_TypeErrorElement {
		
		public static final String ERROR_IS_NOT_A_VALUE = "#NotAValue";
		
		public NotAValueErrorElement(INamedElement invalidElement) {
			this(invalidElement, invalidElement); 
		}
			
		public NotAValueErrorElement(ILanguageElement owner, INamedElement invalidElement) {
			super(ERROR_IS_NOT_A_VALUE, owner, invalidElement,  
				quoteDoc("Element does not have a value: " + invalidElement.getFullyQualifiedName()));
		}
		
	}
	
	/* -----------------  ----------------- */
	
	public static class NotFoundErrorElement extends Invalid_TypeErrorElement {
		
		public static final String NOT_FOUND__Name = ERROR_PREFIX + "NotFound";
		
		protected final IResolvable reference;
		
		public NotFoundErrorElement(IReference reference) {
			super(NOT_FOUND__Name, reference, null, 
				quoteDoc("Could not resolve reference: " + reference.toStringAsCode()));
			this.reference = reference;
		}
		
		@Override
		public String getExtendedName() {
			return errorName(reference.toStringAsCode());
		}
		
		public static String errorName(String nameSuffix) {
			return NOT_FOUND__Name + ":" + nameSuffix;
		}
		
	}
	
}