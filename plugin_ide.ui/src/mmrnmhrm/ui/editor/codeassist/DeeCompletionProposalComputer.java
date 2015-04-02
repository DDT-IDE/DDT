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

import java.nio.file.Path;
import java.util.List;

import melnorme.lang.ide.ui.text.completion.LangCompletionProposalComputer;
import melnorme.lang.ide.ui.text.completion.LangContentAssistInvocationContext;
import melnorme.lang.ide.ui.utils.UIOperationExceptionHandler;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.engine_client.DToolClient;
import mmrnmhrm.core.engine_client.DeeCompletionOperation;
import mmrnmhrm.core.engine_client.DeeCompletionOperation.RefSearchCompletionProposal;
import mmrnmhrm.core.model_elements.DeeSourceElementProvider;
import mmrnmhrm.core.model_elements.DefElementDescriptor;
import mmrnmhrm.ui.DeeImages;
import mmrnmhrm.ui.DeeUIPreferenceConstants.ElementIconsStyle;
import mmrnmhrm.ui.views.DeeElementImageProvider;
import mmrnmhrm.ui.views.DeeElementLabelProvider;
import mmrnmhrm.ui.views.DeeModelElementLabelProvider;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

public class DeeCompletionProposalComputer extends LangCompletionProposalComputer {
	
	protected DToolClient dtoolclient = DToolClient.getDefault();
	
	public DeeCompletionProposalComputer() {
	}
	
	@Override
	protected List<ICompletionProposal> doComputeCompletionProposals(int offset, Path filePath, 
			IDocument document) throws CoreException {
		
		ArrayList2<RefSearchCompletionProposal> proposals = new DeeCompletionOperation(DToolClient.getDefault()).
				execute(filePath, offset, document.get(), 5000);
		
		ArrayList2<ICompletionProposal> result = new ArrayList2<ICompletionProposal>();
		
		for (RefSearchCompletionProposal proposal : proposals) {
			result.add(adaptProposal(proposal));
		}
		
		return result;
	}
	
	@Override
	public List<IContextInformation> computeContextInformation(LangContentAssistInvocationContext context) {
		return super.computeContextInformation(context);
	}
	
	@Override
	protected void handleExceptionInUI(CoreException ce) {
		if(DeeCompletionOperation.compilerPathOverride == null) {
			UIOperationExceptionHandler.handleOperationStatus("Content Assist", ce);
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
	
	public DeeCompletionProposal adaptProposal(RefSearchCompletionProposal proposal) {
		INamedElement namedElement = proposal.getExtraInfo();
		
		String completion = proposal.getCompletion();
		int repStart = proposal.getReplaceStart();
		int repLength = proposal.getReplaceEnd() - proposal.getReplaceStart();
		Image image = createImage(proposal);
		
		String displayString = DeeElementLabelProvider.getLabelForContentAssistPopup(namedElement);
		
		DeeCompletionProposal completionProposal = new DeeCompletionProposal(completion, repStart, repLength,
				image, displayString, namedElement, null);
		completionProposal.setTriggerCharacters(getVarTrigger());
		return completionProposal;
	}
	
	protected Image createImage(RefSearchCompletionProposal proposal) {
		ImageDescriptor imageDescriptor = createImageDescriptor(proposal);
		return DeeImages.getImageDescriptorRegistry().get(imageDescriptor); 
	}
	
	public ImageDescriptor createImageDescriptor(RefSearchCompletionProposal proposal) {
		ElementIconsStyle iconStyle = DeeElementImageProvider.getIconStylePreference();
		
		DefElementDescriptor defDescriptor = null;
		
		if(proposal.getExtraInfo() instanceof DefElementDescriptor) {
			defDescriptor = (DefElementDescriptor) proposal.getExtraInfo();
		}
		else if(proposal.getExtraInfo() instanceof INamedElement) {
			INamedElement defElement = (INamedElement) proposal.getExtraInfo();
			defDescriptor = new DefElementDescriptor(defElement);
		} 
		else if(proposal.getModelElement() instanceof IMember) {
			IMember member = (IMember) proposal.getModelElement();
			try {
				defDescriptor = DeeSourceElementProvider.toElementDescriptor(member);
			} catch (ModelException e) {
				DeeCore.logStatus(e);
				return DeeImages.getIDEInternalErrorImageDescriptor();
			}
		}
		
		if(defDescriptor != null) {
			return DeeElementImageProvider.getDefUnitImageDescriptor(defDescriptor, iconStyle);
		}
		// Return no image
		return null;
	}
	
}