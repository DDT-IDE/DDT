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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.context.ModuleSourceException;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.SymbolTable;
import melnorme.utilbox.core.fntypes.Function;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.references.RefModule;

public abstract class CommonScopeLookup {
	
	/** The offset of the reference. 
	 * Used to check availability in statement scopes. */
	public final int refOffset;
	/** Module Resolver */
	public final ISemanticContext context;
	
	protected final SymbolTable matches = new SymbolTable();
	
	/** The scopes that have already been searched */
	protected final HashSet<IScopeElement> searchedScopes = new HashSet<>(4);
	
	/** Named elements for which evaluateInMembersScope() has been called for. */
	protected final HashSet<INamedElement> resolvedElementsForMemberScopes = new HashSet<>(4);;
	
	
	public CommonScopeLookup(int refOffset, ISemanticContext context) { 
		this.refOffset = refOffset;
		this.context = assertNotNull(context);
	}
	
	public boolean isSequentialLookup() {
		return refOffset >= 0;
	}
	
	public Set<IScopeElement> getSearchedScopes() {
		return searchedScopes;
	}
	
	public SymbolTable getMatchesTable() {
		return matches;
	}
	
	public Collection<INamedElement> getMatchedElements() {
		return matches.getElements();
	}
	
	@Override
	public String toString() {
		return getClass().getName() + " ---\n" + toString_matches();
	}
	
	public String toString_matches() {
		return StringUtil.iterToString(getMatchedElements(), "\n", new Function<INamedElement, String>() {
			@Override
			public String evaluate(INamedElement obj) {
				return obj.getFullyQualifiedName();
			}
		});
	}
	
	/* ----------------- module lookup helpers ----------------- */
	
	public static IConcreteNamedElement resolveModule(ISemanticContext context, ILanguageElement refElement, 
			String moduleFullName) {
		return resolveModule(context, refElement, new ModuleFullName(moduleFullName));
	}
	
	public static IConcreteNamedElement resolveModule(ISemanticContext context, ILanguageElement refElement, 
			ModuleFullName moduleName) {
		try {
			return context.findModule(moduleName);
		} catch (ModuleSourceException pse) {
			return new ErrorElement(moduleName.getFullNameAsString(), null, refElement, null);
		}
	}
	
	public abstract Set<String> findMatchingModules();
	
	/* -----------------  ----------------- */
	
	/** Return whether the search has found all matches. */
	public abstract boolean isFinished();
	
	/** Returns whether this search matches the given name or not. */
	public abstract boolean matchesName(String name);
	
	/* -----------------  ----------------- */
	
	public void evaluateInMembersScope(INamedElement nameElement) {
		if(isFinished() || nameElement == null)
			return;
		
		IConcreteNamedElement concreteElement = nameElement.resolveConcreteElement(context);
		evaluateInMembersScope(concreteElement);
	}
	
	protected void evaluateInMembersScope(IConcreteNamedElement concreteElement) {
		if(concreteElement == null)
			return;
		
		// since evaluateInMembersScope() can call evaluateInMembersScope() of other elements,
		// we need to add loop detection. The visited scopes hash is not enough to prevent this
		// XXX: Perhaps this could be fixed instead by modifying how var nodes do evaluateInMembersScope
		
		if(resolvedElementsForMemberScopes.contains(concreteElement))
			return;
		
		resolvedElementsForMemberScopes.add(concreteElement);
		
		concreteElement.resolveSearchInMembersScope(this);
	}
	
	/* -----------------  ----------------- */
	
	/** 
	 * Evaluate a scope (a collection of nodes with named elements) for this name lookup search. 
	 */
	public void evaluateScope(IScopeElement scope) {
		SymbolTable scopeNames = resolveScopeSymbols(scope);
		if(scopeNames == null)
			 return;
		
		matches.addVisibleSymbols(scopeNames);
		
		if(scope != null) {
			// Warning: potential infinite loop problems here ? 
			scope.getScopeTraverser().evaluateSuperScopes(this);
		}
		
	}
	
	public SymbolTable resolveScopeSymbols(IScopeElement scope) {
		if(scope == null)
			return null;
		
		if(isFinished())
			return null;
		
		if(searchedScopes.contains(scope))
			return null;
		searchedScopes.add(scope);
		
		ScopeTraverser scopeTraverser = scope.getScopeTraverser();
		return doResolveScopeSymbols(scopeTraverser);
	}
	
	protected SymbolTable doResolveScopeSymbols(ScopeTraverser scopeTraverser) {
		SymbolTable names = scopeTraverser.evaluateScope(new ScopeNameResolution(this), refOffset, false);
		
		SymbolTable importedNames = scopeTraverser.evaluateScope(new ScopeNameResolution(this), refOffset, true);
		names.addVisibleSymbols(importedNames);
		return names;
	}
	
	public static class ScopeNameResolution {
		
		protected final CommonScopeLookup lookup;
		
		protected SymbolTable names = new SymbolTable();
		
		public ScopeNameResolution(CommonScopeLookup lookup) {
			this.lookup = lookup;
		}
		
		public SymbolTable getNames() {
			return names;
		}

		public CommonScopeLookup getLookup() {
			return lookup;
		}
		
		public ISemanticContext getContext() {
			return getLookup().context;
		}
		
		public void visitNamedElement(INamedElement namedElement) {
			if(namedElement == null)
				return;
			
			String name = namedElement.getNameInRegularNamespace();
			if(name == null || name.isEmpty()) {
				// Never match an element with missing name;
				return;
			}
			if(!getLookup().matchesName(name)) {
				return;
			}
			// XXX: what about if namedElement is an error?
			
			assertNotNull(namedElement);
			
			names.addSymbol(namedElement);
		}
		
		public void addImportNameElement(RefModule refModule) {
			INamedElement namedElement = refModule.getNamespaceFragment(getContext());
			visitNamedElement(namedElement);
		}
		
	}
	
}