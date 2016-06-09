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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.swt.graphics.Image;

import _org.eclipse.dltk.ui.text.completion.AbstractScriptCompletionProposal;
import dtool.ddoc.TextUI;
import melnorme.lang.ide.ui.text.AbstractSimpleLangSourceViewerConfiguration;
import melnorme.lang.ide.ui.text.DocumentationHoverCreator;
import melnorme.lang.tooling.ToolCompletionProposal;
import melnorme.lang.tooling.common.ISourceBuffer;
import melnorme.lang.tooling.symbols.INamedElement;

public class DeeContentAssistProposal extends AbstractScriptCompletionProposal {
	
	public final INamedElement namedElement; 
	
	public DeeContentAssistProposal(ISourceBuffer sourceBuffer, ToolCompletionProposal proposal, Image image) {
		super(sourceBuffer, proposal, image, null);
		this.namedElement = proposal.getExtraData();
	}
	
	@Override
	public String getProposalInfoString(IProgressMonitor monitor) {
		return TextUI.getDDocHTMLRender(namedElement); /* TODO: remove dependency on namedElement */
	}
	
	@Override
	public IInformationControlCreator getInformationControlCreator() {
		if(informationControlCreator == null) {
			String statusFieldText = AbstractSimpleLangSourceViewerConfiguration.getAdditionalInfoAffordanceString();
			informationControlCreator = new DocumentationHoverCreator().getHoverControlCreator(statusFieldText);
		}
		return informationControlCreator;
	}
	
}