/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.navigator;

import static melnorme.utilbox.core.CoreUtil.list;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IViewPart;

import melnorme.lang.ide.ui.launch.LangLaunchShortcut;
import melnorme.lang.ide.ui.navigator.BuildTargetsActionGroup;
import melnorme.lang.ide.ui.navigator.LangNavigatorActionProvider;
import melnorme.lang.ide.ui.operations.RunToolOperation.RunSDKToolOperation;
import mmrnmhrm.ui.DeeUIMessages;
import mmrnmhrm.ui.launch.DeeLaunchShortcut;

public class DeeNavigatorActionProvider extends LangNavigatorActionProvider {
	
	@Override
	protected BuildTargetsActionGroup createBuildTargetsActionGroup(IViewPart viewPart) {
		return new BuildTargetsActionGroup(viewPart) {
			@Override
			protected LangLaunchShortcut createLaunchShortcut() {
				return new DeeLaunchShortcut();
			}
		};
	}
	
	@Override
	protected void initActionGroups(IViewPart viewPart) {
		super.initActionGroups(viewPart);
		actionGroups.add(new DubPathActionGroup(viewPart));
	}
	
	public static class DubPathActionGroup extends BundleOperationsActionGroup {
		
		public DubPathActionGroup(IViewPart viewPart) {
			super(viewPart);
		}
		
		@Override
		protected void initActions(MenuManager bundleOpsMenu, IProject project) {
			addRunOperationAction(bundleOpsMenu, new AddDubProjectToLocalPath(project));
			addRunOperationAction(bundleOpsMenu, new RemoveDubProjectFromLocalPath(project));
			addRunOperationAction(bundleOpsMenu, new RunDubList(project));
		}
		
		@Override
		protected String getMenuName() {
			return DeeUIMessages.DubActionMenu;
		}
		
		public class AddDubProjectToLocalPath extends RunSDKToolOperation {
			public AddDubProjectToLocalPath(IProject project) {
				super(DeeUIMessages.DubAction_AddLocalPath, project,
					list("add-local", project.getLocation().toFile().toString()));
			}
		}
		
		public class RemoveDubProjectFromLocalPath extends RunSDKToolOperation {
			public RemoveDubProjectFromLocalPath(IProject project) {
				super(DeeUIMessages.DubAction_RemoveLocalPath, project, 
					list("remove-local", project.getLocation().toFile().toString()));
			}
		}
		
		public class RunDubList extends RunSDKToolOperation {
			public RunDubList(IProject project) {
				super(DeeUIMessages.DubAction_RunDubList, project, 
					list("list"));
			}
		}
		
	}

}