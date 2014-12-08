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

import dtool.engine.ResolvedModule;
import dtool.engine.SemanticManager;
import dtool.engine.compiler_installs.CompilerInstall;

public class AbstractDToolOperation {
	
	protected final SemanticManager semanticManager;
	protected final CompilerInstall compilerInstall;
	
	public AbstractDToolOperation(SemanticManager semanticManager) {
		this(semanticManager, null);
	}
	
	public AbstractDToolOperation(SemanticManager semanticManager, Path compilerPath) {
		this.semanticManager = semanticManager;
		this.compilerInstall = semanticManager.getDtoolServer().findBestCompilerInstall(compilerPath);
	}
	
	public SemanticManager getSemanticManager() {
		return semanticManager;
	}
	
	protected ResolvedModule getResolvedModule(Path filePath) throws ExecutionException {
		return semanticManager.getUpdatedResolvedModule(filePath, compilerInstall);
	}
	
}