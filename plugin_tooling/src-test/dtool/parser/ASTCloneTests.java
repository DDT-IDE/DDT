/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast_actual.ASTNode;

public class ASTCloneTests {

	public static void testCloning(ASTNode node) {
		CommonASTNode clonedNode = node.cloneTree();
		assertNotNull(clonedNode);
		assertTrue(clonedNode != node);
		assertTrue(clonedNode.getParent() == null);
		assertTrue(clonedNode.getClass() ==  node.getClass());
		
		assertEquals(clonedNode.toStringAsCode(), node.toStringAsCode());
		assertEquals(clonedNode.isParsedStatus(), false);
	}
	
}