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

import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTNode;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.DeclBlock;
import dtool.ast.declarations.DeclarationEmpty;
import dtool.ast.expressions.Expression;
import dtool.ast.statements.IStatement;
import dtool.engine.common.DefElementCommon;
import dtool.engine.modules.IModuleResolver;
import dtool.parser.Token;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IScopeNode;
import dtool.resolver.ReferenceResolver;
import dtool.util.ArrayView;

/**
 * A definition of a aggregate. 
 */
public abstract class DefinitionAggregate extends CommonDefinition 
	implements IStatement, IScopeNode, ITemplatableElement 
{
	
	public interface IAggregateBody extends IASTNode {
	}
	
	public final ArrayView<TemplateParameter> tplParams;
	public final Expression tplConstraint;
	public final IAggregateBody aggrBody;
	
	public DefinitionAggregate(Token[] comments, ProtoDefSymbol defId, ArrayView<TemplateParameter> tplParams,
		Expression tplConstraint, IAggregateBody aggrBody) {
		super(comments, defId);
		this.tplParams = parentize(tplParams);
		this.tplConstraint = parentize(tplConstraint);
		this.aggrBody = parentizeI(aggrBody);
	}
	
	protected void acceptNodeChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, tplParams);
		acceptVisitor(visitor, tplConstraint);
		acceptVisitor(visitor, aggrBody);
	}
	
	public void aggregateToStringAsCode(ASTCodePrinter cp, String keyword, boolean printDecls) {
		cp.append(keyword);
		cp.append(defname, " ");
		cp.appendList("(", tplParams, ",", ") ");
		DefinitionTemplate.tplConstraintToStringAsCode(cp, tplConstraint);
		if(printDecls) {
			cp.append(aggrBody, "\n");
		}
	}
	
	@Override
	public boolean isTemplated() {
		return tplParams != null;
	}
	
	@Override
	public void resolveSearchInScope(CommonDefUnitSearch search) {
		ReferenceResolver.findInNodeList(search, tplParams, true);
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		ReferenceResolver.resolveSearchInScope(search, getBodyScope());
	}
	
	@Override
	public INamedElement resolveTypeForValueContext(IModuleResolver mr) {
		return DefElementCommon.returnError_ElementIsNotAValue(this);
	}
	
	public IScopeNode getBodyScope() {
		return aggrBody instanceof DeclarationEmpty ? null : ((DeclBlock) aggrBody);
	}
	
}