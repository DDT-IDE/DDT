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
package dtool.ast.references;

import static melnorme.utilbox.core.CoreUtil.assertCast;

import java.util.Collection;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.INamedElement;
import dtool.ast.expressions.IInitializer;
import dtool.engine.common.IValueNode;
import dtool.engine.common.IVarDefinitionLike;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.api.DefUnitDescriptor;

/**
 * This reference node can only be parsed in special circumstances
 */
public final class AutoReference extends Reference {
	
	public AutoReference() {
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_AUTO;
	}
	
	@Override
	protected ASTNode getParent_Concrete() {
		assertCast(getParent(), IVarDefinitionLike.class);
		return super.getParent_Concrete();
	}
	
	public IVarDefinitionLike getParent_() {
		return assertCast(getParent(), IVarDefinitionLike.class);
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("auto");
	}
	
	@Override
	public Collection<INamedElement> findTargetDefElements(IModuleResolver mr, boolean findFirstOnly) {
		IInitializer initializer = getParent_().getDeclaredInitializer();
		if(initializer instanceof IValueNode) {
			IValueNode valueNode = (IValueNode) initializer;
			return valueNode.resolveTypeOfUnderlyingValue(mr);
		}
		return null;
	}
	
	@Override
	public boolean canMatch(DefUnitDescriptor defunit) {
		return false;
	}
	
}