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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;

public class ExpConditional extends Expression {
	
	public final Resolvable condExp;
	public final Resolvable thenExp;
	public final Resolvable elseExp;
	
	public ExpConditional(Resolvable condExp, Resolvable thenExp, Resolvable elseExp) {
		this.condExp = parentize(assertNotNull(condExp));
		this.thenExp = parentize(thenExp);
		this.elseExp = parentize(elseExp); 
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_CONDITIONAL;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, condExp);
		acceptVisitor(visitor, thenExp);
		acceptVisitor(visitor, elseExp);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new ExpConditional(clone(condExp), clone(thenExp), clone(elseExp));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(condExp, "?");
		cp.append(thenExp);
		cp.append(":");
		cp.append(elseExp);
	}
	
}