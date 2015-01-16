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
	
	public static final String NOT_FOUND__Name = "#NotFound";
	public static final String LOOP_ERROR_ELEMENT__Name = "#LoopError";
	
	public static ErrorElement newNotFoundError(IReference resolvable) {
		return new NotFoundErrorElement(resolvable);
	}
	
	// TODO: review this API, probably remove.
	public static ErrorElement newNotFoundError(ILanguageElement parent, ElementDoc doc) {
		return new ErrorElement(NOT_FOUND__Name, null, parent, doc);
	}
	
	public static ErrorElement newLoopError(ILanguageElement parent, ElementDoc doc) {
		return new ErrorElement(LOOP_ERROR_ELEMENT__Name, null, parent, doc);
	}
	
	/* -----------------  ----------------- */
	
	protected final ElementDoc doc;
	
	public ErrorElement(String name, ILanguageElement lexicalParent, ILanguageElement ownerElement, ElementDoc doc) {
		super(name, lexicalParent, ownerElement, true);
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
	public String getModuleFullName() {
		return getName();
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
		return getName();
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
	
	public static class NotFoundErrorElement extends ErrorElement {
		
		protected final IResolvable resolvable;
		
		public NotFoundErrorElement(IResolvable resolvable) {
			super(NOT_FOUND__Name, null, resolvable, 
				quoteDoc("Could not resolve reference: " + resolvable.toStringAsCode()));
			this.resolvable = resolvable;
		}
		
		@Override
		public String toString() {
			return getName() + ":" + resolvable.toStringAsCode();
		}
	}
	
}