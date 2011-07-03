package mmrnmhrm.ui.editor.codeassist;

import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.jface.resource.ImageDescriptor;

import mmrnmhrm.ui.views.DeeModelElementLabelProvider;

public class CompletionProposalLabelProvider extends
		org.eclipse.dltk.ui.text.completion.CompletionProposalLabelProvider {
	
	private DeeModelElementLabelProvider modelElementLabelProvider;
	
	public CompletionProposalLabelProvider() {
		modelElementLabelProvider = new DeeModelElementLabelProvider();
	}
	
	@Override
	public ImageDescriptor createImageDescriptor(CompletionProposal proposal) {
		switch (proposal.getKind()) {
		case CompletionProposal.METHOD_DECLARATION:
		case CompletionProposal.METHOD_NAME_REFERENCE:
		case CompletionProposal.METHOD_REF:
		case CompletionProposal.POTENTIAL_METHOD_DECLARATION:
		case CompletionProposal.TYPE_REF:
		case CompletionProposal.FIELD_REF:
		case CompletionProposal.LOCAL_VARIABLE_REF:
		case CompletionProposal.VARIABLE_DECLARATION:
			return modelElementLabelProvider.getImageDescriptor(proposal.getModelElement(), DeeModelElementLabelProvider.SMALL_SIZE);
		default:
			return super.createImageDescriptor(proposal);
		}
	}
	
}
