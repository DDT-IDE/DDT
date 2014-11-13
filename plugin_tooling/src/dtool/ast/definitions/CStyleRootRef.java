/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.definitions;

import java.util.Collection;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.references.Reference;
import dtool.engine.common.IDeeNamedElement;
import dtool.engine.modules.IModuleResolver;

/**
 * Adapter class to parse C-style postfix declarator, i.e., 
 * the array type info after the symbol name: int* foo[] 
 */
public class CStyleRootRef extends Reference {
	
	public CStyleRootRef() { }
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.CSTYLE_ROOT_REF;
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
	}

	@Override
	public Collection<IDeeNamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return null;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
}