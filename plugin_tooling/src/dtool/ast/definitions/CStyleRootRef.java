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

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.ast_actual.ILangNamedElement;
import melnorme.lang.tooling.bundles.IModuleResolver;
import dtool.ast.references.Reference;

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
	public Collection<ILangNamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return null;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
}