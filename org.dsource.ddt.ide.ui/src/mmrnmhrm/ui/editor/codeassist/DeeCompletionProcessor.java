package mmrnmhrm.ui.editor.codeassist;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.dltk.ui.text.completion.ContentAssistInvocationContext;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProcessor;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.ui.IEditorPart;

public class DeeCompletionProcessor extends ScriptCompletionProcessor {
	
	public DeeCompletionProcessor(IEditorPart editor, ContentAssistant assistant, String partition) {
		super(editor, assistant, partition);
	}
	
	@Override
	protected String getNatureId() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return new char[] { '(' };
	}
	
	@Override
	protected ContentAssistInvocationContext createContext(ITextViewer viewer, int offset) {
		return new ScriptContentAssistInvocationContext(viewer, offset, fEditor, getNatureId());
	}
	
	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return super.getContextInformationValidator();
	}
	
//	protected CompletionProposalLabelProvider getProposalLabelProvider() {
//		return new CompletionProposalLabelProvider();
//	}
	
//	protected IPreferenceStore getPreferenceStore() {
//		return PythonCorePlugin.getDefault().getPreferenceStore();
//	}
	
}