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
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.parser.common.IToken;

public class RefPrimitive extends NamedReference {
	
	public final IToken primitive;
	
	public RefPrimitive(IToken primitiveToken) {
		this.primitive = assertNotNull(primitiveToken);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_PRIMITIVE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendToken(primitive);
	}
	
	@Override
	public String getCoreReferenceName() {
		return primitive.getSourceValue();
	}
	
	@Override
	public boolean isMissingCoreReference() {
		return false;
	}
	
}