/*******************************************************************************
 * Copyright (c) 2011 Bruno Medeiros and other Contributors.
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
import dtool.ast.definitions.Symbol;

public class FunctionBodyOutBlock extends ASTNode {
	
	public final Symbol result; // TODO convert this to DefUnit
	public final BlockStatement block;
	
	public FunctionBodyOutBlock(Symbol result, BlockStatement block) {
		this.result = parentize(result);
		this.block = parentize(block);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.FUNCTION_BODY_OUT_BLOCK;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, result);
		acceptVisitor(visitor, block);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new FunctionBodyOutBlock(clone(result), clone(block));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("out");
		cp.append("(", result, ")");
		cp.append(block);
	}
	
}