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
package melnorme.lang.tooling.ast;

import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.INonScopedContainer;
import dtool.ast.definitions.DefUnit;


public abstract class CommonElement implements ISemanticElement {
	
	public CommonElement() {
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public void evaluateForScopeLookup(CommonScopeLookup lookup, boolean importsOnly, boolean isSequentialLookup) {
		if(this instanceof INonScopedContainer) {
			INonScopedContainer container = ((INonScopedContainer) this);
			// FIXME: remove need for isSequentialLookup?
			lookup.evaluateScopeElements(container.getMembersIterable(), isSequentialLookup, importsOnly);
		}
		
		if(!importsOnly && this instanceof DefUnit) {
			DefUnit defunit = (DefUnit) this;
			lookup.visitElement(defunit);
		}
	}
	
}