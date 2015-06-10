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

import melnorme.lang.tooling.ToolCompletionProposal;
import melnorme.lang.tooling.symbols.INamedElement;
import mmrnmhrm.ui.DeeUI;

import org.eclipse.core.runtime.IProgressMonitor;
import _org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Image;

import _org.eclipse.dltk.ui.text.completion.AbstractScriptCompletionProposal;
import dtool.ddoc.TextUI;

public class DeeContentAssistProposal extends AbstractScriptCompletionProposal {
	
	public final INamedElement namedElement; 
	
	public DeeContentAssistProposal(ToolCompletionProposal proposal, Image image) {
		super(proposal, image, null);
		this.namedElement = proposal.getExtraData();
	}
	
	@Override
	public String getProposalInfoString(IProgressMonitor monitor) {
		return TextUI.getDDocHTMLRender(namedElement); /* FIXME: remove dependency on namedElement */
	}
	
	@Override
	protected boolean insertCompletion() {
		IPreferenceStore preference = DeeUI.getInstance().getPreferenceStore();
		return preference.getBoolean(PreferenceConstants.CODEASSIST_INSERT_COMPLETION);
	}
	
}