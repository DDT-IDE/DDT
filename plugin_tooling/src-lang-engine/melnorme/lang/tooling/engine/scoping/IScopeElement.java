/*******************************************************************************
 * Copyright (c) 2012, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.engine.scoping;


/**
 * Interface for a scope element: 
 * A collection of nodes visited during a name lookup operation.
 * The scope is the basic unit of visitation for the name lookup operations.
 * Note: scope elements are used as key in a hash. 
 */
public interface IScopeElement {
	
	/** @return non-null. */
	public ScopeTraverser getScopeTraverser();
	
	@Override
	public boolean equals(Object obj);
	@Override
	public int hashCode();
	
	
	public static interface IExtendedScopeElement extends IScopeElement {
		
		void resolveLookupInSuperScopes(CommonScopeLookup lookup);
		
	}
	
}