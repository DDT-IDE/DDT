/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.Attribute;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.expressions.Expression;
import dtool.ast.statements.IFunctionBody;
import dtool.parser.Token;
import dtool.util.ArrayView;

public class DefinitionConstructor extends AbstractFunctionDefinition implements IDeclaration {
	
	public DefinitionConstructor(Token[] comments, ArrayView<Attribute> attributes, ProtoDefSymbol defId, 
		ArrayView<TemplateParameter> tplParams, ArrayView<IFunctionParameter> fnParams, 
		ArrayView<FunctionAttributes> fnAttributes, Expression tplConstraint, IFunctionBody fnBody) 
	{
		super(comments, attributes, defId, tplParams, fnParams, fnAttributes, tplConstraint, fnBody);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_CONSTRUCTOR;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, attributes);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, tplParams);
			TreeVisitor.acceptChildren(visitor, fnParams);
			TreeVisitor.acceptChildren(visitor, tplConstraint);
			TreeVisitor.acceptChildren(visitor, fnBody);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendList(attributes, " ", true);
		toStringAsCode_fromDefId(cp);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Constructor;
	}
	
}