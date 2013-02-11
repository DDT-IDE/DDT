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


public class ASTNeoHomogenousVisitor extends ASTNeoDefaultVisitor implements IASTNeoVisitor {
	
	public void traverse(ASTNeoNode node) {
		node.accept(this);
	}
	
	@Override
	public boolean preVisit(ASTNeoNode node) {
		genericVisit(node);
		return true;
	}
	
	@SuppressWarnings("unused") 
	public void genericVisit(ASTNeoNode node) {
	}
	
}
