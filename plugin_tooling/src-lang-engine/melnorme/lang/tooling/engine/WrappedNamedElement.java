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


import java.nio.file.Path;

import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast.ISemanticElement;
import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.symbols.AbstractNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.EArcheType;

public abstract class WrappedNamedElement extends AbstractNamedElement {
	
	protected final INamedElement wrappedElement;
	
	public WrappedNamedElement(INamedElement wrappedElement, ISemanticElement parent) {
		super(wrappedElement.getName(), parent);
		this.wrappedElement = wrappedElement;
	}
	
	@Override
	public Path getModulePath() {
		return wrappedElement.getModulePath();
	}
	
	@Override
	public String getNameInRegularNamespace() {
		return wrappedElement.getNameInRegularNamespace();
	}
	
	@Override
	public String getFullyQualifiedName() {
		return wrappedElement.getFullyQualifiedName();
	}
	
	@Override
	public String getModuleFullyQualifiedName() {
		return wrappedElement.getModuleFullyQualifiedName();
	}
	
	@Override
	public ModuleFullName getModuleFullName() {
		return wrappedElement.getModuleFullName();
	}
	
	@Override
	public INamedElement getParentNamedElement() {
		return wrappedElement.getParentNamedElement();
	}
	
	@Override
	public EArcheType getArcheType() {
		return wrappedElement.getArcheType();
	}
	
	@Override
	public INamedElementNode resolveUnderlyingNode() {
		return wrappedElement.resolveUnderlyingNode();
	}
	
	@Override
	public ElementDoc resolveDDoc() {
		return wrappedElement.resolveDDoc();
	}
	
}