/*******************************************************************************
 * Copyright (c) 2012, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ExpSemantics;
import melnorme.lang.tooling.engine.resolver.TypeReferenceResult;
import dtool.engine.analysis.DeeLanguageIntrinsics;

public class InitializerArray extends Expression implements IInitializer {
	
	public final NodeVector<ArrayInitEntry> entries;
	
	public InitializerArray(NodeVector<ArrayInitEntry> entries) {
		this.entries = parentize(assertNotNull(entries));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.INITIALIZER_ARRAY;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, entries);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new InitializerArray(clone(entries));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNodeList("[", entries, ", ", "]");
	}
	
	public static class ArrayInitEntry extends ASTNode {
		
		public final Expression index;
		public final IInitializer value;
		
		public ArrayInitEntry(Expression index, IInitializer value) {
			this.index = parentize(index);
			this.value = parentize(assertNotNull(value));
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.ARRAY_INIT_ENTRY;
		}
		
		@Override
		public void visitChildren(IASTVisitor visitor) {
			acceptVisitor(visitor, index);
			acceptVisitor(visitor, value);
		}
		
		@Override
		protected CommonASTNode doCloneTree() {
			return new ArrayInitEntry(clone(index), clone(value));
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append(index, " : ");
			cp.append(value);
		}
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected ExpSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new ExpSemantics(this, pickedElement) {
			
			@Override
			public TypeReferenceResult doCreateExpResolution() {
				return new TypeReferenceResult(DeeLanguageIntrinsics.D2_063_intrinsics.dynArrayType);
			}
			
		};
	}
	
}