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

import melnorme.lang.tooling.ast_actual.ILangNamedElement;
import dtool.ast.expressions.Resolvable.IQualifierNode;
import dtool.engine.modules.IModuleResolver;

public abstract class Expression extends Resolvable implements IQualifierNode, IInitializer {
	
	@Override
	public Collection<ILangNamedElement> resolveTypeOfUnderlyingValue(IModuleResolver mr) {
		return findTargetDefElements(mr, true); // TODO
	}
	
	@Override
	public Collection<ILangNamedElement> findTargetDefElements(IModuleResolver mr, boolean findFirstOnly) {
		return Collections.emptySet();
	}
	
}