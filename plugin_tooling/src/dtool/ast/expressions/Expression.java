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
package dtool.ast.expressions;


import java.util.Collection;
import java.util.Collections;

import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.engine.resolver.IResolvableSemantics;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.references.IQualifierNode;

public abstract class Expression extends Resolvable implements IQualifierNode, IInitializer {
	
	/* -----------------  ----------------- */
	
	@Override
	public IResolvableSemantics getSemantics() {
		return semantics;
	}
	
	protected final IResolvableSemantics semantics = new ResolvableSemantics(this) {
		
		@Override
		public Collection<INamedElement> findTargetDefElements(ISemanticContext mr, boolean findOneOnly) {
			return Expression.this.findTargetDefElements(mr, true); // TODO
		}
		
		@Override
		public Collection<INamedElement> resolveTypeOfUnderlyingValue(ISemanticContext mr) {
			return findTargetDefElements(mr, true); // TODO
		}
		
	};
	
	@Override
	public Collection<INamedElement> findTargetDefElements(ISemanticContext mr, boolean findFirstOnly) {
		return Collections.emptySet();
	}
	
}