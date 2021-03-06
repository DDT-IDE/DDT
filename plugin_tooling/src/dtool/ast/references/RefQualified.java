/*******************************************************************************
 * Copyright (c) 2011 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.references;

import static dtool.util.NewUtils.assertInstance;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.expressions.ExpLiteralInteger;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;

/**
 * A normal qualified reference.
 */
public class RefQualified extends CommonQualifiedReference {
	
	public final Resolvable qualifier;
	public final boolean isExpressionQualifier;
	public final int dotOffset;
	
	public RefQualified(IQualifierNode qualifier, int dotOffset, RefIdentifier qualifiedId) {
		super(assertNotNull(qualifiedId));
		this.qualifier = parentize(assertInstance(qualifier, Resolvable.class));
		this.isExpressionQualifier = isExpressionQualifier(qualifier);
		this.dotOffset = dotOffset;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_QUALIFIED;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, qualifier);
		acceptVisitor(visitor, qualifiedId);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new RefQualified(clone((IQualifierNode) qualifier), dotOffset, clone(qualifiedId));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(qualifier, qualifier instanceof ExpLiteralInteger ? " ." : ".");
		cp.append(qualifiedId);
	}
	
	@Override
	public int getDotOffset() {
		return dotOffset;
	}
	
	public static boolean isExpressionQualifier(IQualifierNode qualifier) {
		return qualifier instanceof Expression || 
			((qualifier instanceof RefQualified) && ((RefQualified) qualifier).isExpressionQualifier);
	}
	
	@Override
	public INamedElement resolveRootNameElement(ISemanticContext context) {
		return qualifier.resolveAsQualifiedRefRoot(context);
	}
	
}