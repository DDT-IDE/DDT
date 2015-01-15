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

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.scoping.INonScopedContainer;
import melnorme.utilbox.misc.IteratorUtil;

public class BlockStatementUnscoped extends CommonStatementList implements INonScopedContainer {
	
	public BlockStatementUnscoped(NodeVector<IStatement> nodes) {
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
	protected CommonASTNode doCloneTree() {
		return statements == null ? new BlockStatementUnscoped() : new BlockStatementUnscoped(clone(statements));
	}
	
	@Override
	public Iterable<? extends IASTNode> getMembersIterable() {
		return IteratorUtil.nonNullIterable(statements_asNodes());
	}
	
}