package mmrnmhrm.ui.editor.templates;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.templates.ScriptTemplateContext;
import org.eclipse.dltk.ui.templates.ScriptTemplateContextType;
import org.eclipse.jface.text.IDocument;

public class DeeUniversalTemplateContextType extends ScriptTemplateContextType {

	public static final String CONTEXT_TYPE_ID = "DeeUniversalTemplateContextType";
	
	@Override
	public ScriptTemplateContext createContext(IDocument document, int completionPosition,
			int length, ISourceModule sourceModule) {
		return new DeeTemplateContext(this, document, completionPosition, length, sourceModule);
	}

}
