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

import java.util.HashSet;
import java.util.Set;

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
	protected HashSet<IScopeElement> searchedScopes;
	
	
	public CommonScopeLookup(IModuleElement refOriginModule, int refOffset, ISemanticContext moduleResolver) {
		this(refOriginModule, refOffset, false, moduleResolver);
	}
	
	public CommonScopeLookup(IModuleElement refOriginModule, int refOffset, boolean findOneOnly, 
		ISemanticContext moduleResolver) { 
		this.searchedScopes = new HashSet<>(4);
		this.refOffset = refOffset;
		this.findOnlyOne = findOneOnly;
		this.modResolver = assertNotNull(moduleResolver);
		this.refOriginModule = refOriginModule;
	}
	
	public boolean isSequentialLookup() {
		return refOffset >= 0;
	}
	
	public Set<IScopeElement> getSearchedScopes() {
		return searchedScopes;
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
				return obj.getFullyQualifiedName();
			}
		});
	}
	
	/* -----------------  ----------------- */
	
	public Set<String> findModulesWithPrefix(String fqNamePrefix) {
		return modResolver.findModules(fqNamePrefix);
	}
	
	/* -----------------  ----------------- */
	
	/** Return whether the search has found all matches. */
	public abstract boolean isFinished();
	
	/** 
	 * Evaluate a scope (a collection of nodes), for this name lookup search. 
	 */
	public void evaluateScope(IScopeElement scope) {
		assertNotNull(scope);
		
		if(searchedScopes.contains(scope))
			return;
		
		searchedScopes.add(scope);
		scope.resolveSearchInScope(this);
	}
	
	/* -----------------  ----------------- */
	
	public void evaluateNodeList(Iterable<? extends IASTNode> nodeIterable) {
		evaluateNodeList(nodeIterable, isSequentialLookup());
	}
	
	/* FIXME: need to review this code, possibly remove importsOnly. */
	public void evaluateNodeList(Iterable<? extends IASTNode> nodeIterable, boolean isSequentialLookup) {
		if(nodeIterable != null) {
			evaluateNodeList(nodeIterable, isSequentialLookup, false);
			evaluateNodeList(nodeIterable, isSequentialLookup, true);
		}
	}
	
	public void evaluateNodeList(Iterable<? extends IASTNode> nodeIter, boolean isSequential, boolean importsOnly) {
		
		if(isFinished())
			return;
		
		for (IASTNode node : nodeIter) {
			
			// Check if we have passed the reference offset
			if(isSequential && refOffset < node.getStartPos()) {
				return;
			}
			
			node.evaluateForScopeLookup(this, importsOnly, isSequential);
			
			if(isFinished() && findOnlyOne) // TODO make BUG HERE 
				return;
		}
	}
	
	public void evaluateNamedElementList(Iterable<? extends INamedElement> elementIterable) {
		
		if(isFinished())
			return;
		
		for (INamedElement namedElement : elementIterable) {
			
			evaluateNamedElementForSearch(namedElement);
			if(isFinished() && findOnlyOne) // TODO make BUG HERE 
				return;
		}
	}
	
	public void evaluateNamedElementForSearch(INamedElement namedElement) {
		if(namedElement != null) {
			visitElement(namedElement);
		}
	}
	
}