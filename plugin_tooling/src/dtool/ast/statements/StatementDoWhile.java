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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.MissingParenthesesExpression;

public class StatementDoWhile extends Statement {
	
	public final IStatement body;
	public final Expression condition;
	
	public StatementDoWhile(IStatement body, Expression condition) {
		this.body = parentizeI(assertNotNull(body));
		this.condition = parentize(condition);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_DO_WHILE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, body);
		acceptVisitor(visitor, condition);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("do ");
		cp.append(body, " ");
		cp.append(condition != null, "while");
		MissingParenthesesExpression.appendParenthesesExp(cp, condition);
		cp.append(condition != null, ";");
	}
	
}