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
package dtool.ast.expressions;

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ExpSemantics;
import melnorme.lang.tooling.engine.resolver.TypeReferenceResult;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.ITypeNamedElement;
import dtool.ast.definitions.DefinitionClass_Common;

public class ExpSuper extends Expression {
	
	public ExpSuper() {
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_SUPER;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new ExpSuper();
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("super");
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected ExpSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new ExpSemantics(this, pickedElement) {
		
			@Override
			public TypeReferenceResult doCreateExpResolution() {
				DefinitionClass_Common definitionClass = ExpThis.getClassNodeParent(ExpSuper.this);
				if(definitionClass == null) {
					return null;
				}
				
				INamedElement superClass = definitionClass.getSemantics(context).resolveSuperClass(context);
				if(superClass instanceof ITypeNamedElement) {
					return new TypeReferenceResult((ITypeNamedElement) superClass);
				}
				return null;
			}
			
		};
	}
	
}