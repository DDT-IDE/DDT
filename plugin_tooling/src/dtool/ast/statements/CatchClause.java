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

import java.util.Collections;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;

public class CatchClause extends ASTNode implements IScopeElement {
	
	public final SimpleVariableDef catchParam;
	public final IStatement body;
	
	public CatchClause(SimpleVariableDef catchParam, IStatement body) {
		this.catchParam = parentizeI(catchParam);
		this.body = parentizeI(body);
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
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("catch ");
		cp.append("(", catchParam, ") ");
		cp.append(body);
	}
	
	@Override
	public void resolveSearchInScope(CommonScopeLookup search) {
		if(catchParam != null) {
			search.evaluateNodeList(Collections.singletonList(catchParam), false);
		}
	}
	
}