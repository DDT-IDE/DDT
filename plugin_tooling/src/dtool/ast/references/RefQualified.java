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

import static dtool.util.NewUtils.assertInstance;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.INamedElement;
import dtool.ast.expressions.ExpLiteralInteger;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;
import dtool.engine.modules.IModuleResolver;

/**
 * A normal qualified reference.
 */
public class RefQualified extends CommonQualifiedReference {
	
	public final Resolvable qualifier;
	public final boolean isExpressionQualifier;
	public final int dotOffset;
	
	public RefQualified(IQualifierNode qualifier, int dotOffset, RefIdentifier qualifiedId) {
		super(assertNotNull(qualifiedId));
		this.qualifier = parentizeI(assertInstance(qualifier, Resolvable.class));
		this.dotOffset = dotOffset;
		this.isExpressionQualifier = isExpressionQualifier(qualifier);
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
	public Collection<INamedElement> findRootDefUnits(IModuleResolver moduleResolver) {
		return qualifier.findTargetDefElements(moduleResolver, false);
	}
	
}