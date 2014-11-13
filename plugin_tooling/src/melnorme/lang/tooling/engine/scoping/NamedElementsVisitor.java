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

import java.util.ArrayList;
import java.util.List;

import melnorme.lang.tooling.ast_actual.ILangNamedElement;
import dtool.ast.declarations.PackageNamespace;

public abstract class NamedElementsVisitor {
	
	protected ArrayList<ILangNamedElement> matches = new ArrayList<>(2);
	
	protected boolean matchesArePartialDefUnits = false;
	
	public List<ILangNamedElement> getMatchedElements() {
		return matches;
	}
	
	public void visitElement(ILangNamedElement namedElement) {
		if(matches(namedElement)) {
			addMatch(namedElement);
		}
	}
	
	public boolean matches(ILangNamedElement namedElement) {
		String name = namedElement.getNameInRegularNamespace();
		if(name == null || name.isEmpty()) {
			// Never match an element with missing name;
			return false;
		}
		
		return matchesName(name);
	}
	
	/** Returns whether this search matches the given name or not. */
	public abstract boolean matchesName(String name);
	
	/** Adds the matched named element. */
	public void addMatch(ILangNamedElement namedElem) {
		matches.add(namedElem);
		if(namedElem instanceof PackageNamespace) {
			matchesArePartialDefUnits = true;
		}
	}
	
}