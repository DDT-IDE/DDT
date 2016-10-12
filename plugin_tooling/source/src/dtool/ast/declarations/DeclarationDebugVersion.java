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
import dtool.ast.declarations.DeclarationAttrib.AttribBodySyntax;
import dtool.ast.statements.IStatement;

public class DeclarationDebugVersion extends AbstractConditionalDeclaration {
	
	public final boolean isDebug;
	public final VersionSymbol value;
	
	public DeclarationDebugVersion(boolean isDebug, VersionSymbol value, AttribBodySyntax bodySyntax, 
		ASTNode thenBody, ASTNode elseBody) {
		super(bodySyntax, thenBody, elseBody);
		this.isDebug = isDebug;
		this.value = parentize(value);
	}
	
	public DeclarationDebugVersion(boolean isDebug, VersionSymbol value, IStatement thenBody, IStatement elseBody) {
		super(thenBody, elseBody);
		this.isDebug = isDebug;
		this.value = parentize(value);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_DEBUG_VERSION;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, value);
		acceptVisitor(visitor, thenBody);
		acceptVisitor(visitor, elseBody);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return isStatement == false ?
			new DeclarationDebugVersion(isDebug, clone(value), bodySyntax, clone(thenBody), clone(elseBody)) :
			new DeclarationDebugVersion(isDebug, clone(value), clone((IStatement) thenBody), 
				clone((IStatement) elseBody));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(isDebug ? "debug " : "version ");
		cp.append("(", value, ")");
		toStringAsCodeBodyAndElseBody(cp);
	}
	
}