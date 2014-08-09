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

public class StatementCaseRange extends Statement {
	
	public final Expression expFirst;
	public final Expression expLast;
	public final IStatement body;
	
	public StatementCaseRange(Expression expFirst, Expression expLast, IStatement body) {
		this.expFirst = parentize(assertNotNull(expFirst));
		this.expLast = parentize(expLast);
		this.body = parentizeI(body);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_CASE_RANGE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, expFirst);
		acceptVisitor(visitor, expLast);
		acceptVisitor(visitor, body);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("case ", expFirst, " : .. ");
		cp.append("case ", expLast, " : ");
		cp.append(body);
	}
	
}