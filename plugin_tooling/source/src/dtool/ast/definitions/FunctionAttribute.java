/*******************************************************************************
 * Copyright (c) 2012 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.definitions;

import dtool.ast.declarations.AttribBasic;
import dtool.parser.DeeTokens;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.ISourceRepresentation;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;

/** 
 * Function postfix attribute.
 * Similar implementation to {@link AttribBasic}. 
 */
public class FunctionAttribute extends ASTNode implements IFunctionAttribute {
	
	protected final FunctionAttributes attribKind;
	
	public FunctionAttribute(FunctionAttributes attribKind) {
		this.attribKind = attribKind;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.FUNCTION_POSTFIX_ATTRIB;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new FunctionAttribute(attribKind);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendToken(attribKind);
	}
	
	public static enum FunctionAttributes implements ISourceRepresentation {
		CONST(DeeTokens.KW_CONST.getSourceValue()), 
		IMMUTABLE(DeeTokens.KW_IMMUTABLE.getSourceValue()), 
		INOUT(DeeTokens.KW_INOUT.getSourceValue()), 
		SHARED(DeeTokens.KW_SHARED.getSourceValue()),
		
		PURE(DeeTokens.KW_PURE.getSourceValue()),
		NOTHROW(DeeTokens.KW_NOTHROW.getSourceValue()),
		
		;
		public final String sourceValue;
		
		private FunctionAttributes(String sourceValue) {
			this.sourceValue = sourceValue;
		}
		
		@Override
		public String getSourceValue() {
			return sourceValue;
		}
		
		public static FunctionAttributes fromToken(DeeTokens token) {
			switch (token) {
			case KW_CONST: return CONST;
			case KW_IMMUTABLE: return IMMUTABLE;
			case KW_INOUT: return INOUT;
			case KW_SHARED: return SHARED;
			case KW_PURE: return PURE;
			case KW_NOTHROW: return NOTHROW;
			
			default: return null;
			}
		}
		
	}
	
}