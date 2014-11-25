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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import dtool.ast.definitions.DefUnit;
import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast.IModuleElement;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.core.fntypes.Function;
import melnorme.utilbox.misc.StringUtil;

public abstract class CommonScopeLookup extends NamedElementsVisitor {
	
	/** Flag for stop searching when suitable matches are found. */
	public final boolean findOnlyOne;
	/** The module where the search started. */
	public final IModuleElement refOriginModule;
	/** The offset of the reference. 
	 * Used to check availability in statement scopes. */
	public final int refOffset;
	/** Module Resolver */
	public final ISemanticContext modResolver; // TODO will need to deprecate this field eventually.
	
	/** The scopes that have already been searched */
	protected ArrayList<IScopeProvider> searchedScopes;
	
	
	public CommonScopeLookup(IModuleElement refOriginModule, int refOffset, ISemanticContext moduleResolver) {
		this(refOriginModule, refOffset, false, moduleResolver);
	}
	
	public CommonScopeLookup(IModuleElement refOriginModule, int refOffset, boolean findOneOnly, 
		ISemanticContext moduleResolver) { 
		this.searchedScopes = new ArrayList<>(4);
		this.refOffset = refOffset;
		this.findOnlyOne = findOneOnly;
		this.modResolver = assertNotNull(moduleResolver);
		this.refOriginModule = refOriginModule;
	}
	
	public boolean isSequentialLookup() {
		return refOffset >= 0;
	}
	
	public Set<String> findModulesWithPrefix(String fqNamePrefix) {
		return modResolver.findModules(fqNamePrefix);
	}
	
	/** @return the {@link IModuleElement} of the node or position where this search originates. */
	public IModuleElement getSearchOriginModule() {
		return refOriginModule;
	}
	
	@Override
	public String toString() {
		return getClass().getName() + " ---\n" + toString_matches();
	}
	
	public String toString_matches() {
		return StringUtil.iterToString(matches, "\n", new Function<INamedElement, String>() {
			@Override
			public String evaluate(INamedElement obj) {
				return obj.getModuleFullyQualifiedName();
			}
		});
	}
	
	/* -----------------  ----------------- */
	
	/** Return whether the search has found all matches. */
	public abstract boolean isFinished();
	
	/** Searches for the given CommonDefUnitSearch search, in the scope's 
	 * immediate namespace, secondary namespace (imports), and super scopes.
	 *  
	 * Does not search, if the scope has alread been searched in this search.
	 * The set of matched {@link DefUnit}s must all be visible in the same
	 * non-extended scope, (altough due to imports, they may originate from 
	 * different scopes XXX: fix this behavior? This is an ambiguity error in D).
	 */
	public static void findDefUnitInScope(IScopeProvider scope, CommonScopeLookup search) {
		assertNotNull(scope);
		if(search.hasSearched(scope))
			return;
		
		search.enterNewScope(scope);
		scope.resolveSearchInScope(search);
	}
	
	/** Return whether we have already search the given scope or not. */
	public boolean hasSearched(IScopeProvider scope) {
		// FIXME todo: shit performance here, make it a hash, or sorted search
		if(searchedScopes.contains(scope))
			return true;
		return false;
	}
	
	/** Indicate we are now searching the given new scope. */
	public void enterNewScope(IScopeProvider scope) {
		// TODO: keep only the named scopes?
		// how about partial scopes?
		searchedScopes.add(scope);
	}
	
	/* -----------------  ----------------- */
	
	public void evaluateNamedElementForSearch(INamedElement namedElement) {
		if(namedElement != null) {
			visitElement(namedElement);
		}
	}
	
	// FIXME todo: deprecate: need to get into a Scope class, not a nodeIterable
	public void findInNodeList(Iterable<? extends IASTNode> nodeIterable, boolean isSequentialLookup) {
		if(nodeIterable != null) {
			if(isFinished())
				return;
			findDefUnits(nodeIterable.iterator(), isSequentialLookup, false);
			if(isFinished())
				return;
			findDefUnits(nodeIterable.iterator(), isSequentialLookup, true);
		}
	}
	
	public void findDefUnits(Iterator<? extends IASTNode> iter, boolean isSequentialLookup, boolean importsOnly) {
		
		while(iter.hasNext()) {
			IASTNode node = iter.next();
			
			// Check if we have passed the reference offset
			if(isSequentialLookup && refOffset < node.getStartPos()) {
				return;
			}
			
			node.evaluateForScopeLookup(this, importsOnly, isSequentialLookup);
			if(isFinished() && findOnlyOne) // TODO make BUG HERE 
				return;
		}
	}
	
	public static void findInNamedElementList(CommonScopeLookup search, 
			Iterable<? extends INamedElement> elementIterable) {
		if(elementIterable == null) {
			return;
		}
		
		if(search.isFinished())
			return;
		
		for (INamedElement namedElement : elementIterable) {
			search.evaluateNamedElementForSearch(namedElement);
			if(search.isFinished() && search.findOnlyOne) // TODO make BUG HERE 
				return;
		}
	}
	
}