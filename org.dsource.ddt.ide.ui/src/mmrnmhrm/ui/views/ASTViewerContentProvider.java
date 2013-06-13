package mmrnmhrm.ui.views;

import org.dsource.ddt.ide.core.model.DeeModuleDeclaration;

import melnorme.util.ui.jface.ElementContentProvider;
import melnorme.utilbox.tree.IElement;

public class ASTViewerContentProvider extends ElementContentProvider {
	
	ASTViewer view;
	
	public ASTViewerContentProvider(ASTViewer view) {
		this.view = view;
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		DeeModuleDeclaration deeModuleDecl = view.fDeeModule;
		if(deeModuleDecl == null) {
			return IElement.NO_ELEMENTS;
		}
		IElement input = deeModuleDecl.module; 
		return input.getChildren();
	}
	
}
