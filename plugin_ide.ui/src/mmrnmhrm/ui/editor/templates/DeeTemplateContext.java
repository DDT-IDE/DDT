package mmrnmhrm.ui.editor.templates;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.templates.TemplateContextType;

import _org.eclipse.dltk.ui.templates.ScriptTemplateContext;

public class DeeTemplateContext extends ScriptTemplateContext {

	public DeeTemplateContext(TemplateContextType type, IDocument document, int completionOffset,
			int completionLength) {
		super(type, document, completionOffset, completionLength);
	}
	
}