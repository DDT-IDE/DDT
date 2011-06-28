package mmrnmhrm.ui.editor.doc;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.io.Reader;

import mmrnmhrm.ui.editor.hover.DeeDocTextHover;

import org.dsource.ddt.ide.core.model.DeeModelUtil;
import org.dsource.ddt.ide.core.model.DeeModuleDeclaration;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.documentation.IDocumentationResponse;
import org.eclipse.dltk.ui.documentation.IScriptDocumentationProvider;
import org.eclipse.dltk.ui.documentation.IScriptDocumentationProviderExtension2;
import org.eclipse.dltk.ui.documentation.TextDocumentationResponse;

import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeFinder;

/**
 * It is preferable that this documentation provider is not used as it has a more limited API.
 * Preferable to use {@link DeeDocTextHover} whenever possible.
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
			String header = getHeaderComment((IMember) element);
			return header == null ? null : new TextDocumentationResponse(element, null, convertToHTML(header)); 
		}
		return null;
	}
	
	protected String convertToHTML(String header) {
		assertFail();
		return header;
	}
	
	protected String getHeaderComment(IMember member) {
		ISourceRange range;
		try {
			ISourceModule compilationUnit = member.getSourceModule();
			if(!compilationUnit.isConsistent()) {
				return null;
			}
			
			range = member.getNameRange();
			if(range == null)
				return null;
		} catch(ModelException e) {
			return null;
		}
		
		final int start = range.getOffset();
		
		DeeModuleDeclaration deeModule = DeeModelUtil.getParsedDeeModule(member.getSourceModule());
		ASTNeoNode pickedNode = ASTNodeFinder.findNeoElement(deeModule.neoModule, start, false);
		
		return DeeDocTextHover.getDocInfoForNode(pickedNode);
	}
	
}
