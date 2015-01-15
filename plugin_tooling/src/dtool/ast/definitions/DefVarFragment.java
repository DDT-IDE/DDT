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

import static dtool.util.NewUtils.assertCast;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import dtool.ast.expressions.IInitializer;
import dtool.ast.references.Reference;
import dtool.engine.analysis.CommonDefVarSemantics;
import dtool.engine.analysis.IVarDefinitionLike;

/**
 * A fragment of a variable definition in a multi-identifier variable declaration
 */
public class DefVarFragment extends DefUnit implements IVarDefinitionLike {
	
	public final IInitializer initializer;
	
	public DefVarFragment(DefSymbol defName, IInitializer initializer) {
		super(defName);
		this.initializer = parentize(initializer);
	}
	
	@Override
	public DefinitionVariable getParent_Concrete() {
		return assertCast(parent, DefinitionVariable.class);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_VAR_FRAGMENT;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defName);
		acceptVisitor(visitor, initializer);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new DefVarFragment(clone(defName), clone(initializer));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(defName);
		cp.append("= ", initializer);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	@Override
	public Reference getDeclaredType() {
		return getParent_Concrete().type;
	}
	
	@Override
	public IInitializer getDeclaredInitializer() {
		return initializer;
	}
	
	/* ----------------- ----------------- */
	
	@Override
	protected CommonDefVarSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new CommonDefVarSemantics(this, pickedElement);
	}
	
}