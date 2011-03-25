package mmrnmhrm.ui.editor.codeassist;

import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalCollector;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalComputer;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
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
		return null;
	}
}
