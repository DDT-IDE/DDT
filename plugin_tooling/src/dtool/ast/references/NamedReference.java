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

import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.resolver.IResolvableSemantics;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics;
import melnorme.lang.tooling.engine.scoping.ResolutionLookup;
import melnorme.lang.tooling.symbols.INamedElement;

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
	
	
	/* -----------------  ----------------- */
	
	@Override
	public IResolvableSemantics getSemantics() {
		return semantics;
	}
	
	protected final IResolvableSemantics semantics = new ResolvableSemantics(this) {
		
		@Override
		public Collection<INamedElement> findTargetDefElements(ISemanticContext mr, boolean findOneOnly) {
			if(isMissingCoreReference()) {
				return null;
			}
			int startPos = hasSourceRangeInfo() ? getStartPos() : -1;
			ResolutionLookup search = new ResolutionLookup(getCoreReferenceName(), getModuleNode2(), startPos, 
				findOneOnly, mr);
			performRefSearch(search);
			return search.getMatchedElements();
		}
		
	};
	
	/** Return wheter this reference can match the given defunit.
	 * This is a very lightweight method that only compares the defunit's name 
	 * with the core identifier of this reference.
	 */
	public final boolean canMatch(String simpleName) {
		return getCoreReferenceName().equals(simpleName);
	}
	
}