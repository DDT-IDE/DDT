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

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import dtool.ast.definitions.Symbol;

public class ExpTraits extends Expression {
	
	public final Symbol traitsId;
	public final NodeVector<Resolvable> args;
	
	public ExpTraits(Symbol traitsId, NodeVector<Resolvable> args) {
		this.traitsId = parentize(traitsId);
		this.args = parentize(args);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_TRAITS;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, traitsId);
		acceptVisitor(visitor, args);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new ExpTraits(clone(traitsId), clone(args));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("__traits");
		if(traitsId != null) {
			cp.append("(", traitsId);
			cp.appendNodeList(", ", args, ",", "");
			cp.append(")");
		}
	}
	
}