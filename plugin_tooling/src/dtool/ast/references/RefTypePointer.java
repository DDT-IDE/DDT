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

import java.util.Collection;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.resolver.IResolvableSemantics;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics.TypeReferenceSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.expressions.Resolvable;
import dtool.resolver.DeeLanguageIntrinsics;

public class RefTypePointer extends CommonNativeTypeReference {
	
	public final Reference elemType;
	
	public RefTypePointer(Reference elemType) {
		this.elemType = parentize(elemType);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_TYPE_POINTER;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, elemType);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(elemType, "*");
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public IResolvableSemantics getSemantics() {
		return semantics;
	}
	
	protected final IResolvableSemantics semantics = new TypeReferenceSemantics(this) {
		
		@Override
		public Collection<INamedElement> findTargetDefElements(ISemanticContext mr, boolean findOneOnly) {
			return Resolvable.wrapResult(DeeLanguageIntrinsics.D2_063_intrinsics.pointerType);
		}
		
	};
	
}