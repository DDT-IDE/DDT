/*******************************************************************************
 * Copyright (c) 2005, 2012 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *     IBM Corporation
 *     Bruno Medeiros - lang modifications
 *******************************************************************************/
package melnorme.lang.ide.ui.launch;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import melnorme.lang.ide.launching.LaunchConstants;
import melnorme.lang.ide.ui.LangImages;
import melnorme.lang.ide.ui.LangUIMessages;
import melnorme.utilbox.status.StatusException;


//BM: Original based on org.eclipse.cdt.launch.ui.CArgumentsTab
/**
 * A launch configuration tab that displays and edits program arguments,
 * and working directory launch configuration attributes.
 * <p>
 * This class may be instantiated. This class is not intended to be subclassed.
 * </p>
 */
public class LangArgumentsTab extends AbstractLaunchConfigurationTabExt {
	
	protected final LangArgumentsBlock2 argumentsBlock = new LangArgumentsBlock2(); 
	protected final LangWorkingDirectoryBlock workingDirectoryBlock = new LangWorkingDirectoryBlock();
	
	public LangArgumentsTab() {
		argumentsBlock.addChangeListener(this::updateLaunchConfigurationDialog);
	}
	
	/* ----------------- Control creation ----------------- */
	
	@Override
	public String getName() {
		return LangUIMessages.LangArgumentsTab_Arguments;
	}
	
	@Override
	public Image getImage() {
		return LangImages.IMG_LAUNCHTAB_ARGUMENTS.getImage();
	}
	
	@Override
	public void setLaunchConfigurationDialog(ILaunchConfigurationDialog dialog) {
		super.setLaunchConfigurationDialog(dialog);
		workingDirectoryBlock.setLaunchConfigurationDialog(dialog);
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayout layout = new GridLayout(1, true);
		comp.setLayout(layout);
		comp.setFont(parent.getFont());
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		comp.setLayoutData(gd);
		setHelpContextId();
		
		argumentsBlock.createComponent(comp, new GridData(GridData.FILL_BOTH));
		workingDirectoryBlock.createControl(comp);
	}
	
	protected void setHelpContextId() {
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), 
//				ICDTLaunchHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_ARGUMNETS_TAB);
	}
	
	/* ---------- validation ---------- */
	
	@Override
	public boolean isValid(ILaunchConfiguration config) {
		if(!workingDirectoryBlock.isValid(config)) {
			setErrorMessage(workingDirectoryBlock.getErrorMessage());
			setMessage(workingDirectoryBlock.getMessage());
			return false;
		}
		return super.isValid(config);
	}
	
	@Override
	protected void doValidate() throws StatusException {
	}
	
	/* ----------------- Bindings (Apply/Revert) ----------------- */
	
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(LaunchConstants.ATTR_PROGRAM_ARGUMENTS, "");
		workingDirectoryBlock.setDefaults(config);
	}
	
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		argumentsBlock.setFieldValue(getConfigAttribute(configuration, LaunchConstants.ATTR_PROGRAM_ARGUMENTS, ""));
		workingDirectoryBlock.initializeFrom(configuration);
	}
	
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(LaunchConstants.ATTR_PROGRAM_ARGUMENTS, argumentsBlock.getFieldValue());
		workingDirectoryBlock.performApply(configuration);
	}
	
}