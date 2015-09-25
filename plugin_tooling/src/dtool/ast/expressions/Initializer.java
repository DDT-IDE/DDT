/*******************************************************************************
 * Copyright (c) 2012 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.expressions;

import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.symbols.INamedElement;

/**
 * Default class for initializers.
 * 
 * @see http://dlang.org/declaration.html#Initializer
 */
public abstract class Initializer extends ASTNode implements IInitializer {
	
	@Override
	public INamedElement getTypeOfInitializer(ISemanticContext context) {
		// TODO resolveTypeOfUnderlyingValue for subclasses
		return ErrorElement.newUnsupportedError(this, null);
	}
	
}