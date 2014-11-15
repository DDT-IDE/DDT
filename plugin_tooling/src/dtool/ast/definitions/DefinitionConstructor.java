/*******************************************************************************
 * Copyright (c) 2011, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.definitions;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.TypeSemantics;
import melnorme.utilbox.collections.ArrayView;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.expressions.Expression;
import dtool.ast.statements.IFunctionBody;
import dtool.parser.common.Token;
import dtool.resolver.CommonDefUnitSearch;

public class DefinitionConstructor extends AbstractFunctionDefinition implements IDeclaration {
	
	public DefinitionConstructor(Token[] comments, ProtoDefSymbol defId, 
		ArrayView<TemplateParameter> tplParams, ArrayView<IFunctionParameter> fnParams, 
		ArrayView<FunctionAttributes> fnAttributes, Expression tplConstraint, IFunctionBody fnBody) 
	{
		super(comments, defId, tplParams, fnParams, fnAttributes, tplConstraint, fnBody);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_CONSTRUCTOR;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, tplParams);
		acceptVisitor(visitor, fnParams);
		acceptVisitor(visitor, tplConstraint);
		acceptVisitor(visitor, fnBody);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		toStringAsCode_fromDefId(cp);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Constructor;
	}
	
	@Override
	public String getNameInRegularNamespace() {
		return null;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public INamedElementSemantics getNodeSemantics() {
		return semantics;
	}
	
	protected final TypeSemantics semantics = new TypeSemantics(this) {
		
		@Override
		public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
			// Not applicable to constructor as it cannot be referred directly
		}
		
	};
	
}