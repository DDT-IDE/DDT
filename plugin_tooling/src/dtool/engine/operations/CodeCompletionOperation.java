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

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import dtool.engine.AbstractBundleResolution.ResolvedModule;
import dtool.engine.SemanticManager;
import dtool.resolver.PrefixDefUnitSearch;

public class CodeCompletionOperation extends AbstractDToolOperation {
	
	public CodeCompletionOperation(SemanticManager semanticManager) {
		super(semanticManager);
	}
	
	public CompletionSearchResult doCodeCompletion(Path filePath, int offset, Path compilerPath)
			throws ExecutionException {
		if(filePath == null) { 
			throw new ExecutionException(new Exception("Invalid path for content assist source.")); 
		}
		
		ResolvedModule resolvedModule = getSemanticManager().getUpdatedResolvedModule(filePath, compilerPath);
		return PrefixDefUnitSearch.completionSearch(resolvedModule.getParsedModule(), offset, 
			resolvedModule.getModuleResolver());
	}
	
}