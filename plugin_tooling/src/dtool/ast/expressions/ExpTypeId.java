/*******************************************************************************
 * Copyright (c) 2012, 2014 Bruno Medeiros and other Contributors.
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
import dtool.ast.references.Reference;

public class ExpTypeId extends Expression {
	
	public final Reference typeArgument;
	public final Expression expressionArgument;
	
	public ExpTypeId(Reference typeArgument) {
		this.typeArgument = parentize(typeArgument);
		this.expressionArgument = null;
	}
	
	public ExpTypeId(Expression expressionArgument) {
		this.typeArgument = null;
		this.expressionArgument = parentize(expressionArgument);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_TYPEID;
	}
	
	public Resolvable getArgument() {
		return typeArgument != null ? typeArgument : expressionArgument;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, getArgument());
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return typeArgument != null ? new ExpTypeId(clone(typeArgument)) : new ExpTypeId(clone(expressionArgument));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("typeid");
		cp.append("(", getArgument(), ")");
	}
	
}