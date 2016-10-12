/*******************************************************************************
 * Copyright (c) 2011 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.statements;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.engine.scoping.ScopeTraverser;

/**
 * A scoped statement list. Used by case/default statements
 */
public class ScopedStatementList extends CommonStatementList implements IScopeElement {
	
	public ScopedStatementList(NodeVector<IStatement> statements) {
		super(assertNotNull(statements));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.SCOPED_STATEMENT_LIST;
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new ScopedStatementList(clone(statements));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendList("\n", statements_asNodes(), "\n", "\n");
	}
	
	@Override
	public ScopeTraverser getScopeTraverser() {
		return new ScopeTraverser(statements, false);
	}
	
}