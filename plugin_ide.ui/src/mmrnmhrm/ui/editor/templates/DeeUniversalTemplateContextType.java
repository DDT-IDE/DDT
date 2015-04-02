package mmrnmhrm.ui.editor.templates;

import org.eclipse.jface.text.IDocument;

import _org.eclipse.dltk.ui.templates.ScriptTemplateContext;
import _org.eclipse.dltk.ui.templates.ScriptTemplateContextType;

public class DeeUniversalTemplateContextType extends ScriptTemplateContextType {
	
	public static final String CONTEXT_TYPE_ID = "DeeUniversalTemplateContextType";
	
	public DeeUniversalTemplateContextType() {
	}
	
	public DeeUniversalTemplateContextType(String id, String name) {
		super(id, name);
	}
	
	public DeeUniversalTemplateContextType(String id) {
		super(id);
	}
	
	
	@Override
	public ScriptTemplateContext createContext(IDocument document, int completionPosition, int length) {
		return new DeeTemplateContext(this, document, completionPosition, length);
	}
	
}
