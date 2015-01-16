/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine.analysis.templates;

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.IReference;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.AliasSemantics.RefAliasSemantics;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.EArcheType;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.Reference;

public class AliasElement extends InstantiatedDefUnit {
	
	public final Reference target; // non-children member
	
	public AliasElement(DefSymbol defName, Resolvable target) {
		super(defName);
		this.target = (target instanceof Reference) ? 
				(Reference) target :  
				null; // TODO: error element
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.TEMPLATE_ALIAS_PARAM__INSTANCE;
	}
	
	@Override
	public void visitChildren_rest(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode_instantiatedDefUnit(ASTCodePrinter cp) {
		cp.append("alias ", defName);
		cp.append(" = ", target);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new AliasElement(clone(defName), clone(target));
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Alias;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new RefAliasSemantics(this, pickedElement) {
			@Override
			protected IReference getAliasTarget() {
				return target;
			}
		};
	}
	
}