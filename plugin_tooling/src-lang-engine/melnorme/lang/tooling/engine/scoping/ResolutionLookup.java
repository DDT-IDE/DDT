/*******************************************************************************
 * Copyright (c) 2011, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.engine.scoping;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.ast.IModuleElement;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.symbols.INamedElement;

/**
 * Normal DefUnit search, 
 * searches for DefUnit's whose defname matches the search name. 
 */
public class ResolutionLookup extends CommonScopeLookup {
	
	protected final String searchName;
	
	public ResolutionLookup(String searchName, IModuleElement refOriginModule, ISemanticContext moduleResolver) {
		this(searchName, refOriginModule, -1, moduleResolver);
	}
	
	public ResolutionLookup(String searchName, IModuleElement refOriginModule, int refOffset, 
			ISemanticContext moduleResolver) {
		super(refOriginModule, refOffset, moduleResolver);
		this.searchName = assertNotNull(searchName);
		assertTrue(searchName.isEmpty() == false);
	}
	
	@Override
	public boolean isFinished() {
		return getMatchedElement() != null;
	}
	
	@Override
	public boolean matchesName(String defName) {
		return searchName.equals(defName);
	}
	
	/** @return the matched element. Can be null. */
	public INamedElement getMatchedElement() {
		return matches.get(searchName);
	}
	
}