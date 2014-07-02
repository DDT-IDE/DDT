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
import mmrnmhrm.core.DeeCoreMessages;
import mmrnmhrm.core.engine_client.DubProcessManager;
import mmrnmhrm.core.workspace.CoreDubModel;
import mmrnmhrm.core.workspace.WorkspaceModelManager;
import mmrnmhrm.core.workspace.viewmodel.DubDependenciesContainer;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.DeeUIMessages;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

public class DubNavigatorActionProvider extends CommonActionProvider {
	
	protected ActionGroup dubActionGroup = new ActionGroup() { }; // No-op action group
	
	@Override
	public void init(ICommonActionExtensionSite site) {
		if (site.getViewSite() instanceof ICommonViewerWorkbenchSite) {
			ICommonViewerWorkbenchSite workbenchSite = (ICommonViewerWorkbenchSite) site.getViewSite();
			if (workbenchSite.getPart() instanceof IViewPart) {
				IViewPart viewPart= (IViewPart) workbenchSite.getPart();
				
				dubActionGroup = new DubPathActionGroup(viewPart);
			}
		}
	}
	
	@Override
	public void setContext(ActionContext context) {
		dubActionGroup.setContext(context);
	}
	
	@Override
	public void fillActionBars(IActionBars actionBars) {
		dubActionGroup.fillActionBars(actionBars);
	}
	
	@Override
	public void fillContextMenu(IMenuManager menu) {
		dubActionGroup.fillContextMenu(menu);
	}
	
	public static class DubPathActionGroup extends ActionGroup {
		
		protected final IViewPart viewPart;
		
		protected final AddDubProjectToLocalPath action1 = new AddDubProjectToLocalPath();
		protected final RemoveDubProjectFromLocalPath action2 = new RemoveDubProjectFromLocalPath();
		protected final RunDubList action3 = new RunDubList();
		
		public DubPathActionGroup(IViewPart viewPart) {
			this.viewPart = viewPart;
		}
		
		@Override
		public void fillContextMenu(IMenuManager menu) {
			Object inputElement = getContext().getInput();
			IProject project = getDubProjectFromSelection();
			if(project == null)
				return;
			
			MenuManager dubMenu = new MenuManager(DeeUIMessages.DubActionMenu, 
				DeePluginImages.DUB_DEPS_CONTAINER.getDescriptor(), "dubMenu");
			dubMenu.add(action1);
			dubMenu.add(action2);
			dubMenu.add(action3);
			
			if(inputElement != null) {
				action3.setText(inputElement.toString());
			}
			menu.prependToGroup(ICommonMenuConstants.GROUP_BUILD, dubMenu);
		}
		
		protected Object getSelectionFirst() {
			ISelection selection = getContext().getSelection();
			if(selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				return structuredSelection.getFirstElement();
			}
			return selection;
		}
		
		public IProject getDubProjectFromSelection() {
			Object selElement = getSelectionFirst();
			if(selElement instanceof IProject) {
				IProject project = (IProject) selElement;
				if(CoreDubModel.getBundleInfo(project.getName()) != null) {
					return project;
				}
				return null;
			}
			if(selElement instanceof DubDependenciesContainer) {
				DubDependenciesContainer dubDependenciesContainer = (DubDependenciesContainer) selElement;
				return dubDependenciesContainer.getParent();
			}
			if(DubNavigatorContentProvider.isDubManifestFile(selElement)) {
				IFile file = (IFile) selElement;
				return file.getProject();
			}
			return null;
		}
		
		public abstract class CommonDubAction extends Action {
			
			@Override
			public void run() {
				IProject project = getDubProjectFromSelection();
				DubProcessManager dubMgr = WorkspaceModelManager.getDefault().getProcessManager();
				NullProgressMonitor monitor = new NullProgressMonitor(); // TODO: should create Job for this
				
				dubMgr.submitDubCommand(dubMgr.newDubOperation(
					DeeCoreMessages.RunningDubCommand, null, getCommands(project), monitor));
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