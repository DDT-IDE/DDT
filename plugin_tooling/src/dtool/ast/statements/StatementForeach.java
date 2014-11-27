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

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.utilbox.collections.ArrayView;
import dtool.ast.expressions.Expression;

public class StatementForeach extends Statement implements IScopeElement {
	
	public final boolean isForeachReverse;
	public final ArrayView<ForeachVariableDef> varParams;
	public final Expression iterable;
	public final IStatement body;
	
	public StatementForeach(boolean isForeachReverse, ArrayView<ForeachVariableDef> varParams, Expression iterable,
			IStatement body) {
		this.varParams = parentizeI(varParams);
		this.iterable = parentize(iterable);
		this.body = parentizeI(body);
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
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(isForeachReverse ? "foreach_reverse(" : "foreach(");
		cp.appendList(varParams, ",");
		cp.append(";");
		cp.append(iterable);
		cp.append(") ");
		cp.append(body);
	}
	
	@Override
	public void resolveSearchInScope(CommonScopeLookup search) {
		search.evaluateNodeList(varParams, true);
	}
	
}