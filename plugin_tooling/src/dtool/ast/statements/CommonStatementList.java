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
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.utilbox.collections.ArrayView;

public abstract class CommonStatementList extends Statement {
	
	public final NodeVector<IStatement> statements;
	
	public CommonStatementList(NodeVector<IStatement> statements) {
		this.statements = parentize(assertNotNull(statements));
	}
	
	/** This represents a missing block */
	public CommonStatementList() {
		this.statements = null;
	}
	
	public final ArrayView<ASTNode> statements_asNodes() {
		return NodeVector.<ASTNode>castTypeParameter(statements);
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, statements);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		if(statements == null) {
			cp.append(" ");
			return;
		}
		cp.append("{");
		cp.appendList("\n", statements_asNodes(), "\n", "\n");
		cp.append("}");
	}
	
}