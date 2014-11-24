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

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.engine.resolver.IResolvable;
import melnorme.lang.tooling.engine.resolver.IResolvableSemantics;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.expressions.IInitializer;
import dtool.engine.analysis.IVarDefinitionLike;

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
	
	/* -----------------  ----------------- */
	
	@Override
	public IResolvableSemantics getSemantics() {
		return semantics;
	}
	
	protected final IResolvableSemantics semantics = new ResolvableSemantics(this) {
		
		@Override
		public Collection<INamedElement> findTargetDefElements(ISemanticContext mr, boolean findOneOnly) {
			IInitializer initializer = getParent_().getDeclaredInitializer();
			if(initializer instanceof IResolvable) {
				IResolvable valueNode = (IResolvable) initializer;
				return valueNode.resolveTypeOfUnderlyingValue(mr);
			}
			return null;
		}
		
	};
	
}