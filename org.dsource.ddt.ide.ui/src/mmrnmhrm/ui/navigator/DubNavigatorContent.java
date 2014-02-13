package mmrnmhrm.ui.navigator;

import static melnorme.utilbox.core.CoreUtil.array;
import melnorme.util.swt.jface.AbstractContentProvider;
import mmrnmhrm.core.projectmodel.CommonDubElement;
import mmrnmhrm.core.projectmodel.DubDependenciesContainer;
import mmrnmhrm.core.projectmodel.DubModelManager;
import mmrnmhrm.core.projectmodel.IDubProjectModelListener;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonContentProvider;

import dtool.dub.DubBundleDescription;

public class DubNavigatorContent extends AbstractContentProvider implements ICommonContentProvider {
	
	@Override
	public void saveState(IMemento aMemento) {
	}
	
	@Override
	public void restoreState(IMemento aMemento) {
	}

	@Override
	public void init(ICommonContentExtensionSite aConfig) {
	}
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		super.inputChanged(viewer, oldInput, newInput);
		DubModelManager.getDefault().addListener(new IDubProjectModelListener() {
			
			@Override
			public void notifyUpdateEvent(DubModelManager source, DubBundleDescription eventObject) {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						getViewer().refresh(); //TODO refresh specific element only
					}
				});
			}
		});
	}
	
	protected StructuredViewer getViewer() {
		return (StructuredViewer) viewer;
	}
	
	@Override
	public boolean hasChildren(Object element) {
		if(isDeeProject(element)) {
			return true;
		}
		if(element instanceof CommonDubElement) {
			return ((CommonDubElement) element).hasChildren();
		}
		return false;
	}
	
	@Override
	public Object[] getChildren(Object parent) {
		if(isDeeProject(parent)) {
			IProject project = (IProject) parent;
			DubDependenciesContainer dubContainer = DubModelManager.getDubContainer(project);
			return array(dubContainer);
		}
		if(parent instanceof CommonDubElement) {
			return ((CommonDubElement) parent).getChildren();
		}
		return null;
	}
	
	protected boolean isDeeProject(Object parent) {
		return parent instanceof IProject;
	}
	
	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}
	
}