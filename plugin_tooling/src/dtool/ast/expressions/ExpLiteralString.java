/*******************************************************************************
 * Copyright (c) 2010, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.expressions;

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ExpSemantics;
import melnorme.lang.tooling.engine.resolver.TypeReferenceResult;
import dtool.engine.analysis.DeeLanguageIntrinsics;
import dtool.parser.common.IToken;

public class ExpLiteralString extends Expression {
	
	public final IToken[] stringTokens;
	
	public ExpLiteralString(IToken stringToken) {
		this(new IToken[] { stringToken });
	}
	
	public ExpLiteralString(IToken[] stringToken) {
		this.stringTokens = stringToken;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_LITERAL_STRING;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new ExpLiteralString(stringTokens);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		for (IToken stringToken : stringTokens) {
			cp.appendToken(stringToken);
		}
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected ExpSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new ExpSemantics(this, pickedElement) {
		
			@Override
			public TypeReferenceResult doCreateExpResolution() {
				return resolveTypeReference(DeeLanguageIntrinsics.D2_063_intrinsics.string_type);
			}
			
		};
	}
	
}