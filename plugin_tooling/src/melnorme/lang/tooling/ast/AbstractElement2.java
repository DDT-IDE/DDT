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
package melnorme.lang.tooling.ast;

import melnorme.lang.tooling.ast.util.ASTCodePrinter;


public abstract class AbstractElement2 implements ISemanticElement {
	
	public AbstractElement2() {
		super();
	}
	
	/* -----------------  ----------------- */
	
	/** 
	 * Returns a source representation of this element
	 * 
	 * For nodes: if node parsed without errors then this representation should be equal 
	 * to the original parsed source (disregarding sub-channel tokens). Otherwise, if there were errors, 
	 * this method should still try to print something as close as possible to the original parsed source. 
	 * 
	 * All tokens that were consumed should be printed.
	 * 
	 * Expected tokens that were *not* consumed should preferably be printed as well, but it is not strictly required.
	 * 
	 * This method may be used by for UI display (or tests), but it is not precise enough to be 
	 * used for any semantic purposes.
	 * 
	 */
	public final String toStringAsCode() {
		ASTCodePrinter cp = new ASTCodePrinter();
		toStringAsCode(cp);
		return cp.toString();
	}
	
	/** @see #toStringAsCode() */
	public abstract void toStringAsCode(ASTCodePrinter cp);
	
	
	// String printer helper
	
	public final String toStringClassName() {
		return this.getClass().getSimpleName();
	}
	
}