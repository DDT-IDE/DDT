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

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.parser.common.IToken;
import dtool.util.ArrayView;

public class StatementAsm extends Statement {
	
	public final ArrayView<IToken> tokens;
	
	public StatementAsm(ArrayView<IToken> tokens) {
		this.tokens = tokens;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_ASM;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("asm ");
		if(tokens != null) {
			cp.append("{");
			cp.appendTokenList(tokens, " ", true);
			cp.append("}");
		}
	}
	
}