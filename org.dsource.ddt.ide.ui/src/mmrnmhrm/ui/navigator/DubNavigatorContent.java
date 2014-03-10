package mmrnmhrm.ui.navigator;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;

import java.util.ArrayList;

import melnorme.util.swt.jface.AbstractContentProvider;
import melnorme.utilbox.misc.CollectionUtil;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.projectmodel.DubDependenciesContainer;
import mmrnmhrm.core.projectmodel.DubDependenciesContainer.DubDependencyElement;
import mmrnmhrm.core.projectmodel.DubDependenciesContainer.DubDependencySourceFolderElement;
import mmrnmhrm.core.projectmodel.DubDependenciesContainer.DubErrorElement;
import mmrnmhrm.core.projectmodel.DubDependenciesContainer.DubRawDependencyElement;
import mmrnmhrm.core.projectmodel.DubModel;
import mmrnmhrm.core.projectmodel.DubModel.DubModelUpdateEvent;
import mmrnmhrm.core.projectmodel.DubModelManager;
import mmrnmhrm.core.projectmodel.IDubElement;
import mmrnmhrm.core.projectmodel.IDubElement.DubElementType;
import mmrnmhrm.core.projectmodel.IDubModelListener;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.ModelException;
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
	
	protected IDubModelListener listener;
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		super.inputChanged(viewer, oldInput, newInput);
		
		// Remove previous listener, even though I think inputChange is only called once.
		DubModel.getDefault().removeListener(listener); 
		
		listener = new IDubModelListener() {
			@Override
			public void notifyUpdateEvent(DubModelUpdateEvent updateEvent) {
				// TODO: workaround bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=430005
				
				postRefreshEventToUI(updateEvent);
			}
		};
		DubModel.getDefault().addListener(listener);
	}
	
	@Override
	public void dispose() {
		DubModel.getDefault().removeListener(listener);
	}
	
	protected StructuredViewer getViewer() {
		return (StructuredViewer) viewer;
	}
	
	protected void postRefreshEventToUI(@SuppressWarnings("unused") DubModelUpdateEvent updateEvent) {
		final ArrayList<IProject> dubProjects = new ArrayList<>();
		for (String projectName : DubModel.getDefault().getDubProjects()) {
			IProject project = DeeCore.getWorkspaceRoot().getProject(projectName);
			dubProjects.add(project);
		}
		
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				for (IProject dubProject : dubProjects) {
					getViewer().refresh(dubProject);
				}
			}
		});
	}
	
	public static abstract class DubContentsSwitcher<RET> {
		
		public RET switchElement(Object element) {
			if(element instanceof IDubElement) {
				return visitDubElement((IDubElement) element);
			} else if(element instanceof IProject) {
				return visitProject((IProject) element);
			} if(element instanceof IModelElement && element instanceof IParent) {
				return visitModelElement((IModelElement) element, (IParent) element);
			} else {
				return visitOther(element);
			}
		}
		
		public abstract RET visitDubElement(IDubElement dubElement);
		
		public abstract RET visitProject(IProject project);
		
		public abstract RET visitModelElement(IModelElement element, IParent elementAsParent);
		
		public abstract RET visitOther(Object element);
		
	}
	
	
	@Override
	public boolean hasChildren(Object element) {
		return new DubContentsSwitcher<Boolean>() {
			@Override
			public Boolean visitProject(IProject project) {
				return project.isAccessible() && DubModel.getBundleInfo(project.getName()) != null;
			}
			@Override
			public Boolean visitDubElement(IDubElement dubElement) {
				if(dubElement.getElementType() == DubElementType.DUB_DEP_SRC_FOLDER) {
					return false; // modelElement children disabled for now. // TODO:
				}
				return dubElement.hasChildren();
			}
			@Override
			public Boolean visitModelElement(IModelElement element, IParent elementAsParent) {
				try {
					return elementAsParent.hasChildren();
				} catch (ModelException e) {
					return false;
				}
			}
			@Override
			public Boolean visitOther(Object element) {
				return false;
			}
		}.switchElement(element);
	}
	
	@Override
	public Object[] getChildren(Object parent) {
		return new DubContentsSwitcher<Object[]>() {
			@Override
			public Object[] visitProject(IProject project) {
				return getProjectChildren(project);
			}
			@Override
			public Object[] visitDubElement(IDubElement dubElement) {
				return dubElement.getChildren();
			}
			@Override
			public Object[] visitModelElement(IModelElement element, IParent elementAsParent) {
				try {
					return elementAsParent.getChildren();
				} catch (ModelException e) {
					return IDubElement.NO_CHILDREN;
				}
			}
			@Override
			public Object[] visitOther(Object element) {
				return null;
			}
		}.switchElement(parent);
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
		return new DubContentsSwitcher<Object>() {
			@Override
			public Object visitProject(IProject project) {
				return project.getParent();
			}
			@Override
			public Object visitDubElement(IDubElement dubElement) {
				return dubElement.getParent();
			}
			@Override
			public Object visitModelElement(IModelElement element, IParent elementAsParent) {
				IModelElement parent = element.getParent();
				if(parent instanceof IProjectFragment) {
					// TODO: need to map to DepSourceContainer
					return null;
				}
				return parent;
			}
			@Override
			public Object visitOther(Object element) {
				return null;
			}
		}.switchElement(element);
	}
	
	/* ----------------- specific switcher ----------------- */
	
	public static abstract class DubAllContentSwitcher<RET> extends DubContentsSwitcher<RET> {
		
		@Override
		public RET visitProject(IProject project) {
			return null;
		}
		
		@Override
		public RET visitDubElement(IDubElement element) {
			switch (element.getElementType()) {
			case DUB_DEP_CONTAINER: return visitDepContainer((DubDependenciesContainer) element);
			case DUB_RAW_DEP: return visitRawDepElement((DubRawDependencyElement) element);
			case DUB_ERROR_ELEMENT: return visitErrorElement((DubErrorElement) element);
			case DUB_RESOLVED_DEP: return visitDepElement((DubDependencyElement) element);
			case DUB_DEP_SRC_FOLDER: return visitDepSourceFolderElement((DubDependencySourceFolderElement) element);
			}
			throw assertUnreachable();
		}
		
		public abstract RET visitDepContainer(DubDependenciesContainer element);
		public abstract RET visitRawDepElement(DubRawDependencyElement element);
		public abstract RET visitErrorElement(DubErrorElement element);
		public abstract RET visitDepElement(DubDependencyElement element);
		public abstract RET visitDepSourceFolderElement(DubDependencySourceFolderElement element);
		
		@Override
		public abstract RET visitModelElement(IModelElement element, IParent elementAsParent);
		
		@Override
		public RET visitOther(Object element) {
			if(isDubManifestFile(element)) {
				return visitDubManifestFile((IResource) element);
			}
			if(isDubCacheFolder(element)) {
				return visitDubCacheFolder((IResource) element);
			}
			if(isDubSourceFolder(element)) {
				return visitDubSourceFolder((IResource) element);
			}
			return null;
		}
		
		public abstract RET visitDubManifestFile(IResource element);
		
		public abstract RET visitDubCacheFolder(IResource element);
		
		public abstract RET visitDubSourceFolder(IResource element);
		
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