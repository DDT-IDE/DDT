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

import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.bundles.ModuleFullName;
import melnorme.lang.tooling.symbols.INamedElement;
import descent.core.ddoc.Ddoc;
import dtool.ast.definitions.EArcheType;
import dtool.resolver.CommonDefUnitSearch;

public class NotAValueErrorElement implements INamedElement {
	
	public static final String ERROR_IS_NOT_A_VALUE = " (is not a value)";
	
	protected final INamedElement wrappedElement;

	public NotAValueErrorElement(INamedElement wrappedElement) {
		this.wrappedElement = wrappedElement;
	}
	
	@Override
	public String getName() {
		return wrappedElement.getName();
	}
	
	@Override
	public String getExtendedName() {
		return wrappedElement.getExtendedName() + ERROR_IS_NOT_A_VALUE;
	}
	
	@Override
	public String getNameInRegularNamespace() {
		return wrappedElement.getNameInRegularNamespace();
	}
	
	@Override
	public boolean isLanguageIntrinsic() {
		return wrappedElement.isLanguageIntrinsic();
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
	public INamedElement getParentElement() {
		return wrappedElement.getParentElement();
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		// Do nothing.
	}
	
	@Override
	public INamedElement resolveTypeForValueContext(IModuleResolver mr) {
		// Do nothing.
		return null;
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
	public Ddoc resolveDDoc() {
		return wrappedElement.resolveDDoc();
	}
	
}