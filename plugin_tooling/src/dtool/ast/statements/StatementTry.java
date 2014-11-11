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


import melnorme.utilbox.collections.ArrayView;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;

public class StatementTry extends Statement {
	
	public final IStatement body;
	public final ArrayView<CatchClause> catches;
	public final IStatement finallyBody;
	
	public StatementTry(IStatement body, ArrayView<CatchClause> catches, IStatement finallyBody) {
		this.body = parentizeI(body);
		this.catches = parentize(catches);
		this.finallyBody = parentizeI(finallyBody);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_TRY;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, body);
		acceptVisitor(visitor, catches);
		acceptVisitor(visitor, finallyBody);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("try ");
		cp.append(body);
		cp.appendList(catches, "\n");
		cp.append("finally ", finallyBody);
	}
	
}