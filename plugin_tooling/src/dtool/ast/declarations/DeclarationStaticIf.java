/*******************************************************************************
 * Copyright (c) 2013 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.declarations;

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import dtool.ast.declarations.DeclarationAttrib.AttribBodySyntax;
import dtool.ast.expressions.Expression;
import dtool.ast.statements.IStatement;

public class DeclarationStaticIf extends AbstractConditionalDeclaration {
	
	public final Expression exp;
	
	public DeclarationStaticIf(Expression exp, AttribBodySyntax bodySyntax, ASTNode thenBody, 
		ASTNode elseBody) {
		super(bodySyntax, thenBody, elseBody);
		this.exp = parentize(exp);
	}
	
	public DeclarationStaticIf(Expression exp, IStatement thenBody, IStatement elseBody) {
		super(thenBody, elseBody);
		this.exp = parentize(exp);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_STATIC_IF;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, exp);
		acceptVisitor(visitor, thenBody);
		acceptVisitor(visitor, elseBody);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return isStatement == false ?
			new DeclarationStaticIf(clone(exp), bodySyntax, clone(thenBody), clone(elseBody)) :
			new DeclarationStaticIf(clone(exp), clone((IStatement) thenBody), clone((IStatement) elseBody));
				
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("static if ");
		cp.append("(", exp, ")");
		toStringAsCodeBodyAndElseBody(cp);
	}
	
}