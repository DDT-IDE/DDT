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

import dtool.ast.references.IQualifierNode;
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.symbols.INamedElement;

public abstract class Expression extends Resolvable implements IQualifierNode, IInitializer {
	
	@Override
	public Collection<INamedElement> resolveTypeOfUnderlyingValue(ISemanticContext mr) {
		return findTargetDefElements(mr, true); // TODO
	}
	
	@Override
	public Collection<INamedElement> findTargetDefElements(ISemanticContext mr, boolean findFirstOnly) {
		return Collections.emptySet();
	}
	
}