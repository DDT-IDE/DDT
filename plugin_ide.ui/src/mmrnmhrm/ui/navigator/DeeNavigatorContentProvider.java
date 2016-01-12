/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.navigator;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;

import melnorme.lang.ide.core.BundleInfo;
import melnorme.lang.ide.core.LangCore_Actual;
import melnorme.lang.ide.core.project_model.view.DependenciesContainer;
import melnorme.lang.ide.ui.navigator.NavigatorElementsSwitcher;
import melnorme.lang.ide.ui.views.AbstractNavigatorContentProvider;
import melnorme.utilbox.collections.ArrayList2;
import mmrnmhrm.core.workspace.viewmodel.StdLibContainer;

public class DeeNavigatorContentProvider extends AbstractNavigatorContentProvider {
	
	@Override
	protected LangNavigatorSwitcher_HasChildren hasChildren_switcher() {
		return new LangNavigatorSwitcher_HasChildren() {
		};
	}
	
	@Override
	protected LangNavigatorSwitcher_GetChildren getChildren_switcher() {
		return new LangNavigatorSwitcher_GetChildren() {
			@Override
			public void addFirstProjectChildren(IProject project, ArrayList2<Object> projectChildren) {
				BundleInfo projectInfo = LangCore_Actual.getBundleModel().getProjectInfo(project);
				if(projectInfo != null) {
					DependenciesContainer dubContainer = projectInfo.getDubContainer(project);
					projectChildren.add(dubContainer);
					projectChildren.add(new StdLibContainer(projectInfo.getCompilerInstall(), project));
				}
			}
		};
	}
	
	@Override
	protected LangNavigatorSwitcher_GetParent getParent_switcher() {
		return new LangNavigatorSwitcher_GetParent() {
		};
	}
	
	/* ----------------- specific switcher ----------------- */
	
	public static interface DeeNavigatorAllElementsSwitcher<RET> extends NavigatorElementsSwitcher<RET> {
		
		@Override
		default RET visitFolder(IFolder folder) {
			if(isDubCacheFolder(folder)) {
				return visitDubCacheFolder(folder);
			}
			if(isDubSourceFolder(folder)) {
				return visitDubSourceFolder(folder);
			}
			return null;
		}
		
		public abstract RET visitDubCacheFolder(IFolder element);
		
		public abstract RET visitDubSourceFolder(IFolder element);
		
	}
	
	public static boolean isDubCacheFolder(IFolder folder) {
		if(folder.getProjectRelativePath().equals(new Path(".dub"))) {
			return true;
		}
		return false;
	}
	
	public static boolean isDubSourceFolder(IFolder folder) {
		IProject project = folder.getProject();
		BundleInfo projectInfo = LangCore_Actual.getBundleModel().getProjectInfo(project);
		if(projectInfo == null) {
			return false;
		}
		
		java.nio.file.Path[] sourceFolders = projectInfo.getMainBundle().getEffectiveSourceFolders();
		for (java.nio.file.Path srcFolderPath : sourceFolders) {
			if(folder.getProjectRelativePath().toFile().toPath().equals(srcFolderPath)) {
				return true;
			}
		}
		return false;
	}
	
}