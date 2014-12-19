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

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics.TypeReferenceSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.engine.analysis.DeeLanguageIntrinsics;

public class RefTypeDynArray extends CommonNativeTypeReference {
	
	public final Reference elemtype;
	
	public RefTypeDynArray(Reference elemType) {
		this.elemtype = parentize(elemType);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_TYPE_DYN_ARRAY;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, elemtype);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(elemtype, "[]");
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected TypeReferenceSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new TypeReferenceSemantics(this, pickedElement) {
		
			@Override
			public INamedElement doResolveTargetElement() {
				return DeeLanguageIntrinsics.D2_063_intrinsics.dynArrayType;
			}
			
		};
	}
	
}