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
package dtool.ast.references;

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ReferenceSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.expressions.Expression;

public class RefTypeof extends Reference implements IQualifierNode {
	
	public final Expression exp;
	
	public RefTypeof(Expression exp) {
		this.exp = parentize(exp);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_TYPEOF;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, exp);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new RefTypeof(clone(exp));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("typeof");
		cp.append("(", exp, ")");
	}
	
	public static class ExpRefReturn extends Expression {
		
		public ExpRefReturn() {}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.EXP_REF_RETURN;
		}
		
		@Override
		public void visitChildren(IASTVisitor visitor) {
		}
		
		@Override
		protected CommonASTNode doCloneTree() {
			return new ExpRefReturn();
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append("return");
		}
		
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected ReferenceSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new ReferenceSemantics(this, pickedElement) {
		
			@Override
			public INamedElement doResolveTargetElement() {
				return exp.resolveTypeOfUnderlyingValue_nonNull(context).originalType;
			}
			
		};
	}
	
}