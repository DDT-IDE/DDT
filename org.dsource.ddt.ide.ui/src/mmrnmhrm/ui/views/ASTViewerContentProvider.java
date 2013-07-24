package mmrnmhrm.ui.views;

import melnorme.util.ui.jface.ElementContentProvider;
import melnorme.utilbox.tree.IElement;
import mmrnmhrm.core.parser.DeeModuleDeclaration;

public class ASTViewerContentProvider extends ElementContentProvider {
	
	protected ASTViewer view;
	
	public ASTViewerContentProvider(ASTViewer view) {
		this.view = view;
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		DeeModuleDeclaration deeModuleDecl = view.fDeeModule;
		if(deeModuleDecl == null) {
			return IElement.NO_ELEMENTS;
		}
		IElement input = deeModuleDecl.getModule(); 
		return input.getChildren();
	}
	
}
