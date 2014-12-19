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
package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.OverloadedNamedElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.IResolvable;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;
import dtool.ast.references.Reference;

/**
 * A {@link Resolvable} is either an {@link Reference} or {@link Expression}
 */
public abstract class Resolvable extends ASTNode implements IResolvable {
	
	public Resolvable() {
		assertTrue(this instanceof Reference || this instanceof Expression);
	}
	
	@Override
	public ResolvableSemantics getSemantics(ISemanticContext parentContext) {
		return (ResolvableSemantics) super.getSemantics(parentContext);
	}
	@Override
	protected abstract ResolvableSemantics doCreateSemantics(PickedElement<?> pickedElement);
	
	public final INamedElement resolveTargetElement(ISemanticContext context) {
		return getSemantics(context).resolveTargetElement().result;
	}
	
	
	@Deprecated
	public final Collection<INamedElement> findTargetDefElements(ISemanticContext context) {
		return resolveResultToCollection(resolveTargetElement(context));
	}
	
	public static Collection<INamedElement> resolveResultToCollection(INamedElement result) {
		if(result instanceof OverloadedNamedElement) {
			OverloadedNamedElement overloadedNamedElement = (OverloadedNamedElement) result;
			return overloadedNamedElement.getOverloadedElements();
		} else {
			return new ArrayList2<>(result);
		}
	}
	
	/* ----------------- ----------------- */
	
	public static INamedElement findTargetElementsForReference(ISemanticContext context, Resolvable resolvable) {
		if(resolvable == null) {
			return null;
		}
		return resolvable.getSemantics(context).resolveTargetElement().result;
	}
	
}