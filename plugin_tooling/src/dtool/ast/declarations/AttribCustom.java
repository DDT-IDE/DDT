/*******************************************************************************
 * Copyright (c) 2012, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import dtool.ast.expressions.Expression;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.Reference;

/**
 * Node for User Defined Attributes ( http://dlang.org/attribute.html#uda )
 * Note: the spec/compiler, as of 2.063 only allows an RefIdentifier as base reference 
 * (and it's a reference and not a symbol def, it should refer to another element.
 * 
 */
public class AttribCustom extends AttribAmpersat {
	
	public final Reference ref;
	public final NodeVector<Expression> args; // if null, no argument list
	
	public AttribCustom(Reference ref, NodeVector<Expression> args) {
		this.ref = parentize(ref);
		this.args = parentize(args);
		
		assertTrue(ref == null || ref instanceof RefIdentifier || ref instanceof RefTemplateInstance);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.ATTRIB_CUSTOM;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, ref);
		acceptVisitor(visitor, args);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new AttribCustom(clone(ref), clone(args));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("@");
		cp.append(ref);
		cp.appendNodeList("(", args, ",", ")");
	}
	
}