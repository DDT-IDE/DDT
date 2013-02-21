/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast;


/**
 * By default, visits a node's children only up to a defined depth;
 */
public class ASTChildrenVisitor extends ASTDefaultVisitor {
	
	protected int maxDepth = 1;
	protected int depth = 0; 
	
	@Override
	public boolean preVisit(ASTNeoNode node) {
		depth++;
		if(depth != 1) {
			geneticChildrenVisit(node);
		}
		return depth <= maxDepth;
	}
	
	@Override
	public void postVisit(ASTNeoNode node) {
		depth--;
	}
	
	@SuppressWarnings("unused") 
	protected void geneticChildrenVisit(ASTNeoNode child) {
	}
	
}