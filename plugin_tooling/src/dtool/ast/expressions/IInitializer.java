/*******************************************************************************
 * Copyright (c) 2012, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.expressions;

import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.resolver.TypeReferenceResult;

public interface IInitializer extends IASTNode {
	
	TypeReferenceResult resolveTypeOfUnderlyingValue(ISemanticContext context);
	
}