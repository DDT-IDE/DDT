/*******************************************************************************
 * Copyright (c) 2013 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.declarations;

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import dtool.ast.references.RefIdentifier;
import dtool.ast.statements.IStatement;

/**
 * @see http://dlang.org/declaration.html#AliasThisDeclaration
 * 
 * (Technically not allowed as statement, but parse so anyways.)
 */
public class DeclarationAliasThis extends ASTNode implements IDeclaration, IStatement {
	
	public final boolean isAssignSyntax;
	public final RefIdentifier targetMember;
	
	public DeclarationAliasThis(boolean isAssignSyntax, RefIdentifier targetMember) {
		this.isAssignSyntax = isAssignSyntax;
		this.targetMember = parentize(targetMember);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_ALIAS_THIS;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, targetMember);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new DeclarationAliasThis(isAssignSyntax, clone(targetMember));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("alias ");
		if(isAssignSyntax) {
			cp.append("this");
			cp.append(" = ", targetMember);
		} else {
			cp.append(targetMember);
			cp.append(" this");
		}
		cp.append(";");
	}
	
}
