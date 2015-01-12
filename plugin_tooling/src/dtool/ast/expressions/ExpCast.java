/*******************************************************************************
 * Copyright (c) 2012, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.expressions;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ExpSemantics;
import melnorme.lang.tooling.engine.resolver.TypeReferenceResult;
import dtool.ast.references.Reference;

public class ExpCast extends Expression {
	
	public final Reference castTypeRef;
	public final Resolvable exp;
	
	public ExpCast(Reference castType, Expression exp) {
		this.castTypeRef = parentize(castType);
		this.exp = parentize(exp);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_CAST;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, castTypeRef);
		acceptVisitor(visitor, exp);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("cast");
		cp.append("(", castTypeRef, ")");
		cp.append(exp);
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected ExpSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new ExpSemantics(this, pickedElement) {
			
			@Override
			public TypeReferenceResult doCreateExpResolution() {
				return resolveTypeReference(castTypeRef);
			}
			
		};
	}
	
}