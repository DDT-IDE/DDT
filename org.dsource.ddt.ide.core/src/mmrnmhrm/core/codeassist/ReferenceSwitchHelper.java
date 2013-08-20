/*******************************************************************************
 * Copyright (c) 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.codeassist;

import dtool.ast.ASTNode;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.references.NamedReference;
import dtool.ast.references.Reference;

public class ReferenceSwitchHelper {
	
	public Reference reference;
	
	public void switchOnPickedNode(ASTNode node) {
		if(node instanceof DefSymbol){
			DefSymbol defSymbol = (DefSymbol) node;
			nodeIsDefSymbol(defSymbol);
		} else if(node instanceof NamedReference) {
			NamedReference namedReference = (NamedReference) node;
			nodeIsNamedReference(namedReference);
		} else if(node instanceof Reference) {
			Reference reference = (Reference) node;
			nodeIsNonNamedReference(reference);
		} else {
			nodeIsNotReference();
		}
	}
	
	@SuppressWarnings("unused")
	protected void nodeIsDefSymbol(DefSymbol defSymbol) {
	}
	
	protected void nodeIsNotReference() {
	}
	
	@SuppressWarnings("unused")
	protected void nodeIsNonNamedReference(Reference reference) {
	}
	
	protected void nodeIsNamedReference(NamedReference namedReference) {
		if(namedReference.isMissingCoreReference()) {
			nodeIsNamedReference_missing(namedReference);
		} else {
			nodeIsNamedReference_ok(namedReference);
		}
	}
	
	@SuppressWarnings("unused")
	protected void nodeIsNamedReference_missing(NamedReference namedReference) {
	}
	
	@SuppressWarnings("unused")
	protected void nodeIsNamedReference_ok(NamedReference namedReference) {
	}
	
}