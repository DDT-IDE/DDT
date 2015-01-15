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
import dtool.ast.references.RefIdentifier;

public class InitializerStruct extends Initializer {
	
	public final NodeVector<StructInitEntry> entries;
	
	public InitializerStruct(NodeVector<StructInitEntry> entries) {
		this.entries = parentize(entries);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.INITIALIZER_STRUCT;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, entries);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new InitializerStruct(clone(entries));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNodeList("{", entries, ", ", "}");
	}
	
	public static class StructInitEntry extends ASTNode {
		
		public final RefIdentifier member;
		public final IInitializer value;
		
		public StructInitEntry(RefIdentifier member, IInitializer value) {
			this.member = parentize(member);
			this.value = parentize(assertNotNull(value));
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.STRUCT_INIT_ENTRY;
		}
		
		@Override
		public void visitChildren(IASTVisitor visitor) {
			acceptVisitor(visitor, member);
			acceptVisitor(visitor, value);
		}
		
		@Override
		protected CommonASTNode doCloneTree() {
			return new StructInitEntry(clone(member), clone(value));
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append(member, " : ");
			cp.append(value);
		}
	}
	
}