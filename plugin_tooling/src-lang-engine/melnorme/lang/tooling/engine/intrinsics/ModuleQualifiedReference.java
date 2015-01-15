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
package melnorme.lang.tooling.engine.intrinsics;

import melnorme.lang.tooling.ast.AbstractElement;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.IReference;
import melnorme.lang.tooling.engine.resolver.ReferenceSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.ResolutionLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;

/**
 * Helper reference class.
 */
public class ModuleQualifiedReference extends AbstractElement implements IReference {
	
	public final String moduleFullName;
	public final String elementName;
	
	public ModuleQualifiedReference(String moduleFullName, String elementName) {
		super(null, null, true);
		this.moduleFullName = moduleFullName;
		this.elementName = elementName;
	}
	
	@Override
	public String toStringAsCode() {
		return moduleFullName + "." + elementName;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public ReferenceSemantics getSemantics(ISemanticContext parentContext) {
		return (ReferenceSemantics) super.getSemantics(parentContext);
	}
	@Override
	public ReferenceSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new ReferenceSemantics(this, pickedElement) {
			
			@Override
			public INamedElement doResolveTargetElement() {
				INamedElement module = CommonScopeLookup.resolveModule(context, getResolvable(), moduleFullName);
				if(module == null) 
					return null;
				IConcreteNamedElement moduleConcrete = module.resolveConcreteElement(context);
				
				ResolutionLookup search = new ResolutionLookup(elementName, -1, context);
				search.evaluateInMembersScope(moduleConcrete);
				return search.completeAndGetMatchedElement();
			}
			
		};
	}
	
}