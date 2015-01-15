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
package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import dtool.ast.definitions.IFunctionAttribute;
import dtool.ast.statements.IFunctionBody;

public class DeclarationSpecialFunction extends ASTNode implements IDeclaration {
	
	public static enum SpecialFunctionKind {
		POST_BLIT("this(this)"),
		
		DESTRUCTOR("~this()"),
		;
		public final String sourceValue;
		
		private SpecialFunctionKind(String sourceValue) {
			this.sourceValue = sourceValue;
		}
		
		public String toStringAsCode() {
			return sourceValue;
		}
	}
	
	public final SpecialFunctionKind kind;
	public final NodeVector<IFunctionAttribute> fnAttributes;
	public final IFunctionBody fnBody;
	
	public DeclarationSpecialFunction(SpecialFunctionKind kind, NodeVector<IFunctionAttribute> fnAttributes, 
		IFunctionBody fnBody) {
		this.kind = assertNotNull(kind);
		this.fnAttributes = parentize(fnAttributes);
		this.fnBody = parentize(fnBody);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_SPECIAL_FUNCTION;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, fnAttributes);
		acceptVisitor(visitor, fnBody);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new DeclarationSpecialFunction(kind, clone(fnAttributes), clone(fnBody));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(kind.toStringAsCode());
		cp.appendList(fnAttributes, " ", true);
		cp.append(fnBody);
	}
	
}