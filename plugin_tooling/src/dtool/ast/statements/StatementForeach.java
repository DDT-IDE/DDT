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
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.engine.scoping.ScopeTraverser;
import dtool.ast.expressions.Expression;

public class StatementForeach extends Statement implements IScopeElement {
	
	public final boolean isForeachReverse;
	public final NodeVector<ForeachVariableDef> varParams;
	public final Expression iterable;
	public final IStatement body;
	
	public StatementForeach(boolean isForeachReverse, NodeVector<ForeachVariableDef> varParams, Expression iterable,
			IStatement body) {
		this.varParams = parentize(varParams);
		this.iterable = parentize(iterable);
		this.body = parentize(body);
		this.isForeachReverse = isForeachReverse;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_FOREACH;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, varParams);
		acceptVisitor(visitor, iterable);
		acceptVisitor(visitor, body);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new StatementForeach(isForeachReverse, clone(varParams), clone(iterable), clone(body));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(isForeachReverse ? "foreach_reverse(" : "foreach(");
		cp.appendList(varParams, ",");
		cp.append(";");
		cp.append(iterable);
		cp.append(") ");
		cp.append(body);
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public ScopeTraverser getScopeTraverser() {
		return new ScopeTraverser(varParams, false);
	}
	
}