/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.ast;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;

import melnorme.lang.tooling.ast.util.NodeElementUtil;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.IElementSemanticData;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup.ScopeNameResolution;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.PackageNamespace;
import melnorme.utilbox.misc.Location;
import dtool.ast.definitions.EArcheType;


public abstract class CommonLanguageElement implements ILanguageElement {
	
	public CommonLanguageElement() {
	}
	
	@Override
	public abstract CommonLanguageElement getLexicalParent();
	
	public abstract ILanguageElement getOwnerElement();
	
	@Override
	public boolean isLanguageIntrinsic() {
		return getOwnerElement() == null ? true : getOwnerElement().isLanguageIntrinsic();
	}
	
	/* ----------------- INamedElement utils ----------------- */
	
	@Override
	public String getModuleFullName() {
		INamedElement module = getModuleElement();
		return module == null ? null : module.getFullyQualifiedName();
	}
	
	public INamedElement getModuleElement() {
		INamedElement namedElement = NodeElementUtil.getNearestNamedElement(this);
		while(namedElement != null) {
			if(namedElement.getArcheType() == EArcheType.Module) {
				return namedElement;
			}
			namedElement = NodeElementUtil.getOuterNamedElement(namedElement);
		}
		return null;
	}
	
	/* ----------------- semanticReady utils ----------------- */
	
	@Override
	public abstract boolean isSemanticReady();
	
	protected static <T extends ILanguageElement> T checkIsSemanticReady(T element) {
		assertTrue(element == null || element.isSemanticReady());
		return element;
	}
	
	public static <T extends Iterable<? extends ILanguageElement>> T checkAreSemanticReady(T elements, 
			boolean readyPackageNamespace) {
		
		for(ILanguageElement element : elements) {
			if(readyPackageNamespace && element instanceof PackageNamespace) {
				// PackageNamespace cannot be setCompleted early, so set it completed now
				((PackageNamespace) element).setSemanticReady();
			}
			checkIsSemanticReady(element);
		}
		return elements;
	}
	
	protected static <T extends Iterable<? extends ILanguageElement>> T checkAllSemanticReady(T elements) {
		return checkAreSemanticReady(elements, false);
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public IElementSemanticData getSemantics(ISemanticContext parentContext) {
		assertTrue(isSemanticReady());
		return doGetSemantics(parentContext);
	}
	
	public IElementSemanticData doGetSemantics(ISemanticContext parentContext) {
		ISemanticContext context = getElementSemanticContext(parentContext);
		return context.getSemanticsEntry(this);
	}
	
	@Override
	public ISemanticContext getElementSemanticContext(ISemanticContext parentContext) {
		assertNotNull(parentContext);
		if(isLanguageIntrinsic()) {
			return parentContext.getContainingBundleResolution(true, null);
		}
		
		Location loc = Location.createValidOrNull(this.getSemanticContainerKey());
		return parentContext.getContainingBundleResolution(false, loc);
	}
	
	@Override
	public Path getSemanticContainerKey() {
		return getOwnerElement() == null ? null : getOwnerElement().getSemanticContainerKey();
	}
	
	@Override
	public final IElementSemanticData createSemantics(PickedElement<?> pickedElement) {
		assertTrue(pickedElement.element == this); // Note this precondition!
		return doCreateSemantics(pickedElement);
	}
	
	@SuppressWarnings("unused")
	protected IElementSemanticData doCreateSemantics(PickedElement<?> pickedElement) {
		throw assertFail(); // Not valid unless re-implemented.
	}
	
	/* ----------------- name lookup ----------------- */
	
	/**
	 * Perform a name lookup starting in this node.
	 * The exact mechanism in which the name lookup will be performed will depend on the node, 
	 * but the most common (and default) scenario is to perform a lexical lookup.
	 */
	public final void performNameLookup(CommonScopeLookup lookup) {
		assertTrue(isSemanticReady());
		
		doPerformNameLookup(lookup);
	}
	
	protected void doPerformNameLookup(CommonScopeLookup lookup) {
		assertTrue(lookup.isSequentialLookup());
		assertTrue(lookup.refOffset >= 0);
		
		lookup.evaluateScope(ASTNode.getPrimitivesScope());
		if(lookup.isFinished())
			return;
		
		doPerformNameLookupInLexicalScope(lookup);
	}
	
	protected final void doPerformNameLookupInLexicalScope(CommonScopeLookup lookup) {
		if(this instanceof IScopeElement) {
			IScopeElement scope = (IScopeElement) this;
			lookup.evaluateScope(scope);
		}
		
		if(lookup.isFinished())
			return;
		
		CommonLanguageElement parent = getLexicalParent();
		if(parent != null) {
			parent.doPerformNameLookupInLexicalScope(lookup);
		}
	}
	
	@Override
	public void evaluateForScopeLookup(ScopeNameResolution scopeRes, boolean isSecondaryScope, 
			boolean publicImportsOnly) {
		// Default: do nothing.
	}
	
}