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
package melnorme.lang.ide.ui.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.launch.ILaunchConfigSerializer;
import melnorme.lang.ide.core.launch.ProjectLaunchSettings;
import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.lang.ide.core.utils.ProjectValidator;
import melnorme.lang.ide.ui.LangImages;
import melnorme.lang.ide.ui.LangUIMessages;
import melnorme.lang.ide.ui.fields.ProjectField;
import melnorme.lang.ide.ui.utils.WorkbenchUtils;
import melnorme.lang.tooling.data.Severity;
import melnorme.lang.tooling.data.StatusException;
import melnorme.util.swt.SWTFactoryUtil;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.core.fntypes.CommonRunnable;

public abstract class ProjectBasedLaunchConfigurationTab extends AbstractLaunchConfigurationTabExt {
	
	protected final ProjectField projectField = new LaunchTabProjectField(); 
	
	public ProjectBasedLaunchConfigurationTab() {
		super();
		projectField.addChangeListener(this::updateLaunchConfigurationDialog);
	}
	
	protected final String getProjectName() {
		return projectField.getFieldValue();
	}
	
	protected IProject getValidProjectOrNull() {
		try {
			return getValidProject();
		} catch(StatusException e) {
			return null;
		}
	}
	
	protected IProject getValidProject() throws StatusException {
		return getProjectValidator().getProject(getProjectName());
	}
	
	protected ProjectValidator getProjectValidator() {
		return new ProjectValidator(LangCore.NATURE_ID);
	}
	
	@Override
	protected void doValidate() throws StatusException {
		getValidProject();
	}
	
	/* ----------------- Control creation ----------------- */
	
	@Override
	public String getName() {
		return LangUIMessages.mainLaunchTab_title;
	}
	
	@Override
	public Image getImage() {
		return LangImages.IMG_LAUNCHTAB_MAIN.getImage();
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite topControl = SWTFactoryUtil.createComposite(parent);
		setControl(topControl);
		
		topControl.setLayout(GridLayoutFactory.swtDefaults().spacing(5, 0).create());
		
		projectField.createComponent(topControl, new GridData(GridData.FILL_HORIZONTAL));
		createVerticalSpacer(topControl, 1);
		
		createCustomControls(topControl);
		createVerticalSpacer(topControl, 1);
		
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
		// IScriptDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_MAIN_TAB);
	}
	
	@SuppressWarnings("unused")
	protected void createCustomControls(Composite composite) {
	}
	
	protected class LaunchTabProjectField extends ProjectField {
		@Override
		protected IProject[] getDialogChooseElements() throws CoreException {
			return EclipseUtils.getOpenedProjects(LangCore.NATURE_ID);
		}
	}
	
	/* ----------------- Bindings (Apply/Revert) ----------------- */
	
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		IResource contextResource = WorkbenchUtils.getContextResource();
		runOperation(
			() -> getDefaultProjectSettings(contextResource).saveToConfig(config, true));
	}
	
	protected ILaunchConfigSerializer getDefaultProjectSettings(IResource contextResource) {
		return getDefaultProjectLaunchSettings().initFrom(contextResource);
	}
	
	protected abstract ILaunchConfigSerializer getDefaultProjectLaunchSettings();
	
	@Override
	public void initializeFrom(ILaunchConfiguration config) {
		runOperation(() -> doInitializeFrom(config));
	}
	
	public void doInitializeFrom(ILaunchConfiguration config) throws CommonException {
		initializeFrom(doInitializeFrom_createSettings(config));
	}
	
	protected abstract ProjectLaunchSettings doInitializeFrom_createSettings(ILaunchConfiguration config) 
			throws CommonException;
	
	protected void initializeFrom(ProjectLaunchSettings projectSettings) {
		projectField.setFieldValue(projectSettings.projectName);
	}
	
	@Override
	public final void performApply(ILaunchConfigurationWorkingCopy config) {
		runOperation(
			() -> getLaunchSettingsFromTab().saveToConfig(config));
	}
	
	public void runOperation(CommonRunnable op) {
		try {
			op.run();
		} catch(CommonException e) {
			LangCore.logStatusException(e.toStatusException(Severity.ERROR));
		}
	}
	
	protected abstract ProjectLaunchSettings getLaunchSettingsFromTab();
	
}