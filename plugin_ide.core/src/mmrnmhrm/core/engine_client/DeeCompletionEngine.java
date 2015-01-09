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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.utilbox.collections.ArrayList2;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.codeassist.ScriptCompletionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.CompletionContext;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.CompletionRequestor;

public class DeeCompletionEngine extends ScriptCompletionEngine {
	
	protected CompletionRequestor getRequestor() {
		return requestor;
	}
	
	@Deprecated
	@Override
	public void complete(IModuleSource moduleSource, final int position, int i) {
		assertNotNull(requestor);
		requestor.beginReporting();
		try {
			CompletionContext context = new CompletionContext();
			requestor.acceptContext(context);
			
			ArrayList2<CompletionProposal> proposals = new DeeCompletionOperation().runCompletion(
				moduleSource, position);
			
			if(proposals != null) {
				for(CompletionProposal proposal : proposals) {
					requestor.accept(proposal);
				}
			}
			
		} catch (CoreException e) {
			DeeCore.logStatus(e);
			DeeCompletionOperation.handleCompletionFailure(e.getMessage(), position, requestor);
		} finally {
			requestor.endReporting();
		}
	}
	
}