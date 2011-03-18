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
		DeeModuleDeclaration deeModule = view.fDeeModule;
		if(deeModule == null) {
			return IElement.NO_ELEMENTS;
		}
		IElement input;
		if(view.fUseOldAst == true || deeModule.neoModule == null) {
			input = deeModule.dmdModule;
		} else {
			input = deeModule.neoModule; 
		}
		
		return input.getChildren();
	}
	
}
