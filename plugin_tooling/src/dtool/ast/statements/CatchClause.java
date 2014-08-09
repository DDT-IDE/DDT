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

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IScopeNode;
import dtool.resolver.ReferenceResolver;

public class CatchClause extends ASTNode implements IScopeNode {
	
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
	public void resolveSearchInScope(CommonDefUnitSearch search) {
		if(catchParam != null) {
			ReferenceResolver.findInNodeList(search, Collections.singletonList(catchParam), false);
		}
	}
	
}