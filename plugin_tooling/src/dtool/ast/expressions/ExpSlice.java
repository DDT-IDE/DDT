/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;

public class ExpSlice extends Expression {
	
	public final Expression slicee;
	public final Expression startIndex;
	public final Expression endIndex;
	
	public ExpSlice(Expression slicee, Expression startIndex, Expression endIndex) {
		this.slicee = parentizeI(assertNotNull(slicee));
		this.startIndex = parentize(startIndex);
		this.endIndex = parentize(endIndex);
		assertTrue((endIndex == null) || (startIndex != null));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_SLICE;
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
		if(startIndex != null) {
			cp.append(startIndex);
			cp.append(" .. ", endIndex);
		}
		cp.append("]");
	}
}