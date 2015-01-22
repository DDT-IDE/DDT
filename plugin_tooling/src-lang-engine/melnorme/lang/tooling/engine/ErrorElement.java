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
import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.engine.resolver.ConcreteElementSemantics;
import melnorme.lang.tooling.engine.resolver.IReference;
import melnorme.lang.tooling.engine.resolver.IResolvable;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.AbstractNamedElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.ITypeNamedElement;
import dtool.ast.definitions.EArcheType;
import dtool.engine.analysis.DeeLanguageIntrinsics;

public class ErrorElement extends AbstractNamedElement implements IConcreteNamedElement, ITypeNamedElement {
	
	public static ElementDoc quoteDoc(String string) {
		// TODO: need to quote ddoc macros that might occur in string:
		return DeeLanguageIntrinsics.parseDDoc(string);
	}
	
	public static final String ERROR_PREFIX = "%"; /* FIXME: */
	
	public static final String UNSUPPORTED__Name = ERROR_PREFIX + "Unsupported";
	public static final String LOOP_ERROR_ELEMENT__Name = ERROR_PREFIX + "LoopError";
	
	public static ErrorElement newNotFoundError(IReference reference) {
		return new NotFoundErrorElement(reference);
	}
	
	// Error for unsupported functionality
	public static ErrorElement newUnsupportedError(ILanguageElement owner, ElementDoc doc) {
		return new ErrorElement(UNSUPPORTED__Name, owner, doc);
	}
	
	public static ErrorElement newLoopError(ILanguageElement owner, ElementDoc doc) {
		return new ErrorElement(LOOP_ERROR_ELEMENT__Name, owner, doc);
	}
	
	/* -----------------  ----------------- */
	
	protected final ElementDoc doc;
	
	public ErrorElement(String name, ILanguageElement ownerElement, ElementDoc doc) {
		super(name, null, ownerElement, true);
		assertNotNull(ownerElement);
		this.doc = doc;
	}
	
	@Override
	protected void doSetCompleted() {
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
	public INamedElement getModuleElement() {
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
	public INamedElementNode resolveUnderlyingNode() {
		return null;
	}
	
	@Override
	public ElementDoc resolveDDoc() {
		return doc;
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
	
	public static class ErrorNamedElementSemantics extends ConcreteElementSemantics {
		
		public ErrorNamedElementSemantics(IConcreteNamedElement element, PickedElement<?> pickedElement) {
			super(element, pickedElement);
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonScopeLookup search) {
			// Do nothing.
		}
		
		@Override
		public INamedElement resolveTypeForValueContext() {
			return element;
		}
	}
	
	/* -----------------  ----------------- */
	
	public static class SyntaxErrorElement extends ErrorElement {
		
		public static final String SYNTAX_ERROR__Name = ERROR_PREFIX + "SyntaxError";
		
		public SyntaxErrorElement(ILanguageElement ownerElement, ElementDoc doc) {
			super(SYNTAX_ERROR__Name, ownerElement, doc);
		}
		
		@Override
		public String toString() {
			return getExtendedName();
		}
		
	}
	
	/* -----------------  ----------------- */
	
	public static class NotFoundErrorElement extends ErrorElement {
		
		public static final String NOT_FOUND__Name = ERROR_PREFIX + "NotFound";
		
		protected final IResolvable reference;
		
		public NotFoundErrorElement(IReference reference) {
			super(NOT_FOUND__Name, reference, 
				quoteDoc("Could not resolve reference: " + reference.toStringAsCode()));
			this.reference = reference;
		}
		
		@Override
		public String getExtendedName() {
			return errorName(reference.toStringAsCode());
		}
		
		@Override
		public String toString() {
			return errorName(reference.toStringAsCode());
		}
		
		public static String errorName(String nameSuffix) {
			return NOT_FOUND__Name + ":" + nameSuffix;
		}
		
	}
	
	/* -----------------  ----------------- */
	
	public static class NotATypeErrorElement extends ErrorElement {
		
		public static final String ERROR_IS_NOT_A_TYPE = ERROR_PREFIX + "NotAType";
		
		public final IConcreteNamedElement invalidElement;
		
		public NotATypeErrorElement(IReference owner, IConcreteNamedElement invalidElement) {
			super(ERROR_IS_NOT_A_TYPE, owner, 
				quoteDoc("Element is not a type: " + invalidElement.getFullyQualifiedName()));
			this.invalidElement = invalidElement;
		}
		
		@Override
		public String getExtendedName() {
			return errorName(invalidElement.getExtendedName());
		}
		
		@Override
		public String getFullyQualifiedName() {
			return errorName(invalidElement.getFullyQualifiedName());
		}
		
		@Override
		public String toString() {
			return getExtendedName();
		}
		
		public static String errorName(String nameSuffix) {
			return ERROR_IS_NOT_A_TYPE + ":" + nameSuffix;
		}
		
		/* -----------------  ----------------- */
		
		@Override
		public NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
			return new ErrorElement.ErrorNamedElementSemantics(this, pickedElement) {
				@Override
				public INamedElement resolveTypeForValueContext() {
					// Do nothing.
					return null;
				}
			};
		}
	}
	
}