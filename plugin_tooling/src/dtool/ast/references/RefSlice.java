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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import dtool.ast.expressions.Expression;
import dtool.engine.common.IDeeNamedElement;
import dtool.engine.modules.IModuleResolver;

public class RefSlice extends Reference {
	
	public final Reference slicee;
	public final Expression startIndex;
	public final Expression endIndex;
	
	public RefSlice(Reference slicee, Expression startIndex, Expression endIndex) {
		this.slicee = parentize(slicee);
		this.startIndex = parentize(assertNotNull(startIndex));
		this.endIndex = parentize(assertNotNull(endIndex));
		assertTrue((endIndex == null) || (startIndex != null));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_SLICE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, slicee);
		acceptVisitor(visitor, startIndex);
		acceptVisitor(visitor, endIndex);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(slicee, "[");
		cp.append(startIndex);
		cp.append(" .. ", endIndex);
		cp.append("]");
	}
	
	@Override
	public Collection<IDeeNamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return null; // TODO:
	}
}