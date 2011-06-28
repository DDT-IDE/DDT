package mmrnmhrm.ui.editor.doc;

import java.io.Reader;
import java.io.StringReader;

import mmrnmhrm.ui.editor.hover.DeeDocTextHover;

import org.dsource.ddt.ide.core.model.DeeModelUtil;
import org.dsource.ddt.ide.core.model.DeeModuleDeclaration;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.documentation.IScriptDocumentationProvider;

import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeFinder;

/**
 * It is preferable that this documentation provider is not used as it has a more limited API.
 * Preferable to use {@link DeeDocTextHover} whenever possible.
 */
public class DeeDocumentationProvider implements IScriptDocumentationProvider {
	
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
	
	@Override
	public Reader getInfo(IMember member, boolean lookIntoParents, boolean lookIntoExternal) {
		String header = getHeaderComment(member);
		return (header == null) ? null : new StringReader(convertToHTML(header));
	}
	
	protected String convertToHTML(String header) {
		return header;
	}
	
	@Override
	public Reader getInfo(String content) {
		// BM: used for keywords and some other unclear DLTK scenarios
		return null; 
	}
	
}
