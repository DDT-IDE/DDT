package mmrnmhrm.ui.navigator;

import java.util.ArrayList;

import melnorme.util.swt.jface.AbstractContentProvider;
import melnorme.utilbox.misc.CollectionUtil;
import mmrnmhrm.core.projectmodel.CommonDubElement;
import mmrnmhrm.core.projectmodel.DubDependenciesContainer;
import mmrnmhrm.core.projectmodel.DubModel;
import mmrnmhrm.core.projectmodel.DubModel.IDubModel;
import mmrnmhrm.core.projectmodel.IDubModelListener;
import mmrnmhrm.core.projectmodel.DubModelManager;
import mmrnmhrm.core.projectmodel.IDubElement;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonContentProvider;

import dtool.dub.DubBundleDescription;
import dtool.dub.DubManifestParser;

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
		DubModel.getDefault().addListener(new IDubModelListener() {
			@Override
			public void notifyUpdateEvent(IDubModel source, DubBundleDescription eventObject) {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						getViewer().refresh(); //TODO refresh specific element only
					}
				});
			}
		});
	}
	
	@Override
	public void dispose() {
		/*BUG here*/
		System.out.println("asdfd");
	}
	
	protected StructuredViewer getViewer() {
		return (StructuredViewer) viewer;
	}
	
	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof IProject) {
			IProject project = (IProject) element;
			return project.isAccessible() && DubModel.getBundleInfo(project.getName()) != null;
		}
		if(element instanceof CommonDubElement) {
			return ((CommonDubElement) element).hasChildren();
		}
		return false;
	}
	
	@Override
	public Object[] getChildren(Object parent) {
		if(parent instanceof IProject) {
			return getProjectChildren((IProject) parent);
		}
		if(parent instanceof CommonDubElement) {
			return ((CommonDubElement) parent).getChildren();
		}
		return null;
	}
	
	protected Object[] getProjectChildren(IProject project) {
		ArrayList<Object> arrayList = new ArrayList<>();
		if(project.isAccessible()) {
			DubDependenciesContainer dubContainer = DubModelManager.getDubContainer(project);
			if(dubContainer != null) {
				arrayList.add(dubContainer);
			}
			
			// Add project children ourselves: this is so that children will be sorted by our own sorter. 
			// (otherwise only Platform Navigator sorter will be used)
			// Navigator ResourceExtension will also add this, but they will not appear duplicated because they
			// are equal elements.
			try {
				arrayList.addAll(CollectionUtil.createArrayList(project.members()));
			} catch (CoreException e) {
				// ignore, leave empty
			}
		}
		return arrayList.toArray();
	}
	
	@Override
	public Object getParent(Object element) {
		if(element instanceof DubDependenciesContainer) {
			DubDependenciesContainer dubDependenciesContainer = (DubDependenciesContainer) element;
			return dubDependenciesContainer.getProject(); 
		}
		if(element instanceof IDubElement) {
			IDubElement dubElement = (IDubElement) element;
			return dubElement.getParent();
		}
		return null;
	}
	
	public static boolean isDubManifestFile(Object element) {
		if(element instanceof IFile) {
			IFile file = (IFile) element;
			if(file.getProjectRelativePath().equals(new Path(DubManifestParser.DUB_MANIFEST_FILENAME))) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isDubCacheFolder(Object element) {
		if(!(element instanceof IFolder)) {
			return false;
		} 
		IFolder folder = (IFolder) element;
		if(folder.getProjectRelativePath().equals(new Path(".dub"))) {
			return true;
		}
		return false;
	}
	
	public static boolean isDubSourceFolder(Object element) {
		if(!(element instanceof IFolder)) {
			return false;
		} 
		IFolder folder = (IFolder) element;
		IProject project = folder.getProject();
		DubBundleDescription bundleInfo = DubModel.getBundleInfo(project.getName());
		if(bundleInfo == null) {
			return false;
		}
		
		java.nio.file.Path[] sourceFolders = bundleInfo.getMainBundle().getEffectiveSourceFolders();
		for (java.nio.file.Path srcFolderPath : sourceFolders) {
			if(folder.getProjectRelativePath().toFile().toPath().equals(srcFolderPath)) {
				return true;
			}
		}
		return false;
	}
	
}