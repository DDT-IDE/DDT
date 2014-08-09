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

public class ForeachRangeExpression extends Expression {
	
	public final Expression lower;
	public final Expression upper;
	
	public ForeachRangeExpression(Expression lower, Expression upper) {
		this.lower = parentize(lower);
		this.upper = parentize(upper);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.FOREACH_RANGE_EXPRESSION;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, lower);
		acceptVisitor(visitor, upper);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(lower);
		cp.append(" .. ");
		cp.append(upper);
	}
	
}