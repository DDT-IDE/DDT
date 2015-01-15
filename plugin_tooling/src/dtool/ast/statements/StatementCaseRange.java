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
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import dtool.ast.expressions.Expression;

public class StatementCaseRange extends Statement {
	
	public final Expression expFirst;
	public final Expression expLast;
	public final IStatement body;
	
	public StatementCaseRange(Expression expFirst, Expression expLast, IStatement body) {
		this.expFirst = parentize(assertNotNull(expFirst));
		this.expLast = parentize(expLast);
		this.body = parentize(body);
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
	protected CommonASTNode doCloneTree() {
		return new StatementCaseRange(clone(expFirst), clone(expLast), clone(body));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("case ", expFirst, " : .. ");
		cp.append("case ", expLast, " : ");
		cp.append(body);
	}
	
}