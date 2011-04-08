package mmrnmhrm.ui.editor.text;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.ui.text.completion.ProposalInfo;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposal;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

public abstract class ScriptCompletionProposalExtension extends ScriptCompletionProposal {
	
	/** The CSS used to format javadoc information. */
	private static String fgCSSStyles;
	
	
	public ScriptCompletionProposalExtension(String replacementString, int replacementOffset, int replacementLength, 
			Image image, String displayString, IContextInformation contextInformation, int relevance) {
		
		super(replacementString, replacementOffset, replacementLength, image, displayString, relevance);
		
		setContextInformation(contextInformation);
	}
	
	public ScriptCompletionProposalExtension(String replacementString, int replacementOffset, int replacementLength,
			Image image, String displayString, int relevance) {
		super(replacementString, replacementOffset, replacementLength, image, displayString, relevance);
	}
	
	public ScriptCompletionProposalExtension(String replacementString, int replacementOffset, int replacementLength,
			Image image, String displayString, int relevance, boolean isInDoc) {
		super(replacementString, replacementOffset, replacementLength, image, displayString, relevance, isInDoc);
	}
	
	
	@Override
	public String getDisplayString() {
		if (super.getDisplayString() != null)
			return super.getDisplayString();
		return getReplacementString();
	}
	
	// This actually returns a delta from replacement offset, not the actual final offset
	@Override
	public int getCursorPosition() {
		return super.getCursorPosition();
	}
	
	/* --------------------------------- */
	
	/** Returns the style information for displaying HTML (Javadoc) content. */
	@Override
	protected String getCSSStyles() {
		if (fgCSSStyles == null) {
			fgCSSStyles= HoverUtil.getDDocPreparedCSS("/JavadocHoverStyleSheet.css");
		}
		return fgCSSStyles;
	}
	
	@Override
	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
		//if (getProposalInfo() != null) {
			String info= getProposalInfoString(monitor);
			return HoverUtil.getCompleteHoverInfo(info, getCSSStyles());
		//}
	}
	
	@Override
	protected ProposalInfo getProposalInfo() {
		return super.getProposalInfo();
	}
	
	protected abstract String getProposalInfoString(IProgressMonitor monitor);
	
	
}
