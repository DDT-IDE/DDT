package mmrnmhrm.ui.editor.doc;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.io.Reader;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.projectmodel.DToolClient;
import mmrnmhrm.ui.editor.hover.DeeDocTextHover;

import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.documentation.IDocumentationResponse;
import org.eclipse.dltk.ui.documentation.IScriptDocumentationProvider;
import org.eclipse.dltk.ui.documentation.IScriptDocumentationProviderExtension2;
import org.eclipse.dltk.ui.documentation.TextDocumentationResponse;

/**
 * XXX: DLTK: This {@link DeeDocumentationProvider} is disabled (not used at the moment), 
 * due to a API limitation (unable to specify empty title without having DLTK provide an alternative default)
 * {@link DeeDocTextHover} is used instead.
 */
public class DeeDocumentationProvider implements IScriptDocumentationProvider, IScriptDocumentationProviderExtension2 {
	
	@Deprecated
	@Override
	public Reader getInfo(IMember element, boolean lookIntoParents, boolean lookIntoExternal) {
		return null;
	}
	
	@Deprecated
	@Override
	public Reader getInfo(String content) {
		// BM: used for keywords and some other unclear DLTK scenarios
		// BM: note: but maybe not anymore on DLTK 3.0 IScriptDocumentationProviderExtension2
		return null; 
	}
	
	@Override
	public IDocumentationResponse getDocumentationFor(Object element) {
		if(element instanceof IMember) {
			assertFail(); // DeeDocumentationProvider is disabled, should not ever find an element
			String header = getHeaderComment((IMember) element);
			return header == null ? null : new TextDocumentationResponse(element, "", convertToHTML(header)); 
		}
		return null;
	}
	
	protected String convertToHTML(String header) {
		return header;
	}
	
	protected String getHeaderComment(IMember member) {
		ISourceRange range;
		ISourceModule sourceModule = member.getSourceModule();
		try {
			ISourceModule compilationUnit = sourceModule;
			if(!compilationUnit.isConsistent()) {
				return null;
			}
			
			range = member.getNameRange();
			if(range == null)
				return null;
		} catch(ModelException e) {
			DeeCore.logError(e);
			return null;
		}
		
		final int start = range.getOffset();
		
		return DToolClient.getDDocHTMLView(sourceModule, start);
	}
	
}