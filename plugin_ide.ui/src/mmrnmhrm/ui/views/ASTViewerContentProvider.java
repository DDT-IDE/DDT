package mmrnmhrm.ui.views;

import melnorme.util.swt.jface.ElementContentProvider;
import melnorme.utilbox.tree.IElement;
import dtool.parser.DeeParserResult;

public class ASTViewerContentProvider extends ElementContentProvider {
	
	protected ASTViewer view;
	
	public ASTViewerContentProvider(ASTViewer view) {
		this.view = view;
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		DeeParserResult deeModuleDecl = view.fDeeModule;
		if(deeModuleDecl == null) {
			return IElement.NO_ELEMENTS;
		}
		IElement input = deeModuleDecl.getModuleNode(); 
		return input.getChildren();
	}
	
}