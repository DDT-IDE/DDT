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
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.engine.resolver.IResolvableSemantics;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.expressions.Resolvable;
import dtool.resolver.DeeLanguageIntrinsics;

/**
 * An reference consisting of an element reference and an indexing paramater .
 * Can represent a static array, associative array (aka map), or tuple indexing. 
 * It can be possible to determine which one it represents by syntax analysis only (example: foo[int] or foo[4]), 
 * but sometimes semantic analysis is necessary 
 * (example foo[bar] - is bar a number or a type? is foo a type or a tuple?)
 */
public class RefIndexing extends Reference {
	
	public final Reference elemType;
	public final Resolvable indexArg;
	
	public RefIndexing(Reference keyType, Resolvable indexArg) {
		this.elemType = parentize(keyType);
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
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(elemType);
		cp.append("[", indexArg, "]");
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public IResolvableSemantics getSemantics() {
		return semantics;
	}
	
	protected final IResolvableSemantics semantics = new ResolvableSemantics(this) {
		
		@Override
		public Collection<INamedElement> findTargetDefElements(ISemanticContext mr, boolean findOneOnly) {
			//TODO infer if its a static array, map array, or tupe
			// Assume it's a static array. 
			return Resolvable.wrapResult(DeeLanguageIntrinsics.D2_063_intrinsics.staticArrayType);
		}
		
	};
	
}