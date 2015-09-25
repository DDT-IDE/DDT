/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine.analysis.templates;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefUnit;

/**
 * Instantiated element, usually for template params.
 */
public abstract class InstantiatedDefUnit extends DefUnit {
	
	public InstantiatedDefUnit(DefSymbol defName) {
		super(defName.createCopy());
		assertTrue(defName.isSemanticReady());
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		toStringAsCode_instantiatedDefUnit(cp);
		cp.append(";");
	}
	
	public abstract void toStringAsCode_instantiatedDefUnit(ASTCodePrinter cp);
	
	@Override
	public final void visitChildren(IASTVisitor visitor) {
		// We dont visit defName as child, because the source range is not consistent with the parent
		//acceptVisitor(visitor, defName);
	}
	
	@SuppressWarnings("unused")
	public final void visitChildren_rest(IASTVisitor visitor) {
		
	}
	
}