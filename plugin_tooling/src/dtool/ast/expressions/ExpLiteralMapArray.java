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

public class ExpLiteralMapArray extends Expression {
	
	public final NodeVector<MapArrayLiteralKeyValue> entries;
	
	public ExpLiteralMapArray(NodeVector<MapArrayLiteralKeyValue> entries) {
		this.entries = parentize(assertNotNull(entries));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_LITERAL_MAPARRAY;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, entries);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new ExpLiteralMapArray(clone(entries));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNodeList("[ ", entries, ", ", " ]");
	}
	
	
	public static class MapArrayLiteralKeyValue extends ASTNode {
		public final Expression key;
		public final Expression value;
		
		public MapArrayLiteralKeyValue(Expression key, Expression value) {
			this.key = parentize(assertNotNull(key));
			this.value = parentize(value);
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.MAPARRAY_ENTRY;
		}
		
		@Override
		public void visitChildren(IASTVisitor visitor) {
			acceptVisitor(visitor, key);
			acceptVisitor(visitor, value);
		}
		
		@Override
		protected CommonASTNode doCloneTree() {
			return new MapArrayLiteralKeyValue(clone(key), clone(value));
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append(key);
			cp.append(" : ");
			cp.append(value);
		}
	}
	
}