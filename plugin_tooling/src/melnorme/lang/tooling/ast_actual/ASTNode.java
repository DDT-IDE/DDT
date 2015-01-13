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
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.engine.scoping.NamedElementsScope;
import dtool.ast.definitions.Module;
import dtool.engine.analysis.DeeLanguageIntrinsics;

public abstract class ASTNode extends CommonASTNode {
	
	/* ------------------------  Node type  ------------------------  */
	
	public abstract ASTNodeTypes getNodeType();
	
	/* ------------------------------------------------------------ */
	
	public String getModuleFullName() {
		/* This must be overriden by synthetic defUnits */
		Module moduleNode = assertNotNull(getModuleNode_());
		return moduleNode.getFullyQualifiedName();
	}
	
	public final Module getModuleNode_() {
		return (Module) getModuleNode();
	}
	
	public static NamedElementsScope getPrimitivesScope() {
		return DeeLanguageIntrinsics.D2_063_intrinsics.primitivesScope;
	}
	
}