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


import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.engine.scoping.ScopeTraverser;
import melnorme.utilbox.collections.ArrayView;
import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.expressions.Expression;
import dtool.ast.statements.IFunctionBody;
import dtool.parser.common.Token;

public abstract class AbstractFunctionDefinition extends CommonDefinition 
	implements ICallableElement, IScopeElement, ITemplatableElement
{
	
	public final NodeVector<ITemplateParameter> tplParams;
	public final NodeVector<IFunctionParameter> fnParams;
	public final NodeVector<IFunctionAttribute> fnAttributes;
	public final Expression tplConstraint;
	public final IFunctionBody fnBody;
	
	public AbstractFunctionDefinition(Token[] comments, DefSymbol defId, NodeVector<ITemplateParameter> tplParams,
			NodeVector<IFunctionParameter> fnParams, NodeVector<IFunctionAttribute> fnAttributes, 
			Expression tplConstraint,
		IFunctionBody fnBody) {
		super(comments, defId);
		
		this.tplParams = parentize(tplParams);
		this.fnParams = parentize(fnParams);
		this.fnAttributes = parentize(fnAttributes);
		this.tplConstraint = parentize(tplConstraint);
		this.fnBody = parentize(fnBody);
	}
	
	public final ArrayView<ASTNode> getParams_asNodes() {
		return CoreUtil.blindCast(fnParams);
	}
	
	public static ArrayView<IFunctionParameter> NO_PARAMS = new ArrayView<>(new IFunctionParameter[0]);
	
	protected void visitChildren_common(IASTVisitor visitor) {
		acceptVisitor(visitor, defName);
		acceptVisitor(visitor, tplParams);
		acceptVisitor(visitor, fnParams);
		acceptVisitor(visitor, fnAttributes);
		acceptVisitor(visitor, tplConstraint);
		acceptVisitor(visitor, fnBody);
	}
	
	@Override
	public ArrayView<IFunctionParameter> getParameters() {
		return fnParams == null ? NO_PARAMS : fnParams;
	}
	
	public void toStringAsCode_fromDefId(ASTCodePrinter cp) {
		cp.append(defName);
		cp.appendList("(", tplParams, ",", ") ");
		cp.appendList("(", getParams_asNodes(), ",", ") ");
		cp.appendList(fnAttributes, " ", true);
		DefinitionTemplate.tplConstraintToStringAsCode(cp, tplConstraint);
		cp.appendNodeOrNullAlt(fnBody, " ");
	}
	
	@Override
	public boolean isTemplated() {
		return tplParams != null;
	}
	
	@Override
	public NodeVector<ITemplateParameter> getTemplateParameters() {
		return tplParams;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public ScopeTraverser getScopeTraverser() {
		// TODO: technically not correct, these are two separte scopes
		Iterable<? extends IASTNode> iterable = IteratorUtil.<IASTNode>chainedIterable(tplParams, fnParams);
		return new ScopeTraverser(iterable, false);
	}
	
}