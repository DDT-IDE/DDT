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

import java.util.Iterator;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeListView;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.utilbox.collections.ArrayView;
import melnorme.utilbox.core.CoreUtil;

public abstract class CommonStatementList extends Statement {
	
	public final ArrayView<IStatement> statements;
	
	public CommonStatementList(ArrayView<IStatement> statements) {
		this.statements = parentizeI(assertNotNull(statements));
	}
	
	/** This represents a missing block */
	public CommonStatementList() {
		this.statements = null;
	}
	
	public final ArrayView<ASTNode> statements_asNodes() {
		return CoreUtil.<ArrayView<ASTNode>>blindCast(statements);
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
	
	public Iterator<? extends ASTNode> getMembersIterator() {
		return NodeListView.getIteratorSafe(statements_asNodes());
	}
	
}