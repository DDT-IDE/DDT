/*******************************************************************************
 * Copyright (c) 2011 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.definitions;

import static dtool.engine.analysis.DeeLanguageIntrinsics.D2_063_intrinsics;
import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.TypeSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.engine.scoping.NamedElementsScope;
import melnorme.lang.tooling.engine.scoping.ScopeTraverser;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.ITypeNamedElement;
import melnorme.utilbox.collections.ArrayView;
import dtool.ast.declarations.DeclBlock;
import dtool.ast.expressions.Expression;
import dtool.ast.statements.IStatement;
import dtool.parser.common.Token;

/**
 * A definition of a aggregate. 
 */
public abstract class DefinitionAggregate extends CommonDefinition 
	implements IStatement, IScopeElement, ITemplatableElement, IConcreteNamedElement, ITypeNamedElement
{
	
	public interface IAggregateBody extends IASTNode {
	}
	
	public final NodeVector<ITemplateParameter> tplParams;
	public final Expression tplConstraint;
	public final IAggregateBody aggrBody;
	
	public DefinitionAggregate(Token[] comments, DefSymbol defId, NodeVector<ITemplateParameter> tplParams,
		Expression tplConstraint, IAggregateBody aggrBody) {
		super(comments, defId);
		this.tplParams = parentize(tplParams);
		this.tplConstraint = parentize(tplConstraint);
		this.aggrBody = parentize(aggrBody);
	}
	
	protected void acceptNodeChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defName);
		acceptVisitor(visitor, tplParams);
		acceptVisitor(visitor, tplConstraint);
		acceptVisitor(visitor, aggrBody);
	}
	
	public void aggregateToStringAsCode(ASTCodePrinter cp, String keyword, boolean printDecls) {
		cp.append(keyword);
		cp.append(defName, " ");
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
	public NodeVector<ITemplateParameter> getTemplateParameters() {
		return tplParams;
	}
	
	public ArrayView<ASTNode> getAggregateMembers() {
		return (aggrBody instanceof DeclBlock) ? 
				((DeclBlock) aggrBody).nodes :
				ArrayView.<ASTNode>createFrom();
	}
	
	/* ----------------- ----------------- */
	
	@Override
	public AggregateSemantics getSemantics(ISemanticContext parentContext) {
		return (AggregateSemantics) super.getSemantics(parentContext);
	}
	@Override
	protected AggregateSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new AggregateSemantics(this, pickedElement,
			new MembersScopeElement(getAggregateMembers()),
			new NamedElementsScope(D2_063_intrinsics.createCommonProperties(this)));
	}
	
	public class AggregateSemantics extends TypeSemantics {
		
		protected final NamedElementsScope commonTypeScope;
		
		public AggregateSemantics(DefinitionAggregate typeElement, PickedElement<?> pickedElement, 
				IScopeElement membersScope, NamedElementsScope commonTypeScope) {
			super(typeElement, pickedElement, membersScope);
			this.commonTypeScope = commonTypeScope;
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonScopeLookup search) {
			search.evaluateScope(getMembersScope());
			search.evaluateScope(commonTypeScope);
		}
		
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public ScopeTraverser getScopeTraverser() {
		return new ScopeTraverser(tplParams, false);
	}
	
}