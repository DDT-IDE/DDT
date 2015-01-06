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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.ast.IModuleElement;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.context.ModuleSourceException;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.scoping.IScopeElement.IExtendedScopeElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.SymbolTable;
import melnorme.utilbox.core.fntypes.Function;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.declarations.ImportContent;

public abstract class CommonScopeLookup {
	
	/** The module where the search started. */
	public final IModuleElement refOriginModule;
	/** The offset of the reference. 
	 * Used to check availability in statement scopes. */
	public final int refOffset;
	/** Module Resolver */
	public final ISemanticContext modResolver; // TODO will need to deprecate this field eventually.
	
	protected final HashMap<String, INamedElement> matches = new HashMap<String, INamedElement>();
	
	/** The scopes that have already been searched */
	protected final HashSet<IScopeElement> searchedScopes = new HashSet<>(4);
	
	protected final HashSet<IScopeElement> searchedScopes_asImport = new HashSet<>(4);
	
	/** Named elements for which evaluateInMembersScope() has been called for. */
	protected final HashSet<INamedElement> resolvedElementsForMemberScopes = new HashSet<>(4);;
	
	
	public CommonScopeLookup(IModuleElement refOriginModule, int refOffset, ISemanticContext moduleResolver) { 
		this.refOffset = refOffset;
		this.modResolver = assertNotNull(moduleResolver);
		this.refOriginModule = refOriginModule;
	}
	
	public boolean isSequentialLookup() {
		return refOffset >= 0;
	}
	
	public Set<IScopeElement> getSearchedScopes() {
		return searchedScopes;
	}
	
	@Deprecated
	/** @return the {@link IModuleElement} of the node or position where this search originates. */
	public IModuleElement getSearchOriginModule() {
		return refOriginModule;
	}
	
	@Override
	public String toString() {
		return getClass().getName() + " ---\n" + toString_matches();
	}
	
	public String toString_matches() {
		return StringUtil.iterToString(matches.values(), "\n", new Function<INamedElement, String>() {
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
	
	/* -----------------  ----------------- */
	
	public Collection<INamedElement> getMatchedElements2() {
		return matches.values();
	}
	
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
		evaluateScope(scope, false);
	}
	
	public SymbolTable evaluateScope(IScopeElement scope, boolean asImport) {
		SymbolTable scopeNames = resolveScopeSymbols(scope, asImport);
		if(scopeNames != null) {
			addSymbolTableToMatches(scopeNames);
			
			if(scope instanceof IExtendedScopeElement) {
				IExtendedScopeElement extendedScopeElement = (IExtendedScopeElement) scope;
				// Warning: potential infinite loop problems here 
				extendedScopeElement.resolveLookupInSuperScopes(this);
			}
		}
		
		return scopeNames;
	}
	
	public void addSymbolTableToMatches(SymbolTable scopeNames) {
		matches.putAll(scopeNames.getMap());
	}
	
	public SymbolTable resolveScopeSymbols(IScopeElement scope, boolean asImport) {
		if(scope == null)
			return null;
		
		if(isFinished())
			return null;
		
		if(asImport) {
			// TODO: we should actually create a different scope class when searching as import
			
			if(searchedScopes_asImport.contains(scope))
				return null;
			searchedScopes_asImport.add(scope);
			
		} else {
			if(searchedScopes.contains(scope))
				return null;
			searchedScopes.add(scope);
		}
		
		return evaluateScopeElements(scope.getScopeNodeList(), !scope.allowsForwardReferences(), asImport);
	}
	
	public SymbolTable evaluateScopeElements( 
			Iterable<? extends ILanguageElement> nodeIterable, boolean isSequential, boolean asImport) {
		if(nodeIterable == null)
			return null;
		
		ScopeNameResolution scopeResolution = new ScopeNameResolution(this);
		scopeResolution.evaluateScopeElements(nodeIterable, isSequential, false, false /*Irrelevant*/);
		scopeResolution.evaluateScopeElements(nodeIterable, isSequential, true, asImport);
		scopeResolution.addImportedNamesToSymbolTable();
		return scopeResolution.names;
	}
	
	public static class ScopeNameResolution {
		
		protected final CommonScopeLookup lookup;
		
		protected SymbolTable names = new SymbolTable();
		protected SymbolTable importedNames = new SymbolTable();
		
		public ScopeNameResolution(CommonScopeLookup lookup) {
			this.lookup = lookup;
		}

		public CommonScopeLookup getLookup() {
			return lookup;
		}
		
		public ISemanticContext getContext() {
			return getLookup().modResolver;
		}
		
		public void evaluateScopeElements(Iterable<? extends ILanguageElement> nodeIter, boolean isSequentialLookup, 
				boolean importsOnly, boolean scopeAsImport) {
			
			// Note: don't check for isFinished() during the loop
			for (ILanguageElement node : nodeIter) {
				
				// Check if we have passed the reference offset
				if(isSequentialLookup && node instanceof IASTNode) {
					/* FIXME: make getStartPos available in ILanguageElement */
					IASTNode astNode = (IASTNode) node;
					if(getLookup().refOffset < astNode.getStartPos()) {
						return;
					}
				}
				
				if(!importsOnly && node instanceof INamedElement) {
					INamedElement namedElement = (INamedElement) node;
					visitNamedElement(namedElement, importsOnly);
				}
				
				if(node instanceof INonScopedContainer) {
					INonScopedContainer container = ((INonScopedContainer) node);
					evaluateScopeElements(container.getMembersIterable(), 
						isSequentialLookup, importsOnly, scopeAsImport);
				}
				
				node.evaluateForScopeLookup(this, importsOnly, isSequentialLookup, scopeAsImport);
			}
		}
		
		public void visitNamedElement(INamedElement namedElement, boolean isImportsScope) {
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
			
			if(!isImportsScope) {
				names.addSymbol(namedElement);
			} else {
				importedNames.addSymbol(namedElement);
			}
		}
		
		public void addImportNameElement(ImportContent importStatic) {
			INamedElement namedElement = importStatic.moduleRef.getNamespaceFragment(getContext());
			visitNamedElement(namedElement, false);
		}
		
		public SymbolTable addImportedNamesToSymbolTable() {
			names.addVisibleSymbols(importedNames);
			return names;
		}
		
		public void addModuleImport(SymbolTable moduleNames) {
			if(moduleNames != null) {
				importedNames.addSymbols(moduleNames);
			}
		}
		
	}
	
}