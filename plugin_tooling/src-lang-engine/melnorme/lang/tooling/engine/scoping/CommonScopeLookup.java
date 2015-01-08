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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.context.ModuleSourceException;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.completion.CompletionScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement.IExtendedScopeElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.SymbolTable;
import melnorme.utilbox.core.fntypes.Function;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.declarations.ImportContent;
import dtool.ast.references.RefModule;
import dtool.engine.analysis.ModuleProxy;

public abstract class CommonScopeLookup {
	
	/** The offset of the reference. 
	 * Used to check availability in statement scopes. */
	public final int refOffset;
	/** Module Resolver */
	public final ISemanticContext modResolver; // TODO will need to deprecate this field eventually.
	
	protected final SymbolTable matches = new SymbolTable();
	
	/** The scopes that have already been searched */
	protected final HashSet<IScopeElement> searchedScopes = new HashSet<>(4);
	
	/** Named elements for which evaluateInMembersScope() has been called for. */
	protected final HashSet<INamedElement> resolvedElementsForMemberScopes = new HashSet<>(4);;
	
	
	public CommonScopeLookup(int refOffset, ISemanticContext moduleResolver) { 
		this.refOffset = refOffset;
		this.modResolver = assertNotNull(moduleResolver);
	}
	
	public boolean isSequentialLookup() {
		return refOffset >= 0;
	}
	
	public Set<IScopeElement> getSearchedScopes() {
		return searchedScopes;
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
	
	public Set<String> findModulesWithPrefix(String fqNamePrefix) {
		return modResolver.findModules(fqNamePrefix);
	}
	
	public static IConcreteNamedElement resolveModule(ISemanticContext context, ILanguageElement refElement, 
			String moduleFullName) {
		return resolveModule(context, refElement, new ModuleFullName(moduleFullName));
	}
	
	public static IConcreteNamedElement resolveModule(ISemanticContext context, ILanguageElement refElement, 
			ModuleFullName moduleName) {
		try {
			return context.findModule(moduleName);
		} catch (ModuleSourceException pse) {
			return new ErrorElement(moduleName.getFullNameAsString(), refElement, null);
		}
	}
	
	public void evaluateSearchInImportationNamespace(RefModule refModule) {
		ArrayList<ModuleProxy> moduleImportsScope = new ArrayList<>();
		
		CommonScopeLookup search = this;
		
		/* FIXME: refactor this code */
		if(search instanceof CompletionScopeLookup) {
			CompletionScopeLookup prefixDefUnitSearch = (CompletionScopeLookup) search;
			String prefix = prefixDefUnitSearch.searchPrefix;
			Set<String> matchedModule = prefixDefUnitSearch.findModulesWithPrefix(prefix);
			
			for (String fqName : matchedModule) {
				moduleImportsScope.add(new ModuleProxy(fqName, search.modResolver, true, refModule));
			}
		} else {
			assertTrue(refModule.isMissingCoreReference() == false);
			String moduleFQName = refModule.getRefModuleFullyQualifiedName();
			moduleImportsScope.add(new ModuleProxy(moduleFQName, search.modResolver, true, refModule));
		}
		
		ScopeNameResolution scopeResolution = new ScopeNameResolution(search);
		for (ModuleProxy moduleProxy : moduleImportsScope) {
			scopeResolution.visitNamedElement(moduleProxy);
		}
		matches.addVisibleSymbols(scopeResolution.getNames());
	}
	
	/* -----------------  ----------------- */
	
	/** Return whether the search has found all matches. */
	public abstract boolean isFinished();
	
	/** Returns whether this search matches the given name or not. */
	public abstract boolean matchesName(String name);
	
	/* -----------------  ----------------- */
	
	public void evaluateInMembersScope(INamedElement nameElement) {
		if(isFinished() || nameElement == null)
			return;
		
		IConcreteNamedElement concreteElement = nameElement.resolveConcreteElement(modResolver);
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
		
		if(scope instanceof IExtendedScopeElement) {
			IExtendedScopeElement extendedScopeElement = (IExtendedScopeElement) scope;
			// Warning: potential infinite loop problems here 
			extendedScopeElement.resolveLookupInSuperScopes(this);
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
		
		SymbolTable names = doResolveScopeSymbols(scope);
		
		return names;
	}
	
	protected SymbolTable doResolveScopeSymbols(IScopeElement scope) {
		ScopeTraverser scopeTraverser = scope.getScopeTraverser();
		
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
			return getLookup().modResolver;
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
			
			assertNotNull(namedElement);
			
			names.addSymbol(namedElement);
		}
		
		public void addImportNameElement(ImportContent importStatic) {
			INamedElement namedElement = importStatic.moduleRef.getNamespaceFragment(getContext());
			visitNamedElement(namedElement);
		}
		
	}
	
}