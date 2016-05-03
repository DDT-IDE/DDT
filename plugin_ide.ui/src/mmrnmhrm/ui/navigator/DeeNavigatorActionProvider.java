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

import java.nio.file.Path;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IViewPart;

import melnorme.lang.ide.core.DeeToolPreferences;
import melnorme.lang.ide.core.operations.RunToolOperationOnResource;
import melnorme.lang.ide.core.operations.ILangOperationsListener_Default.ProcessStartKind;
import melnorme.lang.ide.core.operations.ILangOperationsListener_Default.StartOperationOptions;
import melnorme.lang.ide.ui.launch.LangLaunchShortcut;
import melnorme.lang.ide.ui.navigator.BuildTargetsActionGroup;
import melnorme.lang.ide.ui.navigator.LangNavigatorActionProvider;
import melnorme.lang.ide.ui.operations.ToolSourceModifyingOperation;
import melnorme.lang.ide.ui.operations.RunToolUIOperation.RunSDKUIToolOperation;
import melnorme.utilbox.core.CommonException;
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
			addRunOperationAction(bundleOpsMenu, new DubList(project));
			addRunOperationAction(bundleOpsMenu, new FormatBundleOperation(project));
		}
		
		@Override
		protected String getMenuName() {
			return DeeUIMessages.DubActionMenu;
		}
		
		public class AddDubProjectToLocalPath extends RunSDKUIToolOperation {
			public AddDubProjectToLocalPath(IProject project) {
				super(DeeUIMessages.DubAction_AddLocalPath, project,
					list("add-local", project.getLocation().toFile().toString()));
			}
		}
		
		public class RemoveDubProjectFromLocalPath extends RunSDKUIToolOperation {
			public RemoveDubProjectFromLocalPath(IProject project) {
				super(DeeUIMessages.DubAction_RemoveLocalPath, project, 
					list("remove-local", project.getLocation().toFile().toString()));
			}
		}
		
		public class DubList extends RunSDKUIToolOperation {
			public DubList(IProject project) {
				super(DeeUIMessages.DubAction_RunDubList, project, 
					list("list"));
			}
		}
		
	}
	
	public static class FormatBundleOperation extends ToolSourceModifyingOperation {
		public FormatBundleOperation(IProject project) {
			super("Format DUB package (dfmt)", 
				new RunToolOperationOnResource(
					project, 
					list(), 
					new StartOperationOptions(ProcessStartKind.BUILD, true, true)
				) {
					@Override
					protected ProcessBuilder createProcessBuilder() throws CommonException {
						Path fmtPath = DeeToolPreferences.DFMT_PATH.getDerivedValue();
						
						return getToolManager().createToolProcessBuilder(project, fmtPath, 
							commands.toArrayList().addElements("--inplace", ".").toArray(String.class));
					}
				}
			);
		}
		
	}

}