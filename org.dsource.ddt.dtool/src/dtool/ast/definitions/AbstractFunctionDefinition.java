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


import melnorme.utilbox.core.CoreUtil;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.expressions.Expression;
import dtool.ast.statements.IFunctionBody;
import dtool.parser.Token;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IScopeNode;
import dtool.resolver.ReferenceResolver;
import dtool.util.ArrayView;

public abstract class AbstractFunctionDefinition extends CommonDefinition 
	implements ICallableElement, IScopeNode
{
	
	public final ArrayView<TemplateParameter> tplParams;
	public final ArrayView<IFunctionParameter> fnParams;
	public final ArrayView<FunctionAttributes> fnAttributes;
	public final Expression tplConstraint;
	public final IFunctionBody fnBody;
	
	public AbstractFunctionDefinition(Token[] comments, ProtoDefSymbol defId, ArrayView<TemplateParameter> tplParams,
		ArrayView<IFunctionParameter> fnParams, ArrayView<FunctionAttributes> fnAttributes, Expression tplConstraint,
		IFunctionBody fnBody) {
		super(comments, defId);
		
		this.tplParams = parentize(tplParams);
		this.fnParams = parentizeI(fnParams);
		this.fnAttributes = fnAttributes;
		this.tplConstraint = parentize(tplConstraint);
		this.fnBody = parentizeI(fnBody);
	}
	
	public final ArrayView<ASTNode> getParams_asNodes() {
		return CoreUtil.blindCast(fnParams);
	}
	
	public static ArrayView<IFunctionParameter> NO_PARAMS = new ArrayView<>(new IFunctionParameter[0]);
	
	@Override
	public ArrayView<IFunctionParameter> getParameters() {
		return fnParams == null ? NO_PARAMS : fnParams;
	}
	
	public void toStringAsCode_fromDefId(ASTCodePrinter cp) {
		cp.append(defname);
		cp.appendList("(", tplParams, ",", ") ");
		cp.appendList("(", getParams_asNodes(), ",", ") ");
		cp.appendTokenList(fnAttributes, " ", true);
		DefinitionTemplate.tplConstraintToStringAsCode(cp, tplConstraint);
		cp.appendNodeOrNullAlt(fnBody, " ");
	}
	
	@Override
	public void resolveSearchInScope(CommonDefUnitSearch search) {
		ReferenceResolver.findInNodeList(search, tplParams, true);
		ReferenceResolver.findInNodeList(search, fnParams, true);
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		// Do nothing, a function has no members scope
	}
	
	/* ------------------------------------------------------------------------ */
	
}