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
package dtool.ast.statements;

import java.util.Iterator;

import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.engine.common.INonScopedContainer;
import dtool.util.ArrayView;

public class BlockStatementUnscoped extends CommonStatementList implements INonScopedContainer {
	
	public BlockStatementUnscoped(ArrayView<IStatement> nodes) {
		super(nodes);
	}
	
	public BlockStatementUnscoped() {
		super();
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.BLOCK_STATEMENT_UNSCOPED;
	}
	
	@Override
	public Iterator<? extends ASTNode> getMembersIterator() {
		return super.getMembersIterator();
	}
	
}