/*******************************************************************************
 * Copyright (c) 2010, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ExpSemantics;
import melnorme.lang.tooling.engine.resolver.TypeReferenceResult;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.references.Reference;

public class ExpParentheses extends Expression {
	
	public final boolean isDotAfterParensSyntax;
	public final Resolvable resolvable;
	
	public ExpParentheses(boolean isDotAfterParensSyntax, Resolvable resolvable) {
		this.isDotAfterParensSyntax = isDotAfterParensSyntax;
		this.resolvable = parentize(assertNotNull(resolvable));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_PARENTHESES;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, resolvable);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new ExpParentheses(isDotAfterParensSyntax, clone(resolvable));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("(", resolvable, ")");
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected ExpSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new ExpSemantics(this, pickedElement) {
			
			@Override
			public TypeReferenceResult doCreateExpResolution() {
				Resolvable parensExp = ExpParentheses.this.resolvable;
				if(parensExp instanceof Reference) {
					Reference refRoot = (Reference) parensExp;
					return resolveTypeOfExpressionReference(refRoot);
				} else {
					Expression expRoot = (Expression) parensExp;
					return expRoot.resolveTypeOfUnderlyingValue(context);
				}
			}
			
		};
	}
	
	@Override
	public INamedElement resolveAsQualifiedRefRoot(ISemanticContext context) {
		if(resolvable instanceof Reference) {
			Reference reference = (Reference) resolvable;
			return reference.resolveTargetElement(context);
		} else {
			return super.resolveAsQualifiedRefRoot(context);
		}
	}
	
}