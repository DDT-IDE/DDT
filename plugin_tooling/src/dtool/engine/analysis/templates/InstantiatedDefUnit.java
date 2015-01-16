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


import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefUnit;

public abstract class InstantiatedDefUnit extends DefUnit {
	
	public InstantiatedDefUnit(DefSymbol defname) {
		super(defname.createCopy());
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		toStringAsCode_instantiatedDefUnit(cp);
		cp.append(";");
	}
	
	public abstract void toStringAsCode_instantiatedDefUnit(ASTCodePrinter cp);
	
}