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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;

import melnorme.lang.tooling.ast.util.NodeElementUtil;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.IElementSemanticData;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup.ScopeNameResolution;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.PackageNamespace;
import melnorme.utilbox.collections.Collection2;
import melnorme.utilbox.misc.Location;
import dtool.ast.definitions.EArcheType;


public abstract class CommonLanguageElement implements ILanguageElement {
	
	public CommonLanguageElement() {
	}
	
	@Override
	public abstract ILanguageElement getLexicalParent();
	public abstract ILanguageElement getOwnerElement();
	
	
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
	
	/* -----------------  ----------------- */

	public static boolean isCompleted(ILanguageElement element) {
		return element == null || element.isCompleted();
	}
	
	public static void doCheckCompleted(ILanguageElement element) {
		if(element instanceof PackageNamespace) {
			// PackageNamespace cannot be setCompleted early, so set it completed now
			((PackageNamespace) element).setCompleted();
		}
		assertTrue(element.isCompleted());
	}
	
	public static void doCheckCompleted(Collection2<? extends ILanguageElement> elements) {
		for (ILanguageElement element : elements) {
			doCheckCompleted(element);
		}
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public IElementSemanticData getSemantics(ISemanticContext parentContext) {
		assertTrue(isCompleted());
		return doGetSemantics(parentContext);
	}
	
	public IElementSemanticData doGetSemantics(ISemanticContext parentContext) {
		ISemanticContext context = getElementSemanticContext(parentContext);
		return context.getSemanticsEntry(this);
	}
	
	@Override
	public ISemanticContext getElementSemanticContext(ISemanticContext parentContext) {
		if(isLanguageIntrinsic()) {
			return parentContext.getContainingBundleResolution(true, null);
		}
		
		Location loc = Location.createValidOrNull(this.getSemanticContainerKey());
		return parentContext.getContainingBundleResolution(false, loc);
	}
	
	@Override
	public boolean isLanguageIntrinsic() {
		return getOwnerElement() == null ? true : getOwnerElement().isLanguageIntrinsic();
	}
	
	@Override
	public Path getSemanticContainerKey() {
		return getOwnerElement() == null ? null : getOwnerElement().getSemanticContainerKey();
	}
	
	@Override
	public IElementSemanticData createSemantics(PickedElement<?> pickedElement) {
		assertTrue(pickedElement.element == this); // Note this precondition!
		return doCreateSemantics(pickedElement);
	}
	
	@SuppressWarnings("unused")
	protected IElementSemanticData doCreateSemantics(PickedElement<?> pickedElement) {
		throw assertFail(); // Not valid unless re-implemented.
	}
	
	@Override
	public void evaluateForScopeLookup(ScopeNameResolution scopeRes, boolean isSecondaryScope, 
			boolean publicImportsOnly) {
		// Default: do nothing.
	}
	
}