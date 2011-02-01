package mmrnmhrm.ui.editor.text;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

import dtool.ast.definitions.DefUnit;

public class DeeCompletionProposal extends AbstractCompletionProposal implements
		ICompletionProposalExtension
		, ICompletionProposalExtension5
{
	
	
	public final DefUnit defUnit;
	
	/**
	 * Creates a new completion proposal. All fields are initialized based on the provided information.
	 *
	 * @param replacementString the actual string to be inserted into the document
	 * @param replacementOffset the offset of the text to be replaced
	 * @param replacementLength the length of the text to be replaced
	 * @param cursorPosition the position of the cursor following the insert relative to replacementOffset
	 * @param image the image to display for this proposal
	 * @param displayString the string to be displayed for the proposal
	 * @param contextInformation the context information associated with this proposal
	 * @param additionalProposalInfo the additional information associated with this proposal
	 */
	public DeeCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, int cursorPosition,
			Image image, String displayString,
			DefUnit defUnit,
			IContextInformation contextInformation) {
		super(replacementString, replacementOffset, replacementLength,
				cursorPosition, image, displayString, contextInformation);
		this.defUnit = defUnit;
	}
	
	@Override
	public String getProposalInfoString(IProgressMonitor monitor) {
		return HoverUtil.getDefUnitHoverInfoWithDeeDoc(defUnit);
	}
	
	/** A string representation of this proposal, useful for debugging purposes only. */
	@Override
	public String toString() {
		return defUnit.getName();
	}
	
}
