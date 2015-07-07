/*******************************************************************************
 * Copyright (c) 2014, 2014 IBM Corporation and other contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.navigator;

import static melnorme.utilbox.core.CoreUtil.array;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.navigator.ICommonMenuConstants;

import melnorme.lang.ide.ui.navigator.LangNavigatorActionProvider;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCoreMessages;
import mmrnmhrm.core.engine.DeeToolManager;
import mmrnmhrm.core.workspace.viewmodel.DubDependenciesContainer;
import mmrnmhrm.ui.DeeImages;
import mmrnmhrm.ui.DeeUIMessages;

public class DeeNavigatorActionProvider extends LangNavigatorActionProvider {
	
	@Override
	protected void initActionGroups(IViewPart viewPart) {
		super.initActionGroups(viewPart);
		actionGroups.add(new DubPathActionGroup(viewPart));
	}
	
	public static class DubPathActionGroup extends ViewPartActionGroup {
		
		protected final AddDubProjectToLocalPath action1 = new AddDubProjectToLocalPath();
		protected final RemoveDubProjectFromLocalPath action2 = new RemoveDubProjectFromLocalPath();
		protected final RunDubList action3 = new RunDubList();
		
		public DubPathActionGroup(IViewPart viewPart) {
			super(viewPart);
		}
		
		@Override
		public void fillContextMenu(IMenuManager menu) {
			IProject project = getDubProjectFromSelection();
			if(project == null)
				return;
			
			MenuManager dubMenu = new MenuManager(DeeUIMessages.DubActionMenu, 
				DeeImages.DUB_DEPS_CONTAINER.getDescriptor(), "dubMenu");
			dubMenu.add(action1);
			dubMenu.add(action2);
			dubMenu.add(action3);
			
			menu.prependToGroup(ICommonMenuConstants.GROUP_BUILD, dubMenu);
		}
		
		public IProject getDubProjectFromSelection() {
			Object selElement = getSelectionFirstElement();
			if(selElement instanceof IProject) {
				IProject project = (IProject) selElement;
				if(DeeCore.getWorkspaceModel().getBundleInfo(project) != null) {
					return project;
				}
				return null;
			}
			if(selElement instanceof DubDependenciesContainer) {
				DubDependenciesContainer dubDependenciesContainer = (DubDependenciesContainer) selElement;
				return dubDependenciesContainer.getParent();
			}
			if(DeeNavigatorContentProvider.isDubManifestFile(selElement)) {
				IFile file = (IFile) selElement;
				return file.getProject();
			}
			return null;
		}
		
		public abstract class CommonDubAction extends Action {
			
			@Override
			public void run() {
				IProject project = getDubProjectFromSelection();
				DeeToolManager dubMgr = DeeCore.getWorkspaceModelManager().getProcessManager();
				NullProgressMonitor monitor = new NullProgressMonitor(); // TODO: should create Job for this
				
				dubMgr.submitTask(dubMgr.newRunProcessOperation(
					null, DeeCoreMessages.RunningDubCommand, getCommands(project), monitor));
			}
			
			protected abstract String[] getCommands(IProject project);

		}
		
		public class AddDubProjectToLocalPath extends CommonDubAction {
			{
				setText(DeeUIMessages.DubAction_AddLocalPath);
			}
			
			@Override
			protected String[] getCommands(IProject project) {
				return array("dub", "add-local", project.getLocation().toFile().toString());
			}
		}
		
		public class RemoveDubProjectFromLocalPath extends CommonDubAction {
			{
				setText(DeeUIMessages.DubAction_RemoveLocalPath);
			}
			
			@Override
			protected String[] getCommands(IProject project) {
				return array("dub", "remove-local", project.getLocation().toFile().toString());
			}
		}
		
		public class RunDubList extends CommonDubAction {
			{
				setText(DeeUIMessages.DubAction_RunDubList);
			}
			
			@Override
			protected String[] getCommands(IProject project) {
				return array("dub", "list");
			}
		}
		
	}
	
}