/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.ast_actual;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.ast.definitions.Module;
import dtool.ast.util.NodeUtil;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.ReferenceResolver;
import melnorme.lang.tooling.ast.CommonASTNode;

public abstract class ASTNode extends CommonASTNode {
	
	/* ------------------------  Node type  ------------------------  */
	
	public abstract ASTNodeTypes getNodeType();
	
	/* ------------------------------------------------------------ */
	
	public String getModuleFullyQualifiedName() {
		/* This must be overriden by synthetic defUnits */
		Module moduleNode = assertNotNull(getModuleNode());
		return moduleNode.getFullyQualifiedName();
	}
	
	public Module getModuleNode() {
		return NodeUtil.getParentModule(asNode());
	}
	
	/* -----------------  ----------------- */
	
	/** Run a reference search using the lookup rules of this node.
	 * Default is run the search on the full lexical scope */
	public void performRefSearch(CommonDefUnitSearch search) {
		ReferenceResolver.resolveSearchInFullLexicalScope(asNode(), search);
	}
	
}