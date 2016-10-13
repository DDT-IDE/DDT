/*******************************************************************************
 * Copyright (c) 2012 Bruno Medeiros and other Contributors.
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
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import dtool.ast.expressions.Expression;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.Reference;
import dtool.parser.common.Token;

/**
 * A definition of an interface aggregate. 
 */
public class DefinitionClass extends DefinitionClass_Common {
	
	public DefinitionClass(Token[] comments, DefSymbol defId, NodeVector<ITemplateParameter> tplParams,
		Expression tplConstraint, NodeVector<Reference> baseClasses, boolean baseClassesAfterConstraint, 
		IAggregateBody aggrBody) 
	{
		super(comments, defId, tplParams, tplConstraint, baseClasses, baseClassesAfterConstraint, aggrBody);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_CLASS;
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new DefinitionClass(comments, clone(defName), clone(tplParams), clone(tplConstraint), 
			clone(baseClasses), baseClassesAfterConstraint, clone(aggrBody));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		classLikeToStringAsCode(cp, "class ");
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Class;
	}
	
	@Override
	public DefUnit cloneTemplateElement(final RefTemplateInstance templateRef) {
		return setParsedFromOther(
			new DefinitionClass(comments, clone(defName), null, null, 
				clone(baseClasses), baseClassesAfterConstraint, clone(aggrBody)) {
				
				@Override
				public String getExtendedName() {
					return getName() + templateRef.normalizedArgsToString();
				}
				
			}, this);
	}
	
}