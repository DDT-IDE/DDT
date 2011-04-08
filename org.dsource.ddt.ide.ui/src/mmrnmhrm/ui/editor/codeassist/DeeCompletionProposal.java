package mmrnmhrm.ui.editor.codeassist;

import mmrnmhrm.ui.editor.text.HoverUtil;
import mmrnmhrm.ui.editor.text.ScriptCompletionProposalExtension;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

import dtool.ast.definitions.DefUnit;

public class DeeCompletionProposal extends ScriptCompletionProposalExtension {
	
	
	public final DefUnit defUnit;
	
	public DeeCompletionProposal(String replacementString, int replacementOffset, int replacementLength, 
			Image image, String displayString, DefUnit defUnit,
			IContextInformation contextInformation) {
		super(replacementString, replacementOffset, replacementLength, image, displayString, contextInformation, 5);
		this.defUnit = defUnit;
	}
	
	@Override
	public String getProposalInfoString(IProgressMonitor monitor) {
		return HoverUtil.getDefUnitHoverInfoWithDeeDoc(defUnit);
	}
	
	@Override
	protected boolean isSmartTrigger(char trigger) {
		// BM what is this exactly?
		if(trigger == '.') {
			return true;
		}
		return false;
	}
	
	
	@Override
	protected boolean insertCompletion() {
//		IPreferenceStore preference = DeeCorePlugin.getDefault().getPreferenceStore();
//		return preference.getBoolean(PreferenceConstants.CODEASSIST_INSERT_COMPLETION);
		return false; //do a replace completion
	}
	
	/** A string representation of this proposal, useful for debugging purposes only. */
	@Override
	public String toString() {
		return defUnit.getName();
	}
	
}
