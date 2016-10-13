/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.parser;

import dtool.ast.definitions.DefSymbol;
import melnorme.lang.tooling.ast.SourceRange;
import melnorme.lang.tooling.common.ParserError;

public class ProtoDefSymbol {
	
	public final String name;
	public final SourceRange nameSourceRange;
	public final ParserError error;
	
	public ProtoDefSymbol(String name, SourceRange nameSourceRange, ParserError error) {
		this.name = name;
		this.nameSourceRange = nameSourceRange;
		this.error = error;
	}

	public boolean isMissing() {
		return error != null;
	}
	
	public int getStartPos() {
		return nameSourceRange.getStartPos();
	}
	
	public DefSymbol createDefId() {
		ProtoDefSymbol defIdTuple = this;
		DefSymbol defId = new DefSymbol(defIdTuple.name);
		if(defIdTuple.nameSourceRange != null) {
			defId.setSourceRange(defIdTuple.nameSourceRange);
		}
		if(defIdTuple.error == null) {
			defId.setParsedStatus();
		} else {
			defId.setParsedStatusWithErrors(defIdTuple.error);
		}
		return defId;
	}
	
}