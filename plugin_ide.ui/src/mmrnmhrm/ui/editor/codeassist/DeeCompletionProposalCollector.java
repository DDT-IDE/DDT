/*******************************************************************************
 * Copyright (c) 2013, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.editor.codeassist;

import melnorme.lang.tooling.symbols.INamedElement;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.engine_client.DeeCompletionOperation.RefSearchCompletionProposal;
import mmrnmhrm.core.model_elements.DeeSourceElementProvider;
import mmrnmhrm.core.model_elements.DefElementDescriptor;
import mmrnmhrm.ui.DeeImages;
import mmrnmhrm.ui.DeeUIPreferenceConstants.ElementIconsStyle;
import mmrnmhrm.ui.views.DeeElementImageProvider;
import mmrnmhrm.ui.views.DeeElementLabelProvider;
import mmrnmhrm.ui.views.DeeModelElementLabelProvider;

import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposal;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/* FIXME: need to review this code */
public class DeeCompletionProposalCollector {
	
	protected final static char[] VAR_TRIGGER = { ' ', '=', ';' };
	
	protected final DeeModelElementLabelProvider modelElementLabelProvider = new DeeModelElementLabelProvider();
	
	protected static char[] getVarTrigger() {
		return VAR_TRIGGER;
	}
	
	public IScriptCompletionProposal adaptProposal(CompletionProposal proposal) {
		RefSearchCompletionProposal refSearchProposal = (RefSearchCompletionProposal) proposal;
		INamedElement namedElement = refSearchProposal.getExtraInfo();
		
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
	
	protected Image createImage(CompletionProposal proposal) {
		ImageDescriptor imageDescriptor = createImageDescriptor(proposal);
		return DeeImages.getImageDescriptorRegistry().get(imageDescriptor); 
	}
	
	public ImageDescriptor createImageDescriptor(CompletionProposal proposal) {
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