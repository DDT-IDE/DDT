/*******************************************************************************
 * Copyright (c) 2011 Bruno Medeiros and other Contributors.
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

import java.util.Set;

import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.PackageNamespace;
import melnorme.utilbox.misc.CollectionUtil;

/**
 * A scope name lookup for symbols/names that exactly match a given name.
 */
public class ResolutionLookup extends CommonScopeLookup {
	
	protected final String searchName;
	
	public ResolutionLookup(String searchName, ISemanticContext context) {
		this(searchName, -1, context);
	}
	
	public ResolutionLookup(String searchName, int refOffset, ISemanticContext context) {
		super(refOffset, context);
		this.searchName = assertNotNull(searchName);
		assertTrue(searchName.isEmpty() == false);
	}
	
	@Override
	public Set<String> findMatchingModules() {
		return CollectionUtil.createHashSet(searchName);
	}
	
	@Override
	public boolean isFinished() {
		return doGetMatchedElement() != null && !(doGetMatchedElement() instanceof PackageNamespace);
	}
	
	@Override
	public boolean matchesName(String defName) {
		return searchName.equals(defName);
	}
	
	protected INamedElement doGetMatchedElement() {
		return matches.getMap().get(searchName);
	}
	
	/** Complete the search and return the matched element. After this, the search a 
	 * @return the matched element. Can be null. */
	public INamedElement getMatchedElement() {
		completeSearchMatches();
		return doGetMatchedElement();
	}
	
}