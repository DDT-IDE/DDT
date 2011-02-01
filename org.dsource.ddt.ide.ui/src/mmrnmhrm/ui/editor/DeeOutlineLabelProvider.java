package mmrnmhrm.ui.editor;

import melnorme.util.ui.jface.SimpleLabelProvider;
import melnorme.utilbox.tree.IElement;
import mmrnmhrm.ui.views.DeeElementImageProvider;

import org.eclipse.swt.graphics.Image;

import dtool.ast.ASTNeoNode;


public class DeeOutlineLabelProvider extends SimpleLabelProvider {
	
	@Override
	public Image getImage(Object element) {
		return DeeElementImageProvider.getElementImage((IElement) element);
	}
	
	@Override
	public String getText(Object elem) {
		return ((ASTNeoNode) elem).toStringAsElement();
	}
}