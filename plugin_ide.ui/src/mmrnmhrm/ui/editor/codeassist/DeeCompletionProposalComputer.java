package mmrnmhrm.ui.editor.codeassist;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalCollector;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalComputer;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;

public class DeeCompletionProposalComputer extends ScriptCompletionProposalComputer {
	
	public DeeCompletionProposalComputer() {
	}
	
	@Override
	protected ScriptCompletionProposalCollector createCollector(ScriptContentAssistInvocationContext context) {
		return new DeeCompletionProposalCollector(context.getSourceModule());
	}
	
	@Override
	protected TemplateCompletionProcessor createTemplateProposalComputer(ScriptContentAssistInvocationContext context) {
		return new DeeTemplateCompletionProcessor(context);
	}
	
	@Override
	protected List<ICompletionProposal> computeScriptCompletionProposals(int offset,
			ScriptContentAssistInvocationContext context, IProgressMonitor monitor) {
		// FIXME need to update this to fix tests bug.
		return super.computeScriptCompletionProposals(offset, context, monitor);
	}
	
}
