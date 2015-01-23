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
package dtool.engine.analysis.templates;

import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.NonValueConcreteElementSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.DefinitionTemplate;

public final class DefTemplateSemantics extends NonValueConcreteElementSemantics {
	
	protected final DefinitionTemplate defTemplate;
	
	public DefTemplateSemantics(DefinitionTemplate defTemplate, PickedElement<?> pickedElement) {
		super(defTemplate, pickedElement);
		this.defTemplate = defTemplate;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonScopeLookup search) {
		if(defTemplate.wrapper) {
			// TODO: go straight to members of wrapped definition
		}
		IScopeElement scope = defTemplate.decls; //TODO create empty scope.
		if(scope != null) {
			search.evaluateScope(scope);
		}
	}
	
	@Override
	public INamedElement getTypeForValueContext_do() {
		if(defTemplate.wrapper) {
			// TODO: go straight to members of wrapped definition
		}
		return super.getTypeForValueContext_do();
	}
	
}