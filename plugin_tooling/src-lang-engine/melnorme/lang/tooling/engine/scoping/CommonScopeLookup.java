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

import java.util.HashSet;
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
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.scoping.IScopeElement.IExtendedScopeElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.EntriesMap;
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
	
	/** Return whether the search has found all matches. */
	public abstract boolean isFinished();
	
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
		
		evaluateScopeElements(scope.getScopeNodeList(), !scope.allowsForwardReferences());
		
		if(scope instanceof IExtendedScopeElement) {
			IExtendedScopeElement extendedScopeElement = (IExtendedScopeElement) scope;
			// Warning: potential infinite loop problems here 
			extendedScopeElement.resolveLookupInSuperScopes(this);
		}
		
	}
	
	protected void evaluateScopeElements(Iterable<? extends ILanguageElement> nodeIterable, boolean isSequential) {
		if(nodeIterable == null)
			return;
		
		ScopeNameResolution scopeResolution = new ScopeNameResolution();
		scopeResolution.evaluateScopeElements(nodeIterable, isSequential, false);
		scopeResolution.evaluateScopeElements(nodeIterable, isSequential, true);
		
		scopeResolution.addScopeMatchesToLookup();
		
	}
	
	public static class NamesMap extends EntriesMap<String, ArrayList2<INamedElement>> {
		
		@Override
		protected ArrayList2<INamedElement> createEntry(String key) {
			return new ArrayList2<>();
		}
		
	}
	
	public class ScopeNameResolution {
		
		protected NamesMap names = new NamesMap();
		protected NamesMap importedNames = new NamesMap();
		
		public ISemanticContext getContext() {
			return modResolver;
		}
		
		public CommonScopeLookup getLookup() {
			return CommonScopeLookup.this;
		}
		
		protected void evaluateScopeElements(Iterable<? extends ILanguageElement> nodeIter, boolean isSequentialLookup, 
				boolean importsOnly) {
			
			// Note: don't check for isFinished() during the loop
			for (ILanguageElement node : nodeIter) {
				
				// Check if we have passed the reference offset
				if(isSequentialLookup && node instanceof IASTNode) {
					/* FIXME: make getStartPos available in ILanguageElement */
					IASTNode astNode = (IASTNode) node;
					if(refOffset < astNode.getStartPos()) {
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
		
		public void evaluateNamedElementForSearch(INamedElement namedElement, boolean isImportsScope) {
			if(namedElement != null) {
				visitNamedElement(namedElement, isImportsScope);
			}
		}
		
		public void visitNamedElement(INamedElement namedElement, boolean isImportsScope) {
			String name = getNameToMatch(namedElement);
			if(name == null || name.isEmpty()) {
				// Never match an element with missing name;
				return;
			}
			if(!matchesName(name)) {
				return;
			}
			
			NamesMap namesMap = isImportsScope ? importedNames : names;
			
			ArrayList2<INamedElement> entry = namesMap.getEntry(name);
			entry.add(namedElement);
			
			addMatch(namedElement);
		}
		
		public NamesMap getCombinedScopeNames() {
			for (Entry<String, ArrayList2<INamedElement>> nameEntry : importedNames.getMap().entrySet()) {
				String matchedName = nameEntry.getKey();
				ArrayList2<INamedElement> importedNamesEntry = nameEntry.getValue();
				
				if(names.getEntryOrNull(matchedName) == null) {
					// Add imported scope name to main scope.
					names.getMap().put(matchedName, importedNamesEntry);
				}
			}
			return names;
		}
		
		public void addScopeMatchesToLookup() {
			NamesMap names = getCombinedScopeNames();
			
			for (Entry<String, ArrayList2<INamedElement>> nameEntry : names.getMap().entrySet()) {
				String matchedName = nameEntry.getKey();
				ArrayList2<INamedElement> namesEntry = nameEntry.getValue();
				
				addNameEntry(matchedName, namesEntry);
			}
		}

		protected void addNameEntry(String matchedName, ArrayList2<INamedElement> namesEntry) {
			assertTrue(namesEntry.size() > 0);
			
			if(namesEntry.size() == 1) {
				// simplest case, add element directly:
				INamedElement namedElement = namesEntry.get(0);
				matches2.put(matchedName, namedElement);
			}
			
			// we have an overload set, need to check contents.
			
			INamedElement firstElement = namesEntry.get(0);
			ILanguageElement parent = firstElement.getParent();
			
			OverloadedNamedElement overloadedElement = new OverloadedNamedElement(namesEntry, parent) {
				
				@Override
				protected NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
					/*FIXME: BUG here, todo*/
					return null;
				}
			};
			matches2.put(matchedName, overloadedElement);
		}
		
	}
	
}