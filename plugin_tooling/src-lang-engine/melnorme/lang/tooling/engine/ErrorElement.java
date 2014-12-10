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
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.engine.resolver.IResolvable;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.AbstractNamedElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.EArcheType;
import dtool.engine.analysis.DeeLanguageIntrinsics;

public class ErrorElement extends AbstractNamedElement implements IConcreteNamedElement {
	
	public static final String NOT_FOUND__Name = "<not_found>";
	public static final String LOOP_ERROR_ELEMENT__Name = "<Loop_Error>";
	
	
	public static ErrorElement newNotFoundError(IResolvable resolvable) {
		ElementDoc doc = DeeLanguageIntrinsics.parseDDoc("Could not resolve: " + resolvable.toStringAsCode());
		return new ErrorElement(NOT_FOUND__Name, resolvable, doc);
	}
	
	public static ErrorElement newNotFoundError(ILanguageElement parent, ElementDoc doc) {
		return new ErrorElement(NOT_FOUND__Name, parent, doc);
	}
	
	public static ErrorElement newLoopError(ILanguageElement parent, ElementDoc doc) {
		return new ErrorElement(LOOP_ERROR_ELEMENT__Name, parent, doc);
	}
	
	/* -----------------  ----------------- */
	
	protected final ElementDoc doc;
	
	public ErrorElement(String name, ILanguageElement parent, ElementDoc doc) {
		super(name, parent);
		this.doc = doc;
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
	public String getModuleFullyQualifiedName() {
		return getName();
	}
	
	@Override
	public ModuleFullName getModuleFullName() {
		return ModuleFullName.fromString("");
	}
	
	@Override
	public INamedElement getParentNamespace() {
		return null;
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Alias; // TODO: add error archetype
	}
	
	@Override
	public INamedElementNode resolveUnderlyingNode() {
		return null;
	}
	
	@Override
	public ElementDoc resolveDDoc() {
		return doc;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new NamedElementSemantics(this, pickedElement) {
			
			@Override
			protected IConcreteNamedElement doResolveConcreteElement() {
				return ErrorElement.this;
			}
			
			@Override
			public void resolveSearchInMembersScope(CommonScopeLookup search) {
				// Do nothing.
			}
			
			@Override
			public INamedElement resolveTypeForValueContext() {
				// Do nothing.
				return null;
			}
		};
	}
	
}