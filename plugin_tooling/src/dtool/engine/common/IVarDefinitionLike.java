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
package dtool.engine.common;

import dtool.ast.IASTNode;
import dtool.ast.definitions.INamedElement;
import dtool.ast.expressions.IInitializer;
import dtool.ast.references.Reference;
import dtool.engine.operations.CommonDefVarSemantics;

/**
 * Interface for nodes similar to a variable definition (basically defUnits that have an associated type).
 */
public interface IVarDefinitionLike extends INamedElement, IASTNode {
	
	Reference getDeclaredType();
	
	IInitializer getDeclaredInitializer();
	
	CommonDefVarSemantics getNodeSemantics();
	
}