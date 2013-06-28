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

/**
 * Interface for {@link ASTNode} objects. No other class can implement. 
 */
public interface IASTNode extends IElement, IVisitable<IASTVisitor> {
	
	int getOffset();
	int getLength();
	
	int getStartPos();
	int getEndPos();
	
	@Deprecated
	boolean hasNoSourceRangeInfo();
	
	String toStringAsNode(boolean printRangeInfo);
	
	@Override
	public IASTNode[] getChildren(); // Redefined to refine the type of children
	
	public ASTNode asNode();
	
	@Override
	public ASTNode getParent();
	
	public void setParent(ASTNode newParent);
	
}