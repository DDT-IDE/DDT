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
package dtool.ast.util;

import dtool.ast.ASTNode;
import dtool.ast.ASTVisitor;


/**
 * Utility Visitor to visit node's direct children.
 */
public class ASTDirectChildrenVisitor extends ASTVisitor {
	
	protected static final int maxDepth = 1;
	protected int depth = 0; 
	
	@Override
	public boolean preVisit(ASTNode node) {
		if(depth == 1) {
			geneticChildrenVisit(node);
		}
		depth++;
		return depth <= maxDepth;
	}
	
	@Override
	public void postVisit(ASTNode node) {
		depth--;
	}
	
	@SuppressWarnings("unused") 
	protected void geneticChildrenVisit(ASTNode child) {
	}
	
}