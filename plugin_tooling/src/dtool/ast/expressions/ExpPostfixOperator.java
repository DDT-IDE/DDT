/*******************************************************************************
 * Copyright (c) 2012 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import dtool.parser.DeeTokens;

public class ExpPostfixOperator extends Expression {
	
	public static enum PostfixOpType {
		POST_INCREMENT(DeeTokens.INCREMENT),
		POST_DECREMENT(DeeTokens.DECREMENT),
		;
		
		public final DeeTokens token;
		
		private PostfixOpType(DeeTokens token) {
			this.token = token;
			assertTrue(token.getSourceValue() != null);
		}
		
		public static PostfixOpType tokenToPrefixOpType(DeeTokens token) {
			assertTrue(token == DeeTokens.INCREMENT || token == DeeTokens.DECREMENT);
			return token == DeeTokens.INCREMENT ? POST_INCREMENT : POST_DECREMENT;
		}
	}
	
	public final PostfixOpType kind;
	public final Resolvable exp;
	
	public ExpPostfixOperator(Resolvable exp, PostfixOpType kind) {
		this.exp = parentize(exp);
		this.kind = kind;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_POSTFIX_OP;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, exp);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new ExpPostfixOperator(clone(exp), kind);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(exp);
		cp.append(kind.token.getSourceValue());
	}
	
}