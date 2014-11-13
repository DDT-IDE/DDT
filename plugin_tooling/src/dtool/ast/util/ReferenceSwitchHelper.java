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
package dtool.ast.util;

import melnorme.lang.tooling.ast_actual.ASTNode;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.references.NamedReference;
import dtool.ast.references.Reference;

public abstract class ReferenceSwitchHelper<RESULT> {
	
	public Reference reference;
	
	public RESULT switchOnPickedNode(ASTNode node) {
		if(node instanceof DefSymbol){
			DefSymbol defSymbol = (DefSymbol) node;
			return nodeIsDefSymbol(defSymbol);
		} else if(node instanceof NamedReference) {
			NamedReference namedReference = (NamedReference) node;
			return nodeIsNamedReference(namedReference);
		} else if(node instanceof Reference) {
			Reference reference = (Reference) node;
			return nodeIsNonNamedReference(reference);
		} else {
			return nodeIsNotReference();
		}
	}
	
	protected abstract RESULT nodeIsDefSymbol(DefSymbol defSymbol);
	
	protected abstract RESULT nodeIsNotReference();
	
	protected abstract RESULT nodeIsNonNamedReference(Reference reference);
	
	protected RESULT nodeIsNamedReference(NamedReference namedReference) {
		if(namedReference.isMissingCoreReference()) {
			return nodeIsNamedReference_missing(namedReference);
		} else {
			return nodeIsNamedReference_ok(namedReference);
		}
	}
	
	protected abstract RESULT nodeIsNamedReference_missing(NamedReference namedReference);
	
	protected abstract RESULT nodeIsNamedReference_ok(NamedReference namedReference);
	
}