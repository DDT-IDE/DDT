/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.navigator;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import dtool.dub.BundlePath;
import dtool.dub.DubBundleDescription;
import melnorme.lang.ide.core.project_model.UpdateEvent;
import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.lang.ide.ui.navigator.NavigatorElementsSwitcher;
import melnorme.lang.ide.ui.views.AbstractNavigatorContentProvider;
import melnorme.util.swt.SWTUtil;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.workspace.DubProjectInfo;
import mmrnmhrm.core.workspace.DubWorkspaceModel;
import mmrnmhrm.core.workspace.IDubModelListener;
import mmrnmhrm.core.workspace.viewmodel.DubDepSourceFolderElement;
import mmrnmhrm.core.workspace.viewmodel.DubDependenciesContainer;
import mmrnmhrm.core.workspace.viewmodel.DubDependencyElement;
import mmrnmhrm.core.workspace.viewmodel.DubErrorElement;
import mmrnmhrm.core.workspace.viewmodel.DubRawDependencyElement;
import mmrnmhrm.core.workspace.viewmodel.IDubElement;
import mmrnmhrm.core.workspace.viewmodel.StdLibContainer;

public class DubNavigatorContentProvider extends AbstractNavigatorContentProvider {
	
	public static DubWorkspaceModel getWorkspaceModel() {
		return DeeCore.getWorkspaceModel();
	}
	
	protected IDubModelListener listener;
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		super.inputChanged(viewer, oldInput, newInput);
		
		// Remove previous listener, even though I think inputChange is only called once.
		getWorkspaceModel().removeListener(listener); 
		
		listener = new IDubModelListener() {
			@Override
			public void notifyUpdateEvent(UpdateEvent<DubProjectInfo> updateEvent) {
				// we use throttle Job as a workaround to to ensure label is updated, due to bug:
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=430005
				viewerRefreshThrottleJob.scheduleRefreshJob();
			}
		};
		getWorkspaceModel().addListener(listener);
	}
	
	@Override
	public void dispose() {
		getWorkspaceModel().removeListener(listener);
	}
	
	protected final ThrottleCodeJob viewerRefreshThrottleJob = new ThrottleCodeJob(1200) {
		@Override
		protected void runThrottledCode() {
			postRefreshEventToUI(this);
		};
	};
	
	protected void postRefreshEventToUI(final ThrottleCodeJob throttleCodeJob) {
		final ArrayList<IProject> dubProjects = new ArrayList<>();
		for (String projectName : getWorkspaceModel().getDubProjects()) {
			IProject project = EclipseUtils.getWorkspaceRoot().getProject(projectName);
			dubProjects.add(project);
		}
		
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				throttleCodeJob.markRequestFinished();
				for (IProject dubProject : dubProjects) {
					if(SWTUtil.isOkToUse(getViewer().getControl())) {
						getViewer().refresh(dubProject);
					}
				}
			}
		});
		
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected LangNavigatorSwitcher_HasChildren hasChildren_switcher() {
		return new LangNavigatorSwitcher_HasChildren() {
			
			@Override
			public Boolean visitProject(IProject project) {
				return project.isAccessible() && getWorkspaceModel().getBundleInfo(project) != null;
			}
			
			@Override
			public Boolean visitDubElement(IDubElement dubElement) {
				return dubElement.hasChildren();
			}
		};
	}
	
	@Override
	protected LangNavigatorSwitcher_GetChildren getChildren_switcher() {
		return new LangNavigatorSwitcher_GetChildren() {
			@Override
			public Object[] visitDubElement(IDubElement dubElement) {
				return dubElement.getChildren();
			}
			
			@Override
			public void addFirstProjectChildren(IProject project, ArrayList<Object> projectChildren) {
				DubProjectInfo projectInfo = getWorkspaceModel().getProjectInfo(project);
				if(projectInfo != null) {
					DubDependenciesContainer dubContainer = projectInfo.getDubContainer(project);
					projectChildren.add(dubContainer);
					projectChildren.add(new StdLibContainer(projectInfo.getCompilerInstall(), project));
				}
			}
		};
	}
	
	@Override
	protected LangNavigatorSwitcher_GetParent getParent_switcher() {
		return new LangNavigatorSwitcher_GetParent() {
			@Override
			public Object visitDubElement(IDubElement dubElement) {
				return dubElement.getParent();
			}
		};
	}
	
	/* ----------------- specific switcher ----------------- */
	
	public static interface DeeNavigatorAllElementsSwitcher<RET> extends NavigatorElementsSwitcher<RET> {
		
		@Override
		default RET visitDubElement(IDubElement element) {
			switch (element.getElementType()) {
			case DUB_DEP_CONTAINER: return visitDepContainer((DubDependenciesContainer) element);
			case DUB_STD_LIB: return visitStdLibContainer((StdLibContainer) element);
			case DUB_RAW_DEP: return visitRawDepElement((DubRawDependencyElement) element);
			case DUB_ERROR_ELEMENT: return visitErrorElement((DubErrorElement) element);
			case DUB_RESOLVED_DEP: return visitDepElement((DubDependencyElement) element);
			case DUB_DEP_SRC_FOLDER: return visitDepSourceFolderElement((DubDepSourceFolderElement) element);
			}
			throw assertUnreachable();
		}
		
		public abstract RET visitDepContainer(DubDependenciesContainer element);
		public abstract RET visitStdLibContainer(StdLibContainer element);
		public abstract RET visitRawDepElement(DubRawDependencyElement element);
		public abstract RET visitErrorElement(DubErrorElement element);
		public abstract RET visitDepElement(DubDependencyElement element);
		public abstract RET visitDepSourceFolderElement(DubDepSourceFolderElement element);
		
		@Override
		default RET visitOther(Object element) {
			if(isDubManifestFile(element)) {
				return visitDubManifestFile((IFile) element);
			}
			if(isDubCacheFolder(element)) {
				return visitDubCacheFolder((IFolder) element);
			}
			if(isDubSourceFolder(element)) {
				return visitDubSourceFolder((IFolder) element);
			}
			return null;
		}
		
		public abstract RET visitDubManifestFile(IFile element);
		
		public abstract RET visitDubCacheFolder(IFolder element);
		
		public abstract RET visitDubSourceFolder(IFolder element);
		
	}
	
	public static boolean isDubManifestFile(Object element) {
		if(element instanceof IFile) {
			IFile file = (IFile) element;
			if(file.getProjectRelativePath().equals(new Path(BundlePath.DUB_MANIFEST_FILENAME))) {
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
		DubBundleDescription bundleInfo = getWorkspaceModel().getBundleInfo(project);
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