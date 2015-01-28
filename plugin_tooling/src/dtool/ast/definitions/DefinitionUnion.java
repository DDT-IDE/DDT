/*******************************************************************************
 * Copyright (c) 2012, 2014 Bruno Medeiros and other Contributors.
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
import dtool.ast.expressions.Expression;
import dtool.ast.references.RefTemplateInstance;
import dtool.parser.common.Token;

/**
 * A definition of an union aggregate.
 */
public class DefinitionUnion extends DefinitionAggregate {
	
	public DefinitionUnion(Token[] comments, DefSymbol defName, NodeVector<ITemplateParameter> tplParams,
		Expression tplConstraint, IAggregateBody aggrBody) {
		super(comments, defName, tplParams, tplConstraint, aggrBody);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_UNION;
	}
	
	@Override	
	public void visitChildren(IASTVisitor visitor) {
		acceptNodeChildren(visitor);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new DefinitionUnion(comments, clone(defName), clone(tplParams), clone(tplConstraint), clone(aggrBody));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		aggregateToStringAsCode(cp, "union ", true);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Union;
	}
	
	@Override
	public DefUnit cloneTemplateElement(final RefTemplateInstance templateRef) {
		return setParsedFromOther(
			new DefinitionUnion(comments, clone(defName), null, null, clone(aggrBody)) {
				
				@Override
				public String getExtendedName() {
					return getName() + templateRef.normalizedArgsToString();
				}
				
			}, this);
	}
	
}