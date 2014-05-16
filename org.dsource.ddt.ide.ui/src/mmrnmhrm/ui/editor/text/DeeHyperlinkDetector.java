package mmrnmhrm.ui.editor.text;

import mmrnmhrm.lang.ui.EditorUtil;
import mmrnmhrm.ui.DeeUIPlugin;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.ast.ASTNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.references.NamedReference;

public class DeeHyperlinkDetector extends AbstractHyperlinkDetector {
	
	public static final String DEE_EDITOR_TARGET = DeeUIPlugin.PLUGIN_ID + ".texteditor.deeCodeTarget";
	
	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (region == null)
			return null;

		ITextEditor textEditor= (ITextEditor)getAdapter(ITextEditor.class);
		ASTNode module = EditorUtil.parseModuleFromEditorInput(textEditor, false);
		ASTNode selNode = ASTNodeFinder.findElement(module, region.getOffset(), false);
		if(!(selNode instanceof NamedReference))
			return null;
		
		IRegion elemRegion = new Region(selNode.getOffset(), selNode.getLength());

		return new IHyperlink[] {new DeeElementHyperlink(region.getOffset(), elemRegion, textEditor)};
	}

}
