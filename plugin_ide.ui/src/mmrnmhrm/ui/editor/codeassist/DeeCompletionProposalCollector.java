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
import mmrnmhrm.core.engine_client.DeeCompletionOperation.RefSearchCompletionProposal;
import mmrnmhrm.ui.DeeImages;
import mmrnmhrm.ui.views.DeeElementLabelProvider;

import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposal;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public class DeeCompletionProposalCollector {
	
	protected final static char[] VAR_TRIGGER = { ' ', '=', ';' };
	
	protected final DeeCompletionProposalLabelProvider labelProvider = new DeeCompletionProposalLabelProvider();
	
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
		ImageDescriptor imageDescriptor = labelProvider.createImageDescriptor(proposal);
		return DeeImages.getImageDescriptorRegistry().get(imageDescriptor); 
	}
	
}