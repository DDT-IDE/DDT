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
package mmrnmhrm.ui.editor.codeassist;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.ui.editor.actions.SourceOperationContext;
import melnorme.lang.ide.ui.text.completion.LangCompletionProposalComputer;
import melnorme.lang.tooling.ToolCompletionProposal;
import melnorme.lang.tooling.common.ops.IOperationMonitor.NullOperationMonitor;
import melnorme.lang.tooling.completion.LangCompletionResult;
import melnorme.lang.utils.concurrency.TimeoutCancelMonitor;
import melnorme.utilbox.collections.Indexable;
import melnorme.utilbox.concurrency.ICancelMonitor;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;
import mmrnmhrm.core.engine.DeeEngineClient;

public class DeeCompletionProposalComputer extends LangCompletionProposalComputer {
	
	protected DeeEngineClient dtoolclient = DeeEngineClient.getDefault();
	
	public DeeCompletionProposalComputer() {
	}
	
	@Override
	protected LangCompletionResult doComputeProposals(SourceOperationContext context, int offset,
			ICancelMonitor cm) throws CoreException, CommonException, OperationCancellation {
		
		Location editoInputFile = context.getEditorInputLocation();
		
		IProject project = context.getProject();
		String dubPath = LangCore.settings().SDK_LOCATION.getValue(project).toString();
		
		int timeoutMillis = ((TimeoutCancelMonitor) cm).getTimeoutMillis();
		return dtoolclient.new CodeCompletionOperation(editoInputFile, timeoutMillis, offset, dubPath)
			.runEngineOperation(new NullOperationMonitor(cm))
			.convertToCompletionResult();
	}
	
	@Override
	public Indexable<IContextInformation> computeContextInformation(SourceOperationContext context) {
		return super.computeContextInformation(context);
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public DeeContentAssistProposal adaptToolProposal(ToolCompletionProposal proposal) {
		Image image = getImage(proposal);
		
		return new DeeContentAssistProposal(proposal, image);
	}
	
}