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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.util.List;

import melnorme.lang.ide.core.operations.TimeoutProgressMonitor;
import melnorme.lang.ide.ui.editor.actions.SourceOperationContext;
import melnorme.lang.ide.ui.text.completion.LangCompletionProposalComputer;
import melnorme.lang.ide.ui.views.LangImageProvider;
import melnorme.lang.tooling.ToolCompletionProposal;
import melnorme.lang.tooling.completion.LangCompletionResult;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;
import mmrnmhrm.core.engine_client.DToolClient;
import mmrnmhrm.core.model_elements.DefElementDescriptor;
import mmrnmhrm.ui.DeeImages;
import mmrnmhrm.ui.DeeUIPreferenceConstants.ElementIconsStyle;
import mmrnmhrm.ui.views.DeeElementImageProvider;
import mmrnmhrm.ui.views.DeeModelElementLabelProvider;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

public class DeeCompletionProposalComputer extends LangCompletionProposalComputer {
	
	protected DToolClient dtoolclient = DToolClient.getDefault();
	
	public DeeCompletionProposalComputer() {
	}
	
	@Override
	protected LangCompletionResult doComputeProposals(SourceOperationContext context, int offset,
			TimeoutProgressMonitor pm) throws CoreException, CommonException, OperationCancellation {
		
		IDocument document = context.getDocument();
		Location editoInputFile = context.getEditorInputLocation();
		
		int timeoutMillis = pm.getTimeoutMillis();
		return dtoolclient.performCompletionOperation(editoInputFile.path, offset, document.get(), timeoutMillis)
				.convertToCompletionResult();
	}
	
	@Override
	public List<IContextInformation> computeContextInformation(SourceOperationContext context) {
		return super.computeContextInformation(context);
	}
	
	@Override
	protected void handleExceptionInUI(CommonException ce) {
		if(DToolClient.compilerPathOverride == null) {
			super.handleExceptionInUI(ce);;
		} else {
			// We are in tests mode
		}
	}
	
	/* -----------------  ----------------- */
	
	protected final static char[] VAR_TRIGGER = { ' ', '=', ';' };
	
	protected final DeeModelElementLabelProvider modelElementLabelProvider = new DeeModelElementLabelProvider();
	
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
		return DeeImages.getImageDescriptorRegistry().get(imageDescriptor); 
	}
	
	@Override
	protected LangImageProvider getImageProvider() {
		throw assertFail();
	}
	
	public ImageDescriptor createImageDescriptor(ToolCompletionProposal proposal) {
		
		INamedElement namedElement = proposal.getExtraData();
		if(namedElement == null) {
			// Return no image
			return null;
		}
		
		ElementIconsStyle iconStyle = DeeElementImageProvider.getIconStylePreference();
		
		DefElementDescriptor defDescriptor = new DefElementDescriptor(namedElement);
		return DeeElementImageProvider.getDefUnitImageDescriptor(defDescriptor, iconStyle);
	}
	
}