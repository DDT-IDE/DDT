/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.engine.scoping;

import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup.ScopeNameResolution;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.SymbolTable;

public class ScopeTraverser {
	
	protected final boolean scopeAsImport;
	protected final boolean isSequentialLookup;
	protected final Iterable<? extends ILanguageElement> nodeIter;
	
	public ScopeTraverser(Iterable<? extends ILanguageElement> nodeIter, boolean allowsForwardReferences) {
		this(nodeIter, allowsForwardReferences, false);
	}
	
	public ScopeTraverser(Iterable<? extends ILanguageElement> nodeIter, boolean allowsForwardReferences, 
			boolean scopeAsImport) {
		this.nodeIter = nodeIter;
		this.isSequentialLookup = !allowsForwardReferences;
		this.scopeAsImport = scopeAsImport;
	}
	
	public SymbolTable evaluateScope(ScopeNameResolution scopeResolution, int refOffset, boolean isSecondaryScope) {
		if(nodeIter != null) {
			evaluateScopeElements(scopeResolution, nodeIter, refOffset, isSequentialLookup, isSecondaryScope);
		}
		
		return scopeResolution.names;
	}
	
	public void evaluateScopeElements(ScopeNameResolution scopeResolution, 
			Iterable<? extends ILanguageElement> nodeIter, 
			int refOffset, boolean isSequentialLookup, boolean isSecondaryScope) {
		
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
			
			if(node instanceof INamedElement) {
				INamedElement namedElement = (INamedElement) node;
				scopeResolution.visitNamedElement(namedElement);
			}
			
			node.evaluateForScopeLookup(scopeResolution, isSecondaryScope, isSequentialLookup, scopeAsImport);
			
			if(node instanceof INonScopedContainer) {
				INonScopedContainer container = ((INonScopedContainer) node);
				evaluateScopeElements(scopeResolution, container.getMembersIterable(), refOffset, 
					isSequentialLookup, isSecondaryScope);
			}
			
		}
	}
}