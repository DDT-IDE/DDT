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


/**
 * Default generic visitor
 */
public class ASTVisitor implements IASTVisitor {
	
	public static final boolean VISIT_CHILDREN = true;
	public static final boolean DONT_VISIT_CHILDREN = false;
	
	@Override
	public boolean preVisit(ASTNode node) {
		return true;
	}
	
	@Override
	public void postVisit(ASTNode node) {
	}
	
}