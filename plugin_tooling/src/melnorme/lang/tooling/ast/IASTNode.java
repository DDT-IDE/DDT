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
package melnorme.lang.tooling.ast;

import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.utilbox.tree.IElement;
import melnorme.utilbox.tree.IVisitable;

/**
 * Interface for {@link ASTNode} objects. No other class can implement. 
 */
public interface IASTNode extends IElement, IVisitable<IASTVisitor>	{
	
	int getOffset();
	int getLength();
	
	int getStartPos();
	int getEndPos();
	
	String toStringAsNode(boolean printRangeInfo);
	
	@Override
	public IASTNode[] getChildren(); // Redefined to refine the type of children
	
	public ASTNode asNode();
	
	@Override
	public ASTNode getParent();
	
	public void setParent(ASTNode newParent);
	
}