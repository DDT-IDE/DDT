package mmrnmhrm.ui.navigator;

import melnorme.util.swt.SWTUtil;
import melnorme.utilbox.tree.IElement;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

// XXX: Need to take another look at this class and fix it
public class DeeNavigatorContentProvider implements ITreeContentProvider, org.eclipse.dltk.core.IElementChangedListener {
	
	private Viewer viewer;
	
	public DeeNavigatorContentProvider() {
		//DLTKCore.addElementChangedListener(this);
	}
	
	@Override
	public Object[] getChildren(Object element) {
//		if(element instanceof IResource) {
//			element = DLTKCore.create((IResource) element); 
//		}
//		
//		if(element instanceof IParent) {
//			IParent modelElement = (IParent) element;
//			return getChildren(modelElement);
//		} 
		return IElement.NO_ELEMENTS;
	}
	
	private Object[] getChildren(IParent modelElement) {
		try {
			return modelElement.getChildren();
		} catch (ModelException e) {
			return IElement.NO_ELEMENTS;
		}
	}
	
	@Override
	public Object getParent(Object element) {
		if(element instanceof IElement) {
			return ((IElement) element).getParent();
		}
		if(element instanceof IResource) {
			return ((IResource) element).getParent();
		}
		return null;
	}
	
	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof IElement) {
			return ((IElement) element).hasChildren();
		}
		if(element instanceof IContainer) {
			try {
				return ((IContainer) element).members().length != 0;
			} catch (CoreException e) {
				return false;
			}
		}
		return false;
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}
	
	@Override
	public void dispose() {
	}
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
	}
	
	@Override
	public void elementChanged(org.eclipse.dltk.core.ElementChangedEvent event) {
		SWTUtil.runInSWTThread(new Runnable() {
			@Override
			public void run() {
				viewer.refresh();
			}
		});
	}

}
