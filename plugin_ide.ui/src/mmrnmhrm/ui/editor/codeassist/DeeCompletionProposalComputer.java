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
package mmrnmhrm.ui.editor.codeassist;

import java.util.List;

import melnorme.lang.ide.core.utils.TimeoutProgressMonitor;
import melnorme.lang.ide.ui.editor.actions.SourceOperationContext;
import melnorme.lang.ide.ui.text.completion.LangCompletionProposalComputer;
import melnorme.lang.tooling.ToolCompletionProposal;
import melnorme.lang.tooling.completion.LangCompletionResult;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;
import mmrnmhrm.core.engine.DeeEngineClient;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

public class DeeCompletionProposalComputer extends LangCompletionProposalComputer {
	
	protected DeeEngineClient dtoolclient = DeeEngineClient.getDefault();
	
	public DeeCompletionProposalComputer() {
	}
	
	@Override
	protected LangCompletionResult doComputeProposals(SourceOperationContext context, int offset,
			TimeoutProgressMonitor pm) throws CoreException, CommonException, OperationCancellation {
		
		Location editoInputFile = context.getEditorInputLocation();
		
		int timeoutMillis = pm.getTimeoutMillis();
		return dtoolclient.new CodeCompletionOperation(editoInputFile, timeoutMillis, offset)
			.runEngineOperation(pm)
			.convertToCompletionResult();
	}
	
	@Override
	public List<IContextInformation> computeContextInformation(SourceOperationContext context) {
		return super.computeContextInformation(context);
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public DeeContentAssistProposal adaptToolProposal(ToolCompletionProposal proposal) {
		Image image = getImage(proposal);
		
		return new DeeContentAssistProposal(proposal, image);
	}
	
}