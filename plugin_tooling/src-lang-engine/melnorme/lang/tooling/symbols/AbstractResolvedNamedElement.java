/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.symbols;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.ast.CommonLanguageElement;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.INamedElementSemanticData;

/**
 * Resolved elements have their semantic data already resolved.
 * This is possible for nodes whose semantic resolution only depends on the node itself, 
 * and not on parent nodes or other external data.
 */
public abstract class AbstractResolvedNamedElement extends AbstractNamedElement {
	
	public AbstractResolvedNamedElement(String name, CommonLanguageElement lexicalParent) {
		super(name, lexicalParent, null);
	}
	
	@Override
	public INamedElementSemanticData getSemantics(ISemanticContext parentContext) {
		assertTrue(isSemanticReady());
		return doGetSemantics();
	}
	
	@Override
	protected INamedElementSemanticData doCreateSemantics(PickedElement<?> pickedElement) {
		throw assertFail();
	}
	
	protected abstract INamedElementSemanticData doGetSemantics();
	
	@Override
	public ISemanticContext getElementSemanticContext(ISemanticContext parentContext) {
		return parentContext; // We might change this API to null, so far this code only runs for tests.
	}
	
}