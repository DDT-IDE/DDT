package mmrnmhrm.ui.editor.codeassist;

import mmrnmhrm.ui.editor.templates.DeeUniversalTemplateContextType;
import mmrnmhrm.ui.editor.templates.DeeTemplateAccess;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;

import _org.eclipse.dltk.ui.templates.ScriptTemplateCompletionProcessor;

public class DeeTemplateCompletionProcessor extends ScriptTemplateCompletionProcessor {
	
	private static char[] IGNORE = { '.' };
	
	public DeeTemplateCompletionProcessor(ScriptContentAssistInvocationContext context) {
		super(context);
	}
	
	@Override
	protected String getContextTypeId() {
		return DeeUniversalTemplateContextType.CONTEXT_TYPE_ID;
	}
	
	@Override
	protected char[] getIgnore() {
		return IGNORE;
	}
	
	@Override
	protected ScriptTemplateAccess getTemplateAccess() {
		return DeeTemplateAccess.getInstance();
	}
	
}