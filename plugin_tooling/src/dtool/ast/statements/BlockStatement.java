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

import dtool.ast.ASTNodeTypes;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IScopeNode;
import dtool.resolver.ReferenceResolver;
import dtool.util.ArrayView;

/**
 * A compound statement. Allways introduces a new Scope.
 */
public class BlockStatement extends CommonStatementList implements IScopeNode, IFunctionBody {
	
	public BlockStatement(ArrayView<IStatement> statements) {
		super(statements);
	}
	
	public BlockStatement() {
		super();
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.BLOCK_STATEMENT;
	}
	
	@Override
	public void resolveSearchInScope(CommonDefUnitSearch search) {
		ReferenceResolver.findInNodeList(search, statements_asNodes(), true);
	}
	
}