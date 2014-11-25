/*******************************************************************************
 * Copyright (c) 2012, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.ast.util;

import java.util.ArrayList;
import java.util.List;

import melnorme.lang.tooling.ast.ASTVisitor;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.tree.IVisitable;

/**
 * Uses a Visitor to collect a node's children.
 */
public class ASTChildrenCollector extends ASTVisitor {
	
	private boolean visitingParent = true;
	private List<ASTNode> childrenLst;
	
	public static ASTNode[] getChildrenArray(ASTNode elem){
		return ArrayUtil.createFrom(getChildrenList(elem), ASTNode.class);
	}
	
	public static List<ASTNode> getChildrenList(IVisitable<? super IASTVisitor> elem){
		ASTChildrenCollector collector = new ASTChildrenCollector();
		collector.childrenLst = new ArrayList<ASTNode>();
		elem.accept(collector);
		return collector.childrenLst;
	}
	
	
	@Override
	public boolean preVisit(ASTNode node) {
		if(visitingParent == true) {
			visitingParent = false;
			return true; // visit children
		}
		
		// visiting children
		childrenLst.add(node);
		return false;
	}
	
	@Override
	public void postVisit(ASTNode node) {
		// Do nothing
	}
}
