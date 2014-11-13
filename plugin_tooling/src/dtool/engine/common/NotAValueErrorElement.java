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
package dtool.engine.common;

import descent.core.ddoc.Ddoc;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.CommonDefUnitSearch;

public class NotAValueErrorElement implements IDeeNamedElement {
	
	public static final String ERROR_IS_NOT_A_VALUE = " (is not a value)";
	
	protected final IDeeNamedElement wrappedElement;

	public NotAValueErrorElement(IDeeNamedElement wrappedElement) {
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
	public IDeeNamedElement getParentElement() {
		return wrappedElement.getParentElement();
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		// Do nothing.
	}
	
	@Override
	public IDeeNamedElement resolveTypeForValueContext(IModuleResolver mr) {
		// Do nothing.
		return null;
	}
	
	@Override
	public EArcheType getArcheType() {
		return wrappedElement.getArcheType();
	}
	
	@Override
	public DefUnit resolveDefUnit() {
		return wrappedElement.resolveDefUnit();
	}
	
	@Override
	public Ddoc resolveDDoc() {
		return wrappedElement.resolveDDoc();
	}
	
}