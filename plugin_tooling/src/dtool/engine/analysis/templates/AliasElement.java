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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.CommonVarSemantics;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.ResolvableUtil;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.EArcheType;
import dtool.ast.expressions.Resolvable;


public class AliasElement extends InstantiatedDefUnit implements IConcreteNamedElement {
	
	public final Resolvable targetValue; // non-children member
	
	public AliasElement(DefSymbol defName, Resolvable targetValue) {
		super(defName);
		this.targetValue = assertNotNull(targetValue);
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
		cp.append("@value_alias ", defName);
		cp.append(" = ", targetValue);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new AliasElement(clone(defName), targetValue);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new CommonVarSemantics(this, pickedElement) {
			@Override
			public INamedElement resolveTypeForValueContext() {
				return ResolvableUtil.resolveTypeOfExpression(targetValue, context);
			}
		};
	}
	
}