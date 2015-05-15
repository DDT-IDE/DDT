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

import melnorme.lang.ide.core.operations.TimeoutProgressMonitor;
import melnorme.lang.ide.ui.LangImageProvider;
import melnorme.lang.ide.ui.LangImages;
import melnorme.lang.ide.ui.LangUIPlugin_Actual;
import melnorme.lang.ide.ui.editor.actions.SourceOperationContext;
import melnorme.lang.ide.ui.text.completion.LangCompletionProposalComputer;
import melnorme.lang.ide.ui.views.AbstractLangImageProvider;
import melnorme.lang.ide.ui.views.StructureElementLabelProvider;
import melnorme.lang.tooling.ToolCompletionProposal;
import melnorme.lang.tooling.completion.LangCompletionResult;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;
import mmrnmhrm.core.engine.DToolClient;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

public class DeeCompletionProposalComputer extends LangCompletionProposalComputer {
	
	protected DToolClient dtoolclient = DToolClient.getDefault();
	
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
	
	protected final static char[] VAR_TRIGGER = { ' ', '=', ';' };
	
	protected static char[] getVarTrigger() {
		return VAR_TRIGGER;
	}
	
	@Override
	public DeeContentAssistProposal adaptToolProposal(ToolCompletionProposal proposal) {
		Image image = getImage(proposal);
		
		DeeContentAssistProposal completionProposal = new DeeContentAssistProposal(proposal, image);
		completionProposal.setTriggerCharacters(getVarTrigger());
		return completionProposal;
	}
	
	@Override
	protected Image getImage(ToolCompletionProposal proposal) {
		ImageDescriptor imageDescriptor = createImageDescriptor(proposal);
		return LangImages.getImageDescriptorRegistry().get(imageDescriptor); 
	}
	
	@Override
	protected AbstractLangImageProvider getImageProvider() {
		return new LangImageProvider();
	}
	
	public ImageDescriptor createImageDescriptor(ToolCompletionProposal proposal) {
		ImageDescriptor baseImage = getBaseImageDescriptor(proposal).getDescriptor();
		
		StructureElementLabelProvider labelDecorator = LangUIPlugin_Actual.getStructureElementLabelProvider();
		return labelDecorator.getElementImageDescriptor(baseImage, proposal.getAttributes());
	}
	
}