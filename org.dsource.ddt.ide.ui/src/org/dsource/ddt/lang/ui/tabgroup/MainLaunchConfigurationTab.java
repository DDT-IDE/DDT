/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 		DLTK team -
 * 		Bruno Medeiros - modified DLTK version (5.0) to remove debug and console options
 *******************************************************************************/
package org.dsource.ddt.lang.ui.tabgroup;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.debug.ui.messages.DLTKLaunchConfigurationsMessages;
import org.eclipse.dltk.internal.launching.LaunchConfigurationUtils;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.preferences.FieldValidators;
import org.eclipse.dltk.ui.preferences.FieldValidators.FilePathValidator;
import org.eclipse.dltk.utils.PlatformFileUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

public abstract class MainLaunchConfigurationTab extends AbstractMainLaunchConfigurationTab {
	
	protected Text fScriptText;
	
	
	public MainLaunchConfigurationTab() {
		super();
	}
	
	@Override
	protected void doInitializeForm(ILaunchConfiguration config) {
		updateMainModuleFromConfig(config);
	}
	
	private Button fSearchButton;
	
	/**
	 * Creates the widgets for specifying a main type.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	protected void createMainModuleEditor(Composite parent, String text) {
		final Composite editParent;
		if(true) {
			Group mainGroup = new Group(parent, SWT.NONE);
			mainGroup.setText(text);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			mainGroup.setLayoutData(gd);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			mainGroup.setLayout(layout);
			editParent = mainGroup;
		}
		
		fScriptText = new Text(editParent, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		
		WidgetListener listener = getWidgetListener();
		
		fScriptText.setLayoutData(gd);
		fScriptText.addModifyListener(listener);
		
		fSearchButton = createPushButton(editParent, DLTKLaunchConfigurationsMessages.mainTab_searchButton, null);
		fSearchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSearchButtonSelected();
			}
		});
	}
	
	/**
	 * The select button pressed handler
	 */
	protected void handleSearchButtonSelected() {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				getShell(), new WorkbenchLabelProvider(),
				new WorkbenchContentProvider());
		dialog.setTitle(DLTKLaunchConfigurationsMessages.mainTab_searchButton_title);
		dialog.setMessage(DLTKLaunchConfigurationsMessages.mainTab_searchButton_message);
		
		IScriptProject proj = getScriptProject();
		if (proj == null)
			return;
		dialog.setInput(proj.getProject());
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		if (dialog.open() == IDialogConstants.OK_ID) {
			IResource resource = (IResource) dialog.getFirstResult();
			String arg = resource.getProjectRelativePath().toPortableString();
			// check extension
			fScriptText.setText(arg);
		}
	}
	
	/**
	 * Loads the main type from the launch configuration's preference store
	 * 
	 * @param config
	 *            the config to load the main type from
	 */
	protected void updateMainModuleFromConfig(ILaunchConfiguration config) {
		String moduleName = LaunchConfigurationUtils.getString(config, 
				ScriptLaunchConfigurationConstants.ATTR_MAIN_SCRIPT_NAME, "");
		fScriptText.setText(moduleName);
	}
	
	@Override
	protected void doCreateControl(Composite composite) {
		createMainModuleEditor(composite, DLTKLaunchConfigurationsMessages.mainTab_mainModule);
	}
	
	@Override
	public String getName() {
		return DLTKLaunchConfigurationsMessages.mainTab_title;
	}
	
	@Override
	protected void setDefaults(ILaunchConfigurationWorkingCopy configuration, IModelElement element) {
		super.setDefaults(configuration, element);
		if (element instanceof ISourceModule) {
			configuration.setAttribute(
					ScriptLaunchConfigurationConstants.ATTR_MAIN_SCRIPT_NAME,
					element.getResource().getProjectRelativePath().toString());
		}
	}
	
	@Override
	protected void doPerformApply(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(ScriptLaunchConfigurationConstants.ATTR_MAIN_SCRIPT_NAME, getScriptName());
	}
	
	@Override
	protected IResource getResource(ILaunchConfiguration config) throws CoreException {
		final String projectName = LaunchConfigurationUtils.getProjectName(config);
		
		if (projectName == null || projectName.length() == 0
				|| !Path.ROOT.isValidSegment(projectName)) {
			return null;
		}
		
		final IProject project = getWorkspaceRoot().getProject(projectName);
		if (project.exists() && project.isOpen()) {
			final String scriptName = config.getAttribute(
					ScriptLaunchConfigurationConstants.ATTR_MAIN_SCRIPT_NAME,
					(String) null);
			if (scriptName != null && scriptName.length() != 0
					&& new Path(scriptName).segmentCount() > 0
					&& Path.ROOT.isValidPath(scriptName)) {
				final IFile scriptFile = project.getFile(scriptName);
				if (scriptFile.exists()) {
					return scriptFile;
				}
			}
		}
		return project;
	}
	
	protected String getScriptName() {
		return fScriptText.getText().trim();
	}
	
	protected void setScriptName(String value) {
		fScriptText.setText(value);
	}
	
	/**
	 * Validates the selected launch script.
	 * 
	 * @return true if the selected script is valid, false otherwise
	 */
	protected boolean validateScript() {
		URI script = validateAndGetScriptPath();
		IScriptProject project = getScriptProject();
		IEnvironment environment = EnvironmentManager.getEnvironment(project);
		if (script != null) {
			FilePathValidator validator = new FieldValidators.FilePathValidator();
			IStatus result = validator.validate(script, environment);
			
			if (!result.isOK()) {
				IFileHandle file = PlatformFileUtils.findAbsoluteOrEclipseRelativeFile(environment, 
						Path.fromPortableString(script.getPath()));
				if (file.exists() && file.isDirectory()) {
					return true;
				}
				setErrorMessage(DLTKLaunchConfigurationsMessages.error_scriptNotFound);
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Gets the currently selected {@link ISourceModule}.
	 * 
	 * @return the selected source module or <code>null</code>
	 */
	protected ISourceModule getSourceModule() {
		final IScriptProject project = getScriptProject();
		if (project == null) {
			return null;
		}
		final String scriptName = getScriptName();
		if (scriptName.length() == 0) {
			return null;
		}
		final IFile file = project.getProject().getFile(scriptName);
		return (ISourceModule) DLTKCore.create(file);
	}
	
	protected IProject getProject() {
		return getWorkspaceRoot().getProject(getProjectName());
	}
	
	protected URI validateAndGetScriptPath() {
		IScriptProject proj = getScriptProject();
		if (proj != null) {
		} else {
			return null;
		}
		URI location = proj.getProject().getLocationURI();
		if (location == null) {
			setErrorMessage(DLTKLaunchConfigurationsMessages.error_notAValidProject);
			return null;
		}
		
		URI script = null;
		try {
			script = new URI(location.getScheme(), location.getHost(), location.getPath() + '/' + getScriptName(),
					location.getFragment());
		} catch (URISyntaxException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
		if (script != null) {
			IFile[] files = getWorkspaceRoot().findFilesForLocationURI(script);
			if (files.length != 1) {
				return script;
			}
			
			IFile file = files[0];
			if (file.exists() && file.getLocationURI() != null) {
				script = file.getLocationURI();
			}
		}
		return script;
	}
	
	@Override
	protected boolean validate() {
		return super.validate() && validateScript();
	}
	
	@Override
	public Image getImage() {
		return DLTKPluginImages.get(DLTKPluginImages.IMG_OBJS_CLASS);
	}
	
}