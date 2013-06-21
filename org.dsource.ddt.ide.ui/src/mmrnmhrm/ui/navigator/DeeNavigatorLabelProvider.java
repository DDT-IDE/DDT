package mmrnmhrm.ui.navigator;

import melnorme.utilbox.tree.IElement;
import mmrnmhrm.ui.views.DeeElementImageProvider;

import org.eclipse.core.resources.IFolder;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.ui.ModelElementLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import dtool.ast.IASTNode;

public class DeeNavigatorLabelProvider extends ModelElementLabelProvider {

	public DeeNavigatorLabelProvider() {
		super(ModelElementLabelProvider.SHOW_DEFAULT
				| ModelElementLabelProvider.SHOW_QUALIFIED 
				| ModelElementLabelProvider.SHOW_ROOT);
	}
	
	@Override
	public Image getImage(Object element) {
		if(element instanceof IElement) 
			return DeeElementImageProvider.getElementImage((IElement) element);

		if(element instanceof IModelElement || true) 
			return super.getImage(element);

		if(element instanceof IFolder) {
			IFolder folder = (IFolder) element;
			IScriptProject deeproj = DLTKCore.create(folder.getProject());
			if(deeproj == null)
				return null;
			
			//IDeeSourceRoot spentry = null;
			/*try {
				spentry = deeproj.getSourceRoot(folder);
			} catch (CoreException e) {
			}
			
			if(spentry instanceof DeeSourceFolder)
				return getImage(DeePluginImages.ELEM_SOURCEFOLDER);
			*/
			return null;
				
		} else 
			return null;
	}

	@Override
	public String getText(Object element) {
		if(element instanceof IASTNode) {
			return ((IASTNode) element).toString();
		} 
		return super.getText(element);
	}
	
	@Override
	public boolean isLabelProperty(Object element, String property) {
		// Auto-generated method stub
		return false;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}
}
