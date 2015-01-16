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

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ReferenceSemantics.TypeReferenceSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayView;
import melnorme.utilbox.core.CoreUtil;
import dtool.ast.definitions.IFunctionAttribute;
import dtool.ast.definitions.IFunctionParameter;
import dtool.engine.analysis.DeeLanguageIntrinsics;
import dtool.engine.analysis.DeeLanguageIntrinsics.DeeIntrinsicType;

/**
 * A function pointer type
 */
public class RefTypeFunction extends CommonNativeTypeReference {
	
	public final Reference retType;
	public final boolean isDelegate;
	public final NodeVector<IFunctionParameter> params;
	public final NodeVector<IFunctionAttribute> fnAttributes;
	
	public RefTypeFunction(Reference retType, boolean isDelegate, NodeVector<IFunctionParameter> params, 
			NodeVector<IFunctionAttribute> fnAttributes) {
		this.retType = parentize(retType);
		this.isDelegate = isDelegate;
		this.params = parentize(params);
		this.fnAttributes = parentize(fnAttributes);
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
		acceptVisitor(visitor, fnAttributes);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new RefTypeFunction(clone(retType), isDelegate, clone(params), clone(fnAttributes));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(retType, " ");
		cp.append(isDelegate ? "delegate" : "function");
		cp.appendList("(", getParams_asNodes(), ",", ") ");
		cp.appendList(fnAttributes, " ", true);
	}
	
	/* -----------------  ----------------- */

	@Override
	protected TypeReferenceSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new TypeReferenceSemantics(this, pickedElement) {
		
			@Override
			public INamedElement doResolveTargetElement() {
				return intrinsicFunctionTypeInstance;
			}
			
		};
	}
	
	public static final IntrinsicFunctionType intrinsicFunctionTypeInstance = new IntrinsicFunctionType();
	
	public static class IntrinsicFunctionType extends DeeIntrinsicType {
		public IntrinsicFunctionType() {
			DeeLanguageIntrinsics.D2_063_intrinsics.super("<funtion>", null);
			createMembers();
		}
		
		@Override
		protected void doSetCompleted() {
		}
	}
	
}