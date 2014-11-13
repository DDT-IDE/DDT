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
package dtool.ast.references;

import java.util.Collection;

import dtool.ast.expressions.Resolvable.IQualifierNode;
import dtool.engine.common.IDeeNamedElement;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.DefUnitSearch;

/** 
 * A reference based on an identifier. These references also 
 * allow doing a search based on their lookup rules.
 */
public abstract class NamedReference extends Reference implements IQualifierNode {
	
	/** @return the central/primary name of this reference. 
	 * (that usually means the rightmost identifier without qualifiers).
	 * Can be null. */
	public abstract String getCoreReferenceName();
	
	/** @return whether the core reference is missing or not (it can be missing on syntax errors). */
	public boolean isMissingCoreReference() {
		return getCoreReferenceName() == null || getCoreReferenceName().isEmpty();
	}
	
	
	@Override
	public Collection<IDeeNamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findOneOnly) {
		if(isMissingCoreReference()) {
			return null;
		}
		int startPos = hasSourceRangeInfo() ? getStartPos() : -1;
		DefUnitSearch search = new DefUnitSearch(getCoreReferenceName(), getModuleNode(), startPos, 
			findOneOnly, moduleResolver);
		performRefSearch(search);
		return search.getMatchedElements();
	}
	
	/** Return wheter this reference can match the given defunit.
	 * This is a very lightweight method that only compares the defunit's name 
	 * with the core identifier of this reference.
	 */
	public final boolean canMatch(String qualifiedName) {
		return getCoreReferenceName().equals(qualifiedName);
	}
	
}