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

import static dtool.engine.analysis.DeeLanguageIntrinsics.D2_063_intrinsics;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.intrinsics.InstrinsicsScope;
import melnorme.lang.tooling.engine.resolver.TypeSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.utilbox.collections.ArrayView;
import dtool.ast.declarations.DeclBlock;
import dtool.ast.expressions.Expression;
import dtool.ast.statements.IStatement;
import dtool.parser.common.Token;

/**
 * A definition of a aggregate. 
 */
public abstract class DefinitionAggregate extends CommonDefinition 
	implements IStatement, IScopeElement, ITemplatableElement, IConcreteNamedElement
{
	
	public interface IAggregateBody extends IASTNode {
	}
	
	public final ArrayView<ITemplateParameter> tplParams;
	public final Expression tplConstraint;
	public final IAggregateBody aggrBody;
	
	public final IScopeElement membersScope;
	
	public DefinitionAggregate(Token[] comments, ProtoDefSymbol defId, ArrayView<ITemplateParameter> tplParams,
		Expression tplConstraint, IAggregateBody aggrBody) {
		super(comments, defId);
		this.tplParams = parentize(tplParams);
		this.tplConstraint = parentize(tplConstraint);
		this.aggrBody = parentize(aggrBody);
		
		membersScope = aggrBody instanceof DeclBlock ? 
				((DeclBlock) aggrBody) :
				parentize(new DeclBlock(ArrayView.<ASTNode>createFrom())); 
		assertNotNull(membersScope);
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
	
	
	/* ----------------- ----------------- */
	
	@Override
	protected AggregateSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		InstrinsicsScope commonTypeScope = new InstrinsicsScope(D2_063_intrinsics.createCommonProperties(this));
		return new AggregateSemantics(this, commonTypeScope, pickedElement);
	}
	
	public class AggregateSemantics extends TypeSemantics {
		
		protected final InstrinsicsScope commonTypeScope;
		
		public AggregateSemantics(IConcreteNamedElement typeElement, InstrinsicsScope commonTypeScope, 
				PickedElement<?> pickedElement) {
			super(typeElement, pickedElement);
			this.commonTypeScope = commonTypeScope;
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonScopeLookup search) {
			resolveSearchInScope(search, DefinitionAggregate.this.membersScope);
			commonTypeScope.resolveSearchInScope(search);
		}
	
	}
	
	@Override
	public void resolveSearchInScope(CommonScopeLookup search) {
		search.evaluateScopeNodeList(tplParams);
	}
	
}