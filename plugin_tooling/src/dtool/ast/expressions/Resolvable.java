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
import java.util.Collections;

import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.engine.resolver.IResolvable;
import melnorme.lang.tooling.engine.resolver.IResolvableSemantics;
import melnorme.lang.tooling.engine.resolver.ResolvableResult;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.references.Reference;

/**
 * A {@link Resolvable} is either an {@link Reference} or {@link Expression}
 */
public abstract class Resolvable extends ASTNode implements IResolvable {
	
	public Resolvable() {
		assertTrue(this instanceof Reference || this instanceof Expression);
	}
	
	@Override
	public abstract IResolvableSemantics getSemantics();
	
	@Override
	public final Collection<INamedElement> resolveTypeOfUnderlyingValue(ISemanticContext mr) {
		return getSemantics().resolveTypeOfUnderlyingValue(mr);
	}
	
	public final INamedElement findTargetDefElement(ISemanticContext context) {
		return getSemantics().resolveTargetElement(context).result;
	}
	
	public final ResolvableResult resolveTargetElement(ISemanticContext sr) {
		return getSemantics().resolveTargetElement(sr);
	}
	
	@Override
	public final Collection<INamedElement> findTargetDefElements(ISemanticContext mr, boolean findFirstOnly) {
		return getSemantics().findTargetDefElements(mr, findFirstOnly);
	}
	
	/* ----------------- ----------------- */
	
	/** Convenience method for wraping a single defunit as a search result. */
	public static Collection<INamedElement> wrapResult(INamedElement elem) {
		if(elem == null)
			return null;
		return Collections.singletonList(elem);
	}
	
	public static Collection<INamedElement> findTargetElementsForReference(ISemanticContext mr, Resolvable resolvable,
		boolean findFirstOnly) {
		if(resolvable == null) {
			return null;
		}
		return resolvable.getSemantics().findTargetDefElements(mr, findFirstOnly);
	}
	
}