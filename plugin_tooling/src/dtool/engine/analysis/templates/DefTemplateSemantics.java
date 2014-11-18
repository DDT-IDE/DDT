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

import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.engine.resolver.AbstractNamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.NotAValueErrorElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.ReferenceResolver;

public final class DefTemplateSemantics extends AbstractNamedElementSemantics {
	
	protected final DefinitionTemplate defTemplate;
	
	public DefTemplateSemantics(DefinitionTemplate defTemplate) {
		this.defTemplate = defTemplate;
	}
	
	@Override
	public IConcreteNamedElement resolveConcreteElement() {
		return defTemplate;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		if(defTemplate.wrapper) {
			// TODO: go straight to members of wrapped definition
		}
		ReferenceResolver.resolveSearchInScope(search, defTemplate.decls);
	}
	
	@Override
	public INamedElement resolveTypeForValueContext(IModuleResolver mr) {
		if(defTemplate.wrapper) {
			// TODO: 
		}
		return new NotAValueErrorElement(defTemplate);
	}
}