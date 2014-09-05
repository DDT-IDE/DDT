package mmrnmhrm.ui.editor.codeassist;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import mmrnmhrm.core.engine_client.DeeCompletionEngine.RefSearchCompletionProposal;
import mmrnmhrm.ui.DeeImages;
import mmrnmhrm.ui.views.DeeElementLabelProvider;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposal;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposal;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalCollector;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import dtool.ast.definitions.INamedElement;

public class DeeCompletionProposalCollector extends ScriptCompletionProposalCollector {
	
	protected final static char[] VAR_TRIGGER = { ' ', '=', ';' };
	
	@Override
	protected String getNatureId() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	protected char[] getVarTrigger() {
		return VAR_TRIGGER;
	}
	
	public DeeCompletionProposalCollector(ISourceModule module) {
		super(module);
	}
	
	@Override
	public void accept(CompletionProposal proposal) {
		super.accept(proposal);
	}
	
	
	// Most of ScriptCompletionProposalCollector functionality is overridden here
	@Override
	protected IScriptCompletionProposal createScriptCompletionProposal(CompletionProposal proposal) {
		
		if(proposal instanceof RefSearchCompletionProposal) {
			RefSearchCompletionProposal refSearchProposal = (RefSearchCompletionProposal) proposal;
			INamedElement namedElement = refSearchProposal.getExtraInfo();
			
			String completion = proposal.getCompletion();
			int repStart = proposal.getReplaceStart();
			int repLength = proposal.getReplaceEnd() - proposal.getReplaceStart();
			Image image = createImage(proposal);
			
			String displayString = refSearchProposal.isModuleImportCompletion() ?
				namedElement.getModuleFullyQualifiedName() :
				DeeElementLabelProvider.getLabelForContentAssistPopup(namedElement);
			
			DeeCompletionProposal completionProposal = new DeeCompletionProposal(completion, repStart, repLength,
					image, displayString, namedElement, null);
			completionProposal.setTriggerCharacters(getVarTrigger());
			return completionProposal;
			
		} else {
			return super.createScriptCompletionProposal(proposal);
		}
	}
	
	protected Image createImage(CompletionProposal proposal) {
		ImageDescriptor imageDescriptor = getLabelProvider().createImageDescriptor(proposal);
		return DeeImages.getImageDescriptorRegistry().get(imageDescriptor); 
	}
	
	@Override
	protected ScriptCompletionProposal createScriptCompletionProposal(String completion, int replaceStart, int length,
			Image image, String displayString, int i) {
		throw assertFail();
//		return new DeeCompletionProposal(completion, replaceStart, length, image, displayString, i);
	}
	
	@Override
	protected ScriptCompletionProposal createScriptCompletionProposal(String completion, int replaceStart, int length,
			Image image, String displayString, int i, boolean isInDoc) {
		throw assertFail();
//		return new DeeCompletionProposal(completion, replaceStart, length, image, displayString, i, isInDoc);
	}
	
	@Override
	protected ScriptCompletionProposal createOverrideCompletionProposal(
			IScriptProject scriptProject, ISourceModule compilationUnit,
			String name, String[] paramTypes, int start, int length,
			String displayName, String completionProposal) {
		throw assertFail();
//		return new ExamplePythonOverrideCompletionProposal(scriptProject, compilationUnit,
//				name, paramTypes, start, length, displayName, completionProposal);
	}
	
}