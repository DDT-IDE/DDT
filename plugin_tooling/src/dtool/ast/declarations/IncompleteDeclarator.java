/*******************************************************************************
 * Copyright (c) 2013, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import dtool.ast.references.Reference;

/**
 * Represents an incomplete var or function declaration (where the defId is missing).
 */
public class IncompleteDeclarator extends ASTNode implements IDeclaration {
	
	public final Reference ref;
	
	public IncompleteDeclarator(Reference ref) {
		this.ref = parentize(assertNotNull(ref));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.INCOMPLETE_DECLARATOR;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, ref);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new IncompleteDeclarator(clone(ref));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(ref);
		cp.append(";");
	}
	
}