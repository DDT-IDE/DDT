package mmrnmhrm.ui.editor.doc;

import java.io.Reader;

import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.ui.documentation.IScriptDocumentationProvider;

// TODO: DLTK: we are currently own DocProvider, not DLTK's
public class DeeDocumentationProvider implements IScriptDocumentationProvider {
	@Override
	public Reader getInfo(String content) {
		return null;
	}
	@Override
	public Reader getInfo(IMember element, boolean lookIntoParents, boolean lookIntoExternal) {
		return null;
	}
	
}
