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
package dtool.ast.references;

import java.util.Collection;

import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.engine.resolver.IResolvable;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.expressions.Resolvable;

/**
 * Common class for entity references.
 */
public abstract class Reference extends Resolvable implements IResolvable {
	
	@Override
	public Collection<INamedElement> findTargetDefElements(ISemanticContext moduleResolver, boolean findOneOnly) {
		return getSemantics().findTargetDefElements(moduleResolver, findOneOnly);
	}
	
	@Deprecated
	protected static Collection<INamedElement> resolveToInvalidValue() {
		return null; 
	}
	
}