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
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ReferenceSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.expressions.Resolvable;
import dtool.engine.analysis.DeeLanguageIntrinsics;

/**
 * An reference consisting of an element reference and an indexing paramater .
 * Can represent a static array, associative array (aka map), or tuple indexing. 
 * It can be possible to determine which one it represents by syntax analysis only (example: foo[int] or foo[4]), 
 * but sometimes semantic analysis is necessary 
 * (example foo[bar] - is bar a number or a type? is foo a type or a tuple?)
 */
public class RefIndexing extends Reference implements IQualifierNode {
	
	public final Reference elemType;
	public final Resolvable indexArg;
	
	public RefIndexing(Reference elemType, Resolvable indexArg) {
		this.elemType = parentize(elemType);
		this.indexArg = parentize(indexArg);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_INDEXING;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, elemType);
		acceptVisitor(visitor, indexArg);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new RefIndexing(clone(elemType), clone(indexArg));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(elemType);
		cp.append("[", indexArg, "]");
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected ReferenceSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new ReferenceSemantics(this, pickedElement) {
		
			@Override
			public INamedElement doResolveTargetElement() {
				//TODO infer if its a static array, map array, or tupe
				// Assume it's a static array. 
				return DeeLanguageIntrinsics.D2_063_intrinsics.staticArrayType;
			}
			
		};
	}
	
}