package mmrnmhrm.ui.editor.codeassist;

import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.ui.text.completion.CompletionProposalLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;

import mmrnmhrm.ui.views.DeeModelElementLabelProvider;

public class DeeCompletionProposalLabelProvider extends CompletionProposalLabelProvider {
	
	private DeeModelElementLabelProvider modelElementLabelProvider = new DeeModelElementLabelProvider();
	
	public DeeCompletionProposalLabelProvider() {
	}
	
	@Override
	public ImageDescriptor createImageDescriptor(CompletionProposal proposal) {
		if(proposal.getModelElement() instanceof IMember) {
			IMember member = (IMember) proposal.getModelElement();
			return modelElementLabelProvider.getImageDescriptor(member, DeeModelElementLabelProvider.SMALL_SIZE);
		}
		// Return no image, this shouldn't even happen though
		return null;
	}
	
}
