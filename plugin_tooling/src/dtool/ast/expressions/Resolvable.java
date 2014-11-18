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
import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.bundles.ISemanticResolution;
import melnorme.lang.tooling.engine.resolver.AbstractResolvableSemantics;
import melnorme.lang.tooling.engine.resolver.IResolvable;
import melnorme.lang.tooling.engine.resolver.IResolvableSemantics;
import melnorme.lang.tooling.engine.resolver.ResolutionResult;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.references.Reference;

/**
 * A {@link Resolvable} is either an {@link Reference} or {@link Expression}
 */
public abstract class Resolvable extends ASTNode implements IResolvable {
	
	public Resolvable() {
		assertTrue(this instanceof Reference || this instanceof Expression);
	}
	
	protected final IResolvableSemantics defaultResolvableSemantics = new AbstractResolvableSemantics() {
		
		@Override
		public ResolutionResult resolveTargetElement(ISemanticResolution sr) {
			return new ResolutionResult(findTargetDefElement(sr));
		}
		
		@Override
		public Collection<INamedElement> findTargetDefElements(IModuleResolver mr, boolean findOneOnly) {
			return Resolvable.this.findTargetDefElements(mr, findOneOnly);
		}
		
	};
	
	@Override
	public IResolvableSemantics getNodeSemantics() {
		return defaultResolvableSemantics;
	}
	
	@Override
	public Collection<INamedElement> resolveTypeOfUnderlyingValue(IModuleResolver mr) {
		return getNodeSemantics().resolveTypeOfUnderlyingValue(mr);
	}
	
	public final INamedElement findTargetDefElement(IModuleResolver moduleResolver) {
		return getNodeSemantics().findTargetDefElement(moduleResolver);
	}
	
	public final ResolutionResult resolveTargetElement(ISemanticResolution sr) {
		return getNodeSemantics().resolveTargetElement(sr);
	}
	
	/* ----------------- ----------------- */
	
	/** Convenience method for wraping a single defunit as a search result. */
	public static Collection<INamedElement> wrapResult(INamedElement elem) {
		if(elem == null)
			return null;
		return Collections.singletonList(elem);
	}
	
	public static Collection<INamedElement> findTargetElementsForReference(IModuleResolver mr, Resolvable resolvable,
		boolean findFirstOnly) {
		if(resolvable == null) {
			return null;
		}
		return resolvable.findTargetDefElements(mr, findFirstOnly);
	}
	
}