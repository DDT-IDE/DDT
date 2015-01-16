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
package dtool.engine.analysis.templates;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefUnit;

public abstract class InstantiatedDefUnit extends DefUnit {
	
	public InstantiatedDefUnit(DefSymbol defName) {
		super(defName.createCopy());
		assertTrue(defName.isCompleted());
		setSourceRange(defName.getSourceRange());
		setParsedStatus();
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		toStringAsCode_instantiatedDefUnit(cp);
		cp.append(";");
	}
	
	public abstract void toStringAsCode_instantiatedDefUnit(ASTCodePrinter cp);
	
	@Override
	public final void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defName);
	}
	
	public abstract void visitChildren_rest(IASTVisitor visitor);
	
}