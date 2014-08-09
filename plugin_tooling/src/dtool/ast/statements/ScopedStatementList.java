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
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IScopeNode;
import dtool.resolver.ReferenceResolver;
import dtool.util.ArrayView;

/**
 * A scoped statement list. Used by case/default statements
 */
public class ScopedStatementList extends CommonStatementList implements IScopeNode {
	
	public ScopedStatementList(ArrayView<IStatement> statements) {
		super(assertNotNull(statements));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.SCOPED_STATEMENT_LIST;
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendList("\n", statements_asNodes(), "\n", "\n");
	}
	
	@Override
	public void resolveSearchInScope(CommonDefUnitSearch search) {
		ReferenceResolver.findInNodeList(search, statements, true);
	}
	
}