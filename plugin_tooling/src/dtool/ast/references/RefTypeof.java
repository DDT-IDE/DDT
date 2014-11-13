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

import java.util.Collection;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable.IQualifierNode;
import dtool.engine.common.IDeeNamedElement;
import dtool.engine.modules.IModuleResolver;

public class RefTypeof extends Reference implements IQualifierNode {
	
	public final Expression expression;
	
	public RefTypeof(Expression exp) {
		this.expression = parentize(exp);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_TYPEOF;
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
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append("return");
		}
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, expression);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("typeof");
		cp.append("(", expression, ")");
	}
	
	@Override
	public Collection<IDeeNamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return expression.resolveTypeOfUnderlyingValue(moduleResolver);
	}
	
	@Override
	public Collection<IDeeNamedElement> resolveTypeOfUnderlyingValue(IModuleResolver mr) {
		return super.resolveToInvalidValue();
	}
	
}