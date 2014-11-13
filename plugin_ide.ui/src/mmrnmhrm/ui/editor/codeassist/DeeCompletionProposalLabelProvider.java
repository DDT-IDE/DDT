package mmrnmhrm.ui.editor.codeassist;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model_elements.DeeSourceElementProvider;
import mmrnmhrm.core.model_elements.DefElementDescriptor;
import mmrnmhrm.ui.DeeImages;
import mmrnmhrm.ui.DeeUIPreferenceConstants.ElementIconsStyle;
import mmrnmhrm.ui.views.DeeElementImageProvider;
import mmrnmhrm.ui.views.DeeModelElementLabelProvider;

import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.text.completion.CompletionProposalLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;

import dtool.engine.common.IDeeNamedElement;

public class DeeCompletionProposalLabelProvider extends CompletionProposalLabelProvider {
	
	protected DeeModelElementLabelProvider modelElementLabelProvider = new DeeModelElementLabelProvider();
	protected ElementIconsStyle iconStyle;
	
	public DeeCompletionProposalLabelProvider() {
	}
	
	public ElementIconsStyle getIconStylePreference() {
		// Delayed init to ensure this is run in UI thread
		if(iconStyle == null) {
			iconStyle = DeeElementImageProvider.getIconStylePreference();
		}
		return iconStyle;
	}
	
	@Override
	public ImageDescriptor createImageDescriptor(CompletionProposal proposal) {
		DefElementDescriptor defDescriptor = null;
		
		if(proposal.getExtraInfo() instanceof DefElementDescriptor) {
			defDescriptor = (DefElementDescriptor) proposal.getExtraInfo();
		}
		else if(proposal.getExtraInfo() instanceof IDeeNamedElement) {
			IDeeNamedElement defElement = (IDeeNamedElement) proposal.getExtraInfo();
			defDescriptor = new DefElementDescriptor(defElement);
		} 
		else if(proposal.getModelElement() instanceof IMember) {
			IMember member = (IMember) proposal.getModelElement();
			try {
				defDescriptor = DeeSourceElementProvider.toElementDescriptor(member);
			} catch (ModelException e) {
				DeeCore.logStatus(e);
				return DeeImages.getIDEInternalErrorImageDescriptor();
			}
		}
		
		if(defDescriptor != null) {
			return DeeElementImageProvider.getDefUnitImageDescriptor(defDescriptor, getIconStylePreference());
		}
		// Return no image
		return null;
	}
	
}