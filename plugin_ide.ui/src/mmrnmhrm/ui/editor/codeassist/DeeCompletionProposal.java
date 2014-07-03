package mmrnmhrm.ui.editor.codeassist;

import mmrnmhrm.ui.DeeUI;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

import dtool.ast.definitions.INamedElement;
import dtool.ddoc.TextUI;

public class DeeCompletionProposal extends ScriptCompletionProposalExtension {
	
	public final INamedElement namedElement; 
	
	public DeeCompletionProposal(String replacementString, int replacementOffset, int replacementLength, 
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