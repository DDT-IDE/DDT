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

import melnorme.lang.tooling.ast.AbstractElement;
import melnorme.lang.tooling.ast.ISemanticElement;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.symbols.INamedElement;

public abstract class WrappedNamedElement extends AbstractElement implements INamedElement {
	
	protected final INamedElement wrappedElement;
	
	public WrappedNamedElement(INamedElement wrappedElement, ISemanticElement parent) {
		super(parent);
		this.wrappedElement = wrappedElement;
	}
	
	@Override
	public Path getModulePath() {
		return wrappedElement.getModulePath();
	}
	
	@Override
	public String getName() {
		return wrappedElement.getName();
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
	
	/* -----------------  ----------------- */
	
	@Override
	public INamedElementSemantics getSemantics(ISemanticContext parentContext) {
		return (INamedElementSemantics) super.getSemantics(parentContext);
	}
	@Override
	public abstract INamedElementSemantics createSemantics(ISemanticContext context);
	
}