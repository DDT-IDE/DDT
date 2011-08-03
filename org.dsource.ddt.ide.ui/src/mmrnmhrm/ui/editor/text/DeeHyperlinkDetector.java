package mmrnmhrm.ui.editor.text;

import mmrnmhrm.lang.ui.EditorUtil;
import mmrnmhrm.ui.DeePlugin;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.references.Reference;

public class DeeHyperlinkDetector extends AbstractHyperlinkDetector {
	
	public static final String DEE_EDITOR_TARGET = DeePlugin.EXTENSIONS_IDPREFIX+"texteditor.deeCodeTarget";
	
	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (region == null)
			return null;

		ITextEditor textEditor= (ITextEditor)getAdapter(ITextEditor.class);
		ASTNeoNode module = EditorUtil.getNeoModuleFromEditor(textEditor);
		ASTNeoNode selNode = ASTNodeFinder.findElement(module, region.getOffset(), false);
		if(!(selNode instanceof Reference))
			return null;
		
		IRegion elemRegion = new Region(selNode.getOffset(), selNode.getLength());

		return new IHyperlink[] {new DeeElementHyperlink(region.getOffset(), elemRegion, textEditor)};
	}

}
