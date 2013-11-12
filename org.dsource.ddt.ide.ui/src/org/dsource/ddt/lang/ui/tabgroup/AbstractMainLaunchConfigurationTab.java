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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptModel;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.ScriptModelHelper;
import org.eclipse.dltk.debug.ui.messages.DLTKLaunchConfigurationsMessages;
import org.eclipse.dltk.internal.launching.DLTKLaunchingPlugin;
import org.eclipse.dltk.internal.launching.LaunchConfigurationUtils;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * Modification of: org.eclipse.dltk.debug.ui.launchConfigurations.ScriptLaunchConfigurationTab
 */
public abstract class AbstractMainLaunchConfigurationTab extends AbstractLaunchConfigurationTab {
	
	protected static IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	
	protected static IScriptModel getScriptModel() {
		return DLTKCore.create(getWorkspaceRoot());
	}
	
	protected Button fProjButton;
	protected Text fProjText;

	protected WidgetListener fListener = new WidgetListener();
	
	public AbstractMainLaunchConfigurationTab() {
	}

	@Override
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);

		GridLayout topLayout = new GridLayout();
		topLayout.verticalSpacing = 0;
		comp.setLayout(topLayout);

		createProjectEditor(comp);
		createVerticalSpacer(comp, 1);

		doCreateControl(comp);
		createVerticalSpacer(comp, 1);

		createCustomSections(comp);
		Dialog.applyDialogFont(comp);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
		// IScriptDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_MAIN_TAB);
	}

	@SuppressWarnings("unused")
	protected void createCustomSections(Composite comp) {
	}

	private boolean initializing = false;

	@Override
	public final void initializeFrom(ILaunchConfiguration config) {
		initializing = true;
		try {
			updateProjectFromConfig(config);
			doInitializeForm(config);
		} finally {
			initializing = false;
		}
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		validatePage(false);
		return !isError();
	}

	/**
	 * This is a top level method to initiate the manual page validation.
	 */
	protected final void validatePage() {
		validatePage(true);
	}

	/**
	 * This is a top level method to initiate the page validation.
	 */
	private final void validatePage(boolean manual) {
		setErrorMessage(null);
		setMessage(null);
		validate();
	}

	/**
	 * Validates the page. This method should be overridden when more checks are
	 * needed.
	 * 
	 * @return <code>true</code> if input is correct and <code>false</code>
	 *         otherwise
	 */
	protected boolean validate() {
		return validateProject();
	}

	protected boolean isError() {
		return getErrorMessage() != null;
	}

	@Override
	public final void performApply(ILaunchConfigurationWorkingCopy config) {
		String project = fProjText.getText().trim();
		config.setAttribute(ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME, project);

		doPerformApply(config);
		try {
			mapResources(config);
		} catch (CoreException e) {
			DLTKLaunchingPlugin.logWarning(e);
		} catch (IllegalArgumentException e) {
			DLTKLaunchingPlugin.logWarning(e);
		}
	}

	/**
	 * @param config
	 * @throws CoreException
	 */
	protected void mapResources(ILaunchConfigurationWorkingCopy config) throws CoreException {
		IResource resource = getResource(config);
		if (resource == null) {
			config.setMappedResources(null);
		} else {
			config.setMappedResources(new IResource[] { resource });
		}
	}

	/**
	 * Returns a resource mapping for the given launch configuration, or
	 * <code>null</code> if none.
	 * 
	 * @param config
	 *            working copy
	 * @throws CoreException
	 * @returns resource or <code>null</code>
	 * @throws CoreException
	 *             if an exception occurs mapping resource
	 */
	protected IResource getResource(ILaunchConfiguration config) throws CoreException {
		final String projName = config.getAttribute(
				ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME,
				(String) null);
		if (projName != null && Path.ROOT.isValidSegment(projName)) {
			IScriptProject project = getScriptModel().getScriptProject(projName);
			if (project.exists()) {
				return project.getProject();
			}
		}
		return null;
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		IModelElement element = getContextModelElement();
		setDefaults(configuration, element);
	}
	
	/**
	 * Attempts to guess the current project and script being launched.
	 * 
	 * @return model element - the script or the project.
	 */
	protected IModelElement getContextModelElement() {
		IWorkbenchPage page = DLTKUIPlugin.getActivePage();
		if (page == null) {
			return null;
		}
		final ISelection selection = page.getSelection();
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection) selection;
			if (!ss.isEmpty()) {
				final Object obj = ss.getFirstElement();
				if (obj instanceof IModelElement) {
					return (IModelElement) obj;
				}
				if (obj instanceof IResource) {
					IModelElement me = DLTKCore.create((IResource) obj);
					if (me == null) {
						final IProject project = ((IResource) obj).getProject();
						me = DLTKCore.create(project);
					}
					if (me != null) {
						return me;
					}
				}
			}
		}
		IEditorPart editor = page.getActiveEditor();
		if (editor == null) {
			return null;
		}

		IEditorInput editorInput = editor.getEditorInput();
		if (editorInput == null) {
			return null;
		}

		IModelElement me = DLTKUIPlugin.getEditorInputModelElement(editorInput);
		if (me != null) {
			IScriptProject project = me.getScriptProject();
			if (project != null && validateProject(project)) {
				/*
				 * TODO: validate script is an executable and not library/module
				 * otherwise, return null and make user select
				 */
				IResource resource = me.getResource();
				if (resource != null) {
					return me;
				}
				return project;
			}
		}

		return null;
	}
	
	protected void setDefaults(ILaunchConfigurationWorkingCopy configuration, IModelElement element) {
		if (element != null && validateProject(element.getScriptProject())) {
			configuration.setAttribute(
					ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					element.getScriptProject().getElementName());
		}
	}

	/**
	 * Creates the sub-class specific control.
	 * 
	 * <p>
	 * Sub-classes can widgets directly to the <code>composite</code> object
	 * that is passed to them.
	 * </p>
	 * 
	 * @param composite
	 *            control composite
	 * 
	 * @see #createControl(Composite)
	 */
	protected abstract void doCreateControl(Composite composite);

	/**
	 * Performs the sub-class specific configuration tab initialization.
	 * 
	 * @param config
	 *            launch configuration
	 * 
	 * @see #initializeFrom(ILaunchConfiguration)
	 */
	protected abstract void doInitializeForm(ILaunchConfiguration config);

	protected abstract void doPerformApply(ILaunchConfigurationWorkingCopy config);

	protected abstract String getNatureID();



	/**
	 * Creates a project editor
	 * 
	 * <p>
	 * Creates a group containing an input text field and 'Browse' button to
	 * select a project from the workspace.
	 * </p>
	 * 
	 */
	protected void createProjectEditor(Composite parent) {
		final Composite editParent;
		if (true) {
			Group group = new Group(parent, SWT.NONE);
			group.setText(DLTKLaunchConfigurationsMessages.mainTab_projectGroup);
			
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			group.setLayoutData(gd);

			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			group.setLayout(layout);
			editParent = group;
		}
		
		fProjText = new Text(editParent, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fProjText.setLayoutData(gd);
		fProjText.addModifyListener(fListener);

		fProjButton = createPushButton(editParent, DLTKLaunchConfigurationsMessages.mainTab_projectButton, null);
		fProjButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleProjectButtonSelected();
			}
		});
	}

	protected String getLanguageName() {
		IDLTKLanguageToolkit toolkit = DLTKLanguageManager.getLanguageToolkit(getNatureID());
		if (toolkit != null) {
			return toolkit.getLanguageName();
		}
		return null;
	}

	protected IScriptProject getScriptProject() {
		if (getProjectName() == null || getProjectName().isEmpty()) {
			return null;
		}
		
		return getScriptModel().getScriptProject(getProjectName());
	}


	protected final String getProjectName() {
		return fProjText.getText().trim();
	}

	protected WidgetListener getWidgetListener() {
		return fListener;
	}
	
	/**
	 * A listener which handles widget change events for the controls in this tab.
	 */
	protected class WidgetListener implements ModifyListener, SelectionListener {
		@Override
		public void modifyText(ModifyEvent e) {
			if (initializing) {
				return;
			}
			if (e.getSource() == fProjText) {
				projectChanged();
			}
			validatePage();
			updateLaunchConfigurationDialog();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			/* do nothing */
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			updateLaunchConfigurationDialog();
		}
	}
	
	/**
	 * Show a dialog that lets the user select a project. This in turn provides
	 * context for the main type, allowing the user to key a main type name, or
	 * constraining the search for main types to the specified project.
	 */
	protected void handleProjectButtonSelected() {
		IScriptProject project = chooseProject();
		if (project == null) {
			return;
		}
		
		String projectName = project.getElementName();
		setProjectName(projectName);
	}
	
	/**
	 * chooses a project for the type of launch config that it is
	 * 
	 * @return
	 */
	protected IScriptProject chooseProject() {
		final ILabelProvider labelProvider = DLTKUILanguageManager.createLabelProvider(getNatureID());
		final ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setTitle(DLTKLaunchConfigurationsMessages.mainTab_chooseProject_title);
		dialog.setMessage(DLTKLaunchConfigurationsMessages.mainTab_chooseProject_message);

		try {
			final IScriptProject[] projects = ScriptModelHelper
					.getOpenedScriptProjects(DLTKCore.create(getWorkspaceRoot()), getNatureID());
			dialog.setElements(projects);
		} catch (ModelException e) {
			DLTKLaunchingPlugin.log(e);
		}
		
		final IScriptProject project = getScriptProject();
		if (project != null) {
			dialog.setInitialSelections(new Object[] { project });
		}

		if (dialog.open() == Window.OK) {
			return (IScriptProject) dialog.getFirstResult();
		}

		return null;
	}


	/**
	 * Sets the name of the project associated with the launch configuration
	 * 
	 * @param name
	 *            project name
	 */
	protected final void setProjectName(String name) {
		fProjText.setText(name);
	}
	
	/**
	 * updates the project text field form the configuration
	 * 
	 * @param config
	 *            the configuration we are editing
	 */
	protected void updateProjectFromConfig(ILaunchConfiguration config) {
		String projectName = LaunchConfigurationUtils.getProjectName(config);
		if (projectName != null) {
			setProjectName(projectName);
		}
	}
	
	protected boolean validateProject() {
		String projectName = getProjectName();
		if (projectName.length() == 0) {
			setErrorMessage(DLTKLaunchConfigurationsMessages.error_selectProject);
			return false;
		}

		IScriptProject proj = getScriptModel().getScriptProject(projectName);
		if (proj == null || !validateProject(proj)) {
			setErrorMessage(DLTKLaunchConfigurationsMessages.error_notAValidProject);
			return false;
		}

		return true;
	}

	/**
	 * Tests if the project field is valid. Returns <code>true</code> if valid
	 * project is selected or <code>false</code> otherwise.
	 * 
	 * @return
	 */
	protected boolean isValidProject() {
		final String projectName = getProjectName();
		if (projectName.length() == 0) {
			return false;
		}
		IScriptProject proj = getScriptModel().getScriptProject(projectName);
		return proj != null && validateProject(proj);
	}

	/**
	 * Tests if the specified project is valid for this launch configuration
	 * type.
	 * 
	 * @param project
	 * @return
	 */
	protected boolean validateProject(IScriptProject project) {
		if (project != null) {
			try {
				return project.getProject().hasNature(getNatureID());
			} catch (CoreException e) {
				if (DLTKCore.DEBUG)
					e.printStackTrace();
			}
		}
		return false;
	}
	
	protected void projectChanged() {
		notifyProjectChangedListeners(getWorkspaceRoot().getProject(getProjectName()));
	}
	
	public static interface IMainLaunchConfigurationTabListener {
		
		void projectChanged(IProject project);
		
	}
	
	protected final ListenerList listeners = new ListenerList();
	
	public void addListener(IMainLaunchConfigurationTabListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(IMainLaunchConfigurationTabListener listener) {
		this.listeners.remove(listener);
	}
	
	private IProject lastProjectNotification = null;
	
	private void notifyProjectChangedListeners(IProject project) {
		if (project != null) {
			if (project.equals(lastProjectNotification)) {
				return;
			}
		} else {
			if (lastProjectNotification == null) {
				return;
			}
		}
		lastProjectNotification = project;
		Object[] list = this.listeners.getListeners();
		for (int i = 0; i < list.length; i++) {
			((IMainLaunchConfigurationTabListener) list[i]).projectChanged(project);
		}
	}
	
}