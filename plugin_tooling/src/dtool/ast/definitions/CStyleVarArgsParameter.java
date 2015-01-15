/*******************************************************************************
 * Copyright (c) 2013, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.definitions;

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;


/** 
 * C-style var args paramater, as in: <br>
 * <code> ... </code>
 */
public class CStyleVarArgsParameter extends ASTNode implements IFunctionParameter {
	
	public CStyleVarArgsParameter() {
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.VAR_ARGS_PARAMETER;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new CStyleVarArgsParameter();
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("...");
	}
	
	@Override
	public boolean isVariadic() {
		return true;
	}
	
	@Override
	public String getTypeStringRepresentation() {
		return toStringAsCode();
	}
	
	@Override
	public String getInitializerStringRepresentation() {
		return null;
	}
	
	@Override
	public String toStringForFunctionSignature() {
		return toStringAsCode();
	}

}