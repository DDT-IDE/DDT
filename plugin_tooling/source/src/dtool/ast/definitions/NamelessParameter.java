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
package dtool.ast.definitions;

import static dtool.ast.definitions.FunctionParameter.getStringRepresentation;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;


/** 
 * A nameless function parameter, such as in: <br>
 * <code> void func(int, int); </code>
 */
public class NamelessParameter extends ASTNode implements IFunctionParameter {
	
	public final FnParameterAttributes paramAttribs;
	public final Reference type;
	public final Expression defaultValue;
	public final boolean isVariadic;
	
	public NamelessParameter(FnParameterAttributes paramAttribs, Reference type, Expression defaultValue, 
		boolean isVariadic) {
		this.paramAttribs = paramAttribs; 
		this.type = parentize(assertNotNull(type));
		this.defaultValue = parentize(defaultValue);
		this.isVariadic = isVariadic;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.NAMELESS_PARAMETER;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, type);
		acceptVisitor(visitor, defaultValue);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new NamelessParameter(paramAttribs, clone(type), clone(defaultValue), isVariadic);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		paramAttribs.toStringAsCode(cp);
		cp.append(type);
		cp.append(" = ", defaultValue);
		cp.append(isVariadic, "...");
	}
	
	@Override
	public boolean isVariadic() {
		return isVariadic;
	}
	
	@Override
	public String toStringForFunctionSignature(boolean includeName) {
		return getStringRepresentation(type, null, isVariadic);
	}
	
	@Override
	public String getInitializerStringRepresentation() {
		if(defaultValue == null)
			return null;
		return defaultValue.toStringAsCode();
	}
	
}