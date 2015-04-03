/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.engine_client;

import java.nio.file.Path;

import melnorme.lang.tooling.completion.CompletionSoftFailure;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.misc.Location;

import org.eclipse.core.runtime.CoreException;

import dtool.engine.operations.DeeCompletionSearchResult;
import dtool.engine.operations.DeeCompletionSearchResult.DeeCompletionProposal;

public class DeeCompletionOperation {
	
	// Tests may modify this variable, but only tests
	public static volatile Location compilerPathOverride = null;
	
	protected final DToolClient dtoolClient;
	
	public DeeCompletionOperation(DToolClient dtoolClient) {
		this.dtoolClient = dtoolClient;
	}
	
	public ArrayList2<DeeCompletionProposal> execute(Path filePath, int offset, String source, int timeoutMillis) 
			throws CoreException, CompletionSoftFailure {
		DeeCompletionSearchResult completionResult = dtoolClient.performCompletionOperation(
			filePath, offset, source, timeoutMillis);
		
		return completionResult.handleResult();
	}
	
}