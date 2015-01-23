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

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ConcreteElementSemantics;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.expressions.Expression;
import dtool.ast.statements.IFunctionBody;
import dtool.parser.common.Token;

public class DefinitionConstructor extends AbstractFunctionDefinition implements IDeclaration, IConcreteNamedElement {
	
	public DefinitionConstructor(Token[] comments, DefSymbol defName, 
			NodeVector<ITemplateParameter> tplParams, NodeVector<IFunctionParameter> fnParams, 
			NodeVector<IFunctionAttribute> fnAttributes, Expression tplConstraint, IFunctionBody fnBody) {
		super(comments, defName, tplParams, fnParams, fnAttributes, tplConstraint, fnBody);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_CONSTRUCTOR;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		visitChildren_common(visitor);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new DefinitionConstructor(comments, clone(defName), clone(tplParams), clone(fnParams), 
			clone(fnAttributes), clone(tplConstraint), clone(fnBody));
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
	protected NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new ConcreteElementSemantics(this, pickedElement) {
			
			@Override
			public void resolveSearchInMembersScope(CommonScopeLookup search) {
				// Not applicable to constructor as it cannot be referred directly
			}
			
			@Override
			public INamedElement getTypeForValueContext_do() {
				return null;
			}
			
		};
	}
	
}