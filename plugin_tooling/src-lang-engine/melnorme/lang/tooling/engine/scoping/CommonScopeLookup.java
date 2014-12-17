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
import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.ast.IModuleElement;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.context.ModuleSourceException;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.scoping.IScopeElement.IExtendedScopeElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
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
	
	/* FIXME: need to review this code, possibly remove importsOnly. */
	protected void evaluateScopeElements(Iterable<? extends ILanguageElement> nodeIterable, boolean isSequential) {
		if(nodeIterable == null)
			return;
		
		ScopeNameResolution scopeResolution = new ScopeNameResolution();
		scopeResolution.evaluateScopeElements(nodeIterable, isSequential, false);
		scopeResolution.evaluateScopeElements(nodeIterable, isSequential, true);
	}
	
	public class ScopeNameResolution {
		
//		protected HashMap<String, ArrayList2<INamedElement>> names = new HashMap<>(2);

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
					visitNamedElement(namedElement);
				}
				
				if(node instanceof INonScopedContainer) {
					INonScopedContainer container = ((INonScopedContainer) node);
					evaluateScopeElements(container.getMembersIterable(), isSequentialLookup, importsOnly);
				}
				
				node.evaluateForScopeLookup(CommonScopeLookup.this, importsOnly, isSequentialLookup);
			}
		}
		
	}
	
	/* FIXME: */
	@Deprecated
	public void evaluateNamedElementForSearch(INamedElement namedElement) {
		if(namedElement != null) {
			visitNamedElement(namedElement);
		}
	}
	
	public void visitNamedElement(INamedElement namedElement) {
		String name = getNameToMatch(namedElement);
		if(name == null || name.isEmpty()) {
			// Never match an element with missing name;
			return;
		}
		if(!matchesName(name)) {
			return;
		}
		
		addMatch(namedElement);
	}
	
}