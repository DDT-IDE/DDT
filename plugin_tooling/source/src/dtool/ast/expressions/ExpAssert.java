/*******************************************************************************
 * Copyright (c) 2012 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.expressions;

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;

public class ExpAssert extends Expression {
	
	public final Expression exp;
	public final Expression msg;
	
	public ExpAssert(Expression exp, Expression msg) {
		this.exp = parentize(exp);
		this.msg = parentize(msg);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_MIXIN_STRING;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, exp);
		acceptVisitor(visitor, msg);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new ExpAssert(clone(exp), clone(msg));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("assert");
		if(exp != null) {
			cp.append("(", exp);
			cp.append(",", msg);
			cp.append(")");
		}
	}
}