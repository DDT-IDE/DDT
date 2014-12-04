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

import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast.ISemanticElement;
import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.engine.resolver.IResolvable;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.AbstractNamedElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.EArcheType;
import dtool.engine.analysis.DeeLanguageIntrinsics;

public class NotFoundErrorElement extends AbstractNamedElement implements IConcreteNamedElement {
	
	public static final String NOT_FOUND__NAME = "<not_found>";
	
	protected final ElementDoc doc;
	
	public static NotFoundErrorElement newFromResolvable(IResolvable resolvable) {
		ElementDoc doc = DeeLanguageIntrinsics.parseDDoc("Could not resolve: " + resolvable.toStringAsCode());
		return new NotFoundErrorElement(resolvable, doc);
	}
	
	public NotFoundErrorElement(ISemanticElement parent, ElementDoc parseDDoc) {
		super(NOT_FOUND__NAME, parent);
		this.doc = parseDDoc;
	}
	
	@Override
	public String getNameInRegularNamespace() {
		return getName(); ///*FIXME: BUG here*/
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
	public INamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new NamedElementSemantics(this, pickedElement) {
			
			@Override
			protected IConcreteNamedElement doResolveConcreteElement(ISemanticContext context) {
				return NotFoundErrorElement.this;
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