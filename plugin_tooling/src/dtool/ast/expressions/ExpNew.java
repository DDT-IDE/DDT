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

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeListView;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ExpSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.references.RefIndexing;
import dtool.ast.references.Reference;

/**
 * New expression.
 * Note that the:
 * <code> new AllocatorArgumentsopt Type [ AssignExpression ] </code>
 * case is parsed as a {@link RefIndexing} containing Type and AssignExpression. 
 * Semantic analysis would be necessary to disambiguate.
 */
public class ExpNew extends Expression {
	
	public final Expression outerClassArg;
	public final NodeListView<Expression> allocArgs;
	public final Reference newtype;
	public final NodeListView<Expression> args;
	
	public ExpNew(Expression outerClassArg, NodeListView<Expression> atorArgs, Reference type,
		NodeListView<Expression> args) {
		this.outerClassArg = parentize(outerClassArg);
		this.allocArgs = parentize(atorArgs);
		this.newtype = parentize(type);
		this.args = parentize(args);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_NEW;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, outerClassArg);
		acceptVisitor(visitor, allocArgs);
		acceptVisitor(visitor, newtype);
		acceptVisitor(visitor, args);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("", outerClassArg, ".");
		cp.append("new");
		cp.appendNodeList("(", allocArgs, ", ", ")", " "); 
		cp.append(newtype);
		cp.appendNodeList("(", args, ", ", ")", " ");
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected ExpSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new ExpSemantics(this, pickedElement) {
		
		@Override
		public INamedElement doResolveTargetElement() {
			// This is not entirely correct for struct-like types, 
			// in that case a pointer to the the type is actually the type of the new exp.
			// But current behavior is acceptable for now.
			
			// Also, if the type ref is a static array, the return type is supposed to be a dynamic array,
			// but we don't implement that
			return findTargetElementsForReference(context, newtype);
		}
		
	};
	}
	
}