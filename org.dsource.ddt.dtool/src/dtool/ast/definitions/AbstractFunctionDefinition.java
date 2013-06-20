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


import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.core.CoreUtil;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.expressions.Expression;
import dtool.ast.statements.IFunctionBody;
import dtool.parser.Token;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
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
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		// FIXME
		return this;
	}
	
	@Override
	public Iterator<IFunctionParameter> getMembersIterator(IModuleResolver moduleResolver) {
		/*BUG here*/
		return fnParams.iterator();
	}
	
	@Override
	public List<IScope> getSuperScopes(IModuleResolver moduleResolver) {
		// TODO: function super
		return null;
	}
	
	@Override
	public boolean hasSequentialLookup() {
		return false;
	}
	
	/* ------------------------------------------------------------------------ */
	
}