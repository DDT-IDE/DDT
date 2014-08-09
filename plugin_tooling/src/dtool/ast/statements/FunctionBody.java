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
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;

public class FunctionBody extends ASTNode implements IFunctionBody {
	
	public final BlockStatement bodyBlock;
	
	public FunctionBody(BlockStatement bodyBlock) {
		this.bodyBlock = parentize(assertNotNull(bodyBlock));
	}
	
	protected FunctionBody(BlockStatement bodyBlock, @SuppressWarnings("unused") boolean dummy) {
		this.bodyBlock = parentize(bodyBlock);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.FUNCTION_BODY;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, bodyBlock);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("body", bodyBlock);
	}
	
}