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
package melnorme.lang.tooling.engine.scoping;

import java.util.HashMap;
import java.util.List;

import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;
import dtool.engine.analysis.PackageNamespaceFragment;

public abstract class NamedElementsVisitor {
	
	protected final ArrayList2<INamedElement> matches = new ArrayList2<>(2);
	
	protected final HashMap<String, INamedElement> matches2 = new HashMap<String, INamedElement>();
	
	// FIXME: deprecate, then remove
	protected boolean matchesArePartialDefUnits = false;
	
	public List<INamedElement> getMatchedElements() {
		return matches;
	}
	
	/** Returns whether this search matches the given name or not. */
	public abstract boolean matchesName(String name);
	
	public void addMatch(INamedElement namedElement) {
		matches.add(namedElement);
		
		if(namedElement instanceof PackageNamespaceFragment) {
			matchesArePartialDefUnits = true;
		}
	}
	
}