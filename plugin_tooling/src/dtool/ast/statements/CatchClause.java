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
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.engine.scoping.ScopeTraverser;
import melnorme.utilbox.misc.IteratorUtil;

public class CatchClause extends ASTNode implements IScopeElement {
	
	public final SimpleVariableDef catchParam;
	public final IStatement body;
	
	public CatchClause(SimpleVariableDef catchParam, IStatement body) {
		this.catchParam = parentize(catchParam);
		this.body = parentize(body);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.TRY_CATCH_CLAUSE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, catchParam);
		acceptVisitor(visitor, body);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new CatchClause(clone(catchParam), clone(body));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("catch ");
		cp.append("(", catchParam, ") ");
		cp.append(body);
	}
	
	/* -----------------  ----------------- */
	
	public Iterable<SimpleVariableDef> getScopeNodeList() {
		return catchParam == null ? null : IteratorUtil.iterable(catchParam);
	}
	
	@Override
	public ScopeTraverser getScopeTraverser() {
		return new ScopeTraverser(getScopeNodeList(), false);
	}
	
}