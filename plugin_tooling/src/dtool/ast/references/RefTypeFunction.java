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

import melnorme.utilbox.core.CoreUtil;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.FunctionAttributes;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.definitions.INamedElement;
import dtool.ast.expressions.Resolvable;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.LanguageIntrinsics;
import dtool.resolver.LanguageIntrinsics.DeeIntrinsicType;
import dtool.util.ArrayView;

/**
 * A function pointer type
 */
public class RefTypeFunction extends CommonRefNative {
	
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
	
	@Override
	public Collection<INamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return Resolvable.wrapResult(intrinsicFunctionTypeInstance);
	}
	
	public static final IntrinsicFunctionType intrinsicFunctionTypeInstance = new IntrinsicFunctionType();
	
	public static class IntrinsicFunctionType extends DeeIntrinsicType {
		public IntrinsicFunctionType() {
			LanguageIntrinsics.D2_063_intrinsics.super("<funtion>", null);
		}
	}
}