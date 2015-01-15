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

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import dtool.ast.definitions.Symbol;

public class StatementContinue extends Statement {
	
	public final Symbol id;
	
	public StatementContinue(Symbol id) {
		this.id = parentize(id);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_CONTINUE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, id);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new StatementContinue(clone(id));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("continue ");
		cp.append(id);
		cp.append(";");
	}
	
}