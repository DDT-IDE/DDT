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

import melnorme.lang.tooling.symbols.INamedElement;
import mmrnmhrm.ui.DeeUI;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

import dtool.ddoc.TextUI;

public class DeeContentAssistProposal extends ScriptCompletionProposalExtension {
	
	public final INamedElement namedElement; 
	
	public DeeContentAssistProposal(String replacementString, int replacementOffset, int replacementLength, 
			Image image, String displayString, INamedElement namedElement,
			IContextInformation contextInformation) {
		super(replacementString, replacementOffset, replacementLength, image, displayString, contextInformation, 5);
		this.namedElement = namedElement;
	}
	
	@Override
	public String getProposalInfoString(IProgressMonitor monitor) {
		return TextUI.getDDocHTMLRender(namedElement);
	}
	
	@Override
	protected boolean isSmartTrigger(char trigger) {
		// BM: From my understanding, a smart trigger is a insertion trigger character 
		// that doesn't get added to the text
		return false;
	}
	
	@Override
	protected boolean isValidPrefix(String prefix) {
		if(isInDoc()) {
			return super.isValidPrefix(prefix);
		}
		return isPrefix(prefix, getReplacementString());
	}
	
	@Override
	protected boolean insertCompletion() {
		IPreferenceStore preference = DeeUI.getInstance().getPreferenceStore();
		return preference.getBoolean(PreferenceConstants.CODEASSIST_INSERT_COMPLETION);
	}
	
	/** A string representation of this proposal, useful for debugging purposes only. */
	@Override
	public String toString() {
		return namedElement.getName();
	}
	
}