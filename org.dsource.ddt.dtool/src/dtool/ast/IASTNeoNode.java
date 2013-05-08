/*******************************************************************************
 * Copyright (c) 2011, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast;

import melnorme.utilbox.tree.IElement;
import melnorme.utilbox.tree.IVisitable;
import descent.internal.compiler.parser.ast.IASTNode;

/**
 * Interface for {@link ASTNode} objects. No other class can implement. 
 */
public interface IASTNeoNode extends IASTNode, IElement, IVisitable<IASTVisitor> {
	
	public ASTNode asNode();
	
	@Override
	public ASTNode getParent();
	
}
