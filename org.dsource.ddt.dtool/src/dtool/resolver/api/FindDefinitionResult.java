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
package dtool.resolver.api;

import java.nio.file.Path;
import java.util.List;

import dtool.ast.SourceRange;

public class FindDefinitionResult {
	
	public final String errorMessage;
	public final List<FindDefinitionResultEntry> results;
	public final Path originFilePath;
	
	public FindDefinitionResult(String errorMessage) {
		this.errorMessage = errorMessage;
		this.results = null;
		this.originFilePath = null;
	}
	
	public FindDefinitionResult(List<FindDefinitionResultEntry> results, Path compilationUnitPath) {
		this.errorMessage = null;
		this.results = results;
		this.originFilePath = compilationUnitPath;
	}

	public FindDefinitionResult createFailureResult(String errorMessage) {
		return new FindDefinitionResult(errorMessage);
	}
	
	
	public static class FindDefinitionResultEntry {
		
		public final SourceRange sourceRange;
		public final String extendedName;
		public final boolean isLanguageIntrinsic;
		public final Path compilationUnitPath;
		
		public FindDefinitionResultEntry(Path compilationUnitPath, SourceRange sourceRange, String extendedName, 
				boolean isLanguageIntrinsic) {
			this.sourceRange = sourceRange;
			this.extendedName = extendedName;
			this.isLanguageIntrinsic = isLanguageIntrinsic;
			this.compilationUnitPath = compilationUnitPath;
		}

		public boolean isLanguageIntrinsic() {
			return isLanguageIntrinsic;
		}
		
	}
	
}