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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.ast.IModuleElement;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.context.ModuleSourceException;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.OverloadedNamedElement;
import melnorme.lang.tooling.engine.scoping.IScopeElement.IExtendedScopeElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.core.fntypes.Function;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.declarations.ImportContent;
import dtool.engine.analysis.ModuleProxy;
import dtool.engine.analysis.PackageNamespace;
import dtool.engine.analysis.PackageNamespaceFragment;

public abstract class CommonScopeLookup {
	
	/** Flag for stop searching when suitable matches are found. */
	public final boolean findOnlyOne;
	/** The module where the search started. */
	public final IModuleElement refOriginModule;
	/** The offset of the reference. 
	 * Used to check availability in statement scopes. */
	public final int refOffset;
	/** Module Resolver */
	public final ISemanticContext modResolver; // TODO will need to deprecate this field eventually.
	
	protected final ArrayList2<INamedElement> matches = new ArrayList2<>(2);
	protected final HashMap<String, INamedElement> matches2 = new HashMap<String, INamedElement>();
	
	/** The scopes that have already been searched */
	protected final HashSet<IScopeElement> searchedScopes = new HashSet<>(4);
	
	/** Named elements for which evaluateInMembersScope() has been called for. */
	protected final HashSet<INamedElement> resolvedElementsForMemberScopes = new HashSet<>(4);;
	
	
	public CommonScopeLookup(IModuleElement refOriginModule, int refOffset, ISemanticContext moduleResolver) {
		this(refOriginModule, refOffset, false, moduleResolver);
	}
	
	public CommonScopeLookup(IModuleElement refOriginModule, int refOffset, boolean findOneOnly, 
			ISemanticContext moduleResolver) { 
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
	
	@Deprecated
	public List<INamedElement> getMatchedElements() {
		return matches;
	}
	public Collection<INamedElement> getMatchedElements2() {
		return matches2.values();
	}
	
	/** Return whether the search has found all matches. */
	public abstract boolean isFinished();
	
	/** Returns whether this search matches the given name or not. */
	public abstract boolean matchesName(String name);
	
	public void addMatch(INamedElement namedElement) {
		matches.add(namedElement);
	}
	
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
	 * Evaluate a scope (a collection of nodes), for this name lookup search. 
	 */
	public void evaluateScope(IScopeElement scope) {
		if(scope == null)
			return;
		
		if(isFinished())
			return;
		
		if(searchedScopes.contains(scope))
			return;
		searchedScopes.add(scope);
		
		evaluateScopeElements(scope);
		
		if(scope instanceof IExtendedScopeElement) {
			IExtendedScopeElement extendedScopeElement = (IExtendedScopeElement) scope;
			// Warning: potential infinite loop problems here 
			extendedScopeElement.resolveLookupInSuperScopes(this);
		}
		
	}
	
	public void evaluateScopeElements(IScopeElement scope) {
		evaluateScopeElements(scope.getScopeNodeList(), !scope.allowsForwardReferences());
	}
	
	public ScopeNameResolution evaluateScopeElements(Iterable<? extends ILanguageElement> nodeIterable, 
			boolean isSequential) {
		if(nodeIterable == null)
			return null;
		
		ScopeNameResolution scopeResolution = new ScopeNameResolution(this);
		scopeResolution.evaluateScopeElements(nodeIterable, isSequential);
		
		matches2.putAll(scopeResolution.names);
		matches2.putAll(scopeResolution.importedNames); /*FIXME: BUG here*/
		
		return scopeResolution;
	}
	
	@SuppressWarnings("serial")
	public static class NamesMap extends HashMap<String, INamedElement> {
		
	}
	
	public static class ScopeNameResolution {
		
		protected final CommonScopeLookup lookup;
		
		protected NamesMap names = new NamesMap();
		protected NamesMap importedNames = new NamesMap();
		
		public ScopeNameResolution(CommonScopeLookup lookup) {
			this.lookup = lookup;
		}

		public CommonScopeLookup getLookup() {
			return lookup;
		}
		
		public ISemanticContext getContext() {
			return getLookup().modResolver;
		}
		
		public void evaluateScopeElements(Iterable<? extends ILanguageElement> nodeIterable, boolean isSequential) {
			evaluateScopeElements(nodeIterable, isSequential, false);
			evaluateScopeElements(nodeIterable, isSequential, true);
		}
		
		public void evaluateScopeElements(Iterable<? extends ILanguageElement> nodeIter, boolean isSequentialLookup, 
				boolean importsOnly) {
			
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
					evaluateScopeElements(container.getMembersIterable(), isSequentialLookup, importsOnly);
				}
				
				node.evaluateForScopeLookup(this, importsOnly, isSequentialLookup);
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
			getLookup().addMatch(namedElement); /* FIXME: deprecate */
			
			if(!isImportsScope) {
				addSymbolToNamespace(names, namedElement);
			} else {
				addSymbolToNamespace(importedNames, namedElement);
			}
		}
		
		protected static void addSymbolToNamespace(NamesMap namesMap, INamedElement newElement) {
			String name = newElement.getNameInRegularNamespace();
			
			INamedElement existingNamedElement = namesMap.get(name);
			
			if(existingNamedElement instanceof PackageNamespace && newElement instanceof PackageNamespaceFragment) {
				PackageNamespace existingNamespace = (PackageNamespace) existingNamedElement;
				PackageNamespaceFragment newNamespace = (PackageNamespaceFragment) newElement;
				
				INamedElement containedElement = newNamespace.getContainedElement();
				addSymbolToNamespace(existingNamespace.getNamedElements(), containedElement);
			} else {
				newElement = convertNameSpace(newElement);
				addEntryToMap(namesMap, name, newElement);
			}
		}
		
		protected static INamedElement convertNameSpace(INamedElement newElement) {
			if(newElement instanceof PackageNamespaceFragment) {
				PackageNamespaceFragment newNamespace = (PackageNamespaceFragment) newElement;
				// convert to PackageNamespace
				String fqn = newNamespace.getFullyQualifiedName();
				ILanguageElement parent = newNamespace.getParent();
				INamedElement containedElement = convertNameSpace(newNamespace.getContainedElement());
				return new PackageNamespace(fqn, parent, containedElement);
			}
			return newElement;
		}
		
		protected static void addEntryToMap(NamesMap namesMap, String name, INamedElement newElement) {
			INamedElement existingEntry = namesMap.get(name);
			if(existingEntry == null) {
				namesMap.put(name, newElement);
			} else {
				if(existingEntry instanceof ModuleProxy && newElement instanceof ModuleProxy) {
					assertTrue(existingEntry.getFullyQualifiedName().equals(newElement.getFullyQualifiedName()));
					// Don't add duplicated element.
					return;
				}
				
				OverloadedNamedElement overloadElement;
				
				if(existingEntry instanceof OverloadedNamedElement) {
					overloadElement = (OverloadedNamedElement) existingEntry;
				} else {
					// Give priority to ModuleProxy element (note: this isn't entirely like DMD behavior
					if(newElement instanceof ModuleProxy && existingEntry instanceof PackageNamespace) {
						namesMap.put(name, newElement);
						return;
					}
					if(newElement instanceof PackageNamespace && existingEntry instanceof ModuleProxy) {
						return;
					}
					
					overloadElement = new OverloadedNamedElement(existingEntry, existingEntry.getParent());
					namesMap.put(name, overloadElement);
				}
				overloadElement.addElement(newElement);
			}
		}
		
		public void addImportNameElement(ImportContent importStatic) {
			INamedElement namedElement = importStatic.moduleRef.getNamespaceFragment(getContext());
			visitNamedElement(namedElement, false);
		}
		
		/* -----------------  ----------------- */
		
		public NamesMap getCombinedScopeNames() {
			for (Entry<String, INamedElement> nameEntry : importedNames.entrySet()) {
				String matchedName = nameEntry.getKey();
				INamedElement matchedElement = nameEntry.getValue();
				
				if(names.get(matchedName) == null) {
					// Add imported scope name to main scope.
					names.put(matchedName, matchedElement);
				}
			}
			return names;
		}
		
	}
	
}