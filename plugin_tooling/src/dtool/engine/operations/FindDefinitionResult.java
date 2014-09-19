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
package dtool.engine.operations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.nio.file.Path;
import java.util.List;

import melnorme.lang.tooling.ast.SourceRange;

public class FindDefinitionResult {
	
	public final String errorMessage;
	public final List<FindDefinitionResultEntry> results;
	
	public FindDefinitionResult(String errorMessage) {
		this.errorMessage = errorMessage;
		this.results = null;
	}
	
	public FindDefinitionResult(List<FindDefinitionResultEntry> results) {
		this.errorMessage = null;
		this.results = results;
	}
	
	public FindDefinitionResult createFailureResult(String errorMessage) {
		return new FindDefinitionResult(errorMessage);
	}
	
	
	public static class FindDefinitionResultEntry {
		
		public final String extendedName;
		public final boolean isLanguageIntrinsic;
		public final Path modulePath;
		public final SourceRange sourceRange;
		
		public FindDefinitionResultEntry(String extendedName, boolean isLanguageIntrinsic, Path compilationUnitPath, 
				SourceRange sourceRange) {
			this.extendedName = assertNotNull(extendedName);
			this.isLanguageIntrinsic = isLanguageIntrinsic;
			this.modulePath = compilationUnitPath;
			this.sourceRange = sourceRange;
		}
		
		public boolean isLanguageIntrinsic() {
			return isLanguageIntrinsic;
		}
		
	}
	
}