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
package dtool;

import dtool.ast.ASTDefaultVisitor;
import dtool.ast.ASTNode;
import dtool.ast.IASTVisitor;


public abstract class ASTNodeSearcher<T> extends ASTDefaultVisitor implements IASTVisitor {
	
	public T match;
	public boolean continueSearch = true;
	
	@Override
	public boolean preVisit(ASTNode node) {
		if(continueSearch == false)
			return false;
		
		return doPreVisit(node);
	}
	
	public abstract boolean doPreVisit(ASTNode node);
	
	@Override
	public void postVisit(ASTNode node) {
	}
	
}