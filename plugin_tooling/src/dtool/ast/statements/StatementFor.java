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

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;

public class StatementFor extends Statement {
	
	public final IStatement init;
	public final Expression condition;
	public final Expression increment;
	public final IStatement body;
	
	public StatementFor(IStatement init, Expression condition, Expression increment, IStatement body) {
		this.init = parentizeI(init);
		this.condition = parentize(condition);
		this.increment = parentize(increment);
		this.body = parentizeI(body);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_FOR;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, init);
		acceptVisitor(visitor, condition);
		acceptVisitor(visitor, increment);
		acceptVisitor(visitor, body);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("for(");
		cp.append(init);
		cp.append(condition);
		cp.append(";");
		cp.append(increment);
		cp.append(") ");
		cp.append(body);
	}
	
}