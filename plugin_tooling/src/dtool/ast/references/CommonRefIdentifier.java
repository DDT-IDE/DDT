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


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTVisitor;

public abstract class CommonRefIdentifier extends NamedReference {
	
	protected final String identifier;
	
	public CommonRefIdentifier(String identifier) {
		this.identifier = identifier;
		assertTrue(identifier == null || identifier.length() > 0); 
		assertTrue(getDenulledIdentifier().indexOf(' ') == -1);
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(identifier);
	}
	
	@Override
	public String getCoreReferenceName() { 
		return identifier;
	}
	
	public boolean isMissing() {
		return identifier == null;
	}
	
	public String getDenulledIdentifier() {
		return identifier == null ? "" : identifier;
	}
	
}