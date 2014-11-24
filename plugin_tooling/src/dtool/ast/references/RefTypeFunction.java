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
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.engine.resolver.IResolvableSemantics;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics.TypeReferenceSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayView;
import melnorme.utilbox.core.CoreUtil;
import dtool.ast.definitions.FunctionAttributes;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.expressions.Resolvable;
import dtool.resolver.DeeLanguageIntrinsics;
import dtool.resolver.DeeLanguageIntrinsics.DeeIntrinsicType;

/**
 * A function pointer type
 */
public class RefTypeFunction extends CommonNativeTypeReference {
	
	public final Reference retType;
	public final boolean isDelegate;
	public final ArrayView<IFunctionParameter> params;
	public final ArrayView<FunctionAttributes> fnAttributes;
	
	public RefTypeFunction(Reference retType, boolean isDelegate, ArrayView<IFunctionParameter> params, 
		ArrayView<FunctionAttributes> fnAttributes) {
		this.retType = parentize(retType);
		this.isDelegate = isDelegate;
		this.params = parentizeI(params);
		this.fnAttributes = fnAttributes;
	}
	
	public final ArrayView<ASTNode> getParams_asNodes() {
		return CoreUtil.blindCast(params);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_TYPE_FUNCTION;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, retType);
		acceptVisitor(visitor, params);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(retType, " ");
		cp.append(isDelegate ? "delegate" : "function");
		cp.appendList("(", getParams_asNodes(), ",", ") ");
		cp.appendTokenList(fnAttributes, " ", true);
	}
	
	/* -----------------  ----------------- */

	@Override
	public IResolvableSemantics getSemantics() {
		return semantics;
	}
	
	protected final IResolvableSemantics semantics = new TypeReferenceSemantics(this) {
		
		@Override
		public Collection<INamedElement> findTargetDefElements(ISemanticContext mr, boolean findOneOnly) {
			return Resolvable.wrapResult(intrinsicFunctionTypeInstance);
		}
		
	};
	
	public static final IntrinsicFunctionType intrinsicFunctionTypeInstance = new IntrinsicFunctionType();
	
	public static class IntrinsicFunctionType extends DeeIntrinsicType {
		public IntrinsicFunctionType() {
			DeeLanguageIntrinsics.D2_063_intrinsics.super("<funtion>", null);
		}
	}
	
}