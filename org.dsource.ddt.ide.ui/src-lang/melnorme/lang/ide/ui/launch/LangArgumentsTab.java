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

import melnorme.lang.ide.launching.LaunchConstants;
import melnorme.lang.ide.launching.LaunchMessages;
import melnorme.lang.ide.ui.LangImages;
import melnorme.lang.ide.ui.LangUIPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


//BM: Original based on org.eclipse.cdt.launch.ui.CArgumentsTab
/**
 * A launch configuration tab that displays and edits program arguments,
 * and working directory launch configuration attributes.
 * <p>
 * This class may be instantiated. This class is not intended to be subclassed.
 * </p>
 */
public class LangArgumentsTab extends AbstractLaunchConfigurationTabExt {
	/**
	 * Tab identifier used for ordering of tabs added using the 
	 * <code>org.eclipse.debug.ui.launchConfigurationTabs</code>
	 * extension point.
	 *   
	 * @since 6.0
	 */
//	public static final String TAB_ID = "org.eclipse.cdt.cdi.launch.argumentsTab"; //$NON-NLS-1$
	
	// Program arguments UI widgets
	protected Label fPrgmArgumentsLabel;
	protected Text fPrgmArgumentsText;
	protected Button fArgumentVariablesButton;
	
	// Working directory
	protected LangWorkingDirectoryBlock fWorkingDirectoryBlock = new LangWorkingDirectoryBlock();
	
	@Override
	public void createControl(Composite parent) {
		Font font = parent.getFont();
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		comp.setLayout(layout);
		comp.setFont(font);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		comp.setLayoutData(gd);
		setControl(comp);
		setHelpContextId();
		
		createArgumentComponent(comp, 1);
		
		fWorkingDirectoryBlock.createControl(comp);
	}
	
	protected void setHelpContextId() {
//		LangUIPlugin.getDefault().getWorkbench().getHelpSystem().setHelp(
//				getControl(), 
//				ICDTLaunchHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_ARGUMNETS_TAB);
	}
	
	protected void createArgumentComponent(Composite comp, int horizontalSpan) {
		Font font = comp.getFont();
		Group group = new Group(comp, SWT.NONE);
		group.setFont(font);
		group.setLayout(new GridLayout());
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = horizontalSpan;
		group.setLayoutData(gd);
		
		group.setText(LaunchMessages.LangArgumentsTab_Program_Arguments);
		fPrgmArgumentsText = new Text(group, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		fPrgmArgumentsText.getAccessible().addAccessibleListener(
				new AccessibleAdapter() {
					@Override
					public void getName(AccessibleEvent e) {
						e.result = LaunchMessages.LangArgumentsTab_Program_Arguments;
					}
				});
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 40;
		gd.widthHint = 100;
		fPrgmArgumentsText.setLayoutData(gd);
		fPrgmArgumentsText.setFont(font);
		fPrgmArgumentsText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent evt) {
				updateLaunchConfigurationDialog();
			}
		});
		fArgumentVariablesButton= createVariablesButton(group, LaunchMessages.LangArgumentsTab_Variables, fPrgmArgumentsText); 
		gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		fArgumentVariablesButton.setLayoutData(gd);
		// need to strip the mnemonic from buttons:
		ControlAccessibleListener.addControlAccessibleListener(fArgumentVariablesButton, fArgumentVariablesButton.getText());
	}
	
	@Override
	public boolean isValid(ILaunchConfiguration config) {
		return fWorkingDirectoryBlock.isValid(config);
	}
	
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(LaunchConstants.ATTR_PROG_ARGUMENTS, (String) null);
		config.setAttribute(LaunchConstants.ATTR_WORKING_DIRECTORY, (String) null);
	}
	
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			fPrgmArgumentsText.setText(configuration.getAttribute(LaunchConstants.ATTR_PROG_ARGUMENTS, "")); //$NON-NLS-1$
			fWorkingDirectoryBlock.initializeFrom(configuration);
		}
		catch (CoreException e) {
			setErrorMessage(LaunchMessages.getFormattedString(LaunchMessages.Launch_common_Exception_occurred_reading_configuration_EXCEPTION, e.getStatus().getMessage())); //$NON-NLS-1$
			LangUIPlugin.log(e);
		}
	}
	
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(LaunchConstants.ATTR_PROG_ARGUMENTS, getAttributeValueFrom(fPrgmArgumentsText));
		fWorkingDirectoryBlock.performApply(configuration);
	}
	
	/**
	 * Returns the string in the text widget, or <code>null</code> if empty.
	 * 
	 * @return text or <code>null</code>
	 */
	protected String getAttributeValueFrom(Text text) {
		String content = text.getText().trim();
		// Bug #131513 - eliminate Windows \r line delimiter
		content = content.replaceAll("\r\n", "\n");  //$NON-NLS-1$//$NON-NLS-2$
		if (content.length() > 0) {
			return content;
		}
		return null;
	}
	
	@Override
	public String getId() {
		return null;
		//return TAB_ID;
	}
	
	@Override
	public String getName() {
		return LaunchMessages.LangArgumentsTab_Arguments;
	}
	
	@Override
	public void setLaunchConfigurationDialog(ILaunchConfigurationDialog dialog) {
		super.setLaunchConfigurationDialog(dialog);
		fWorkingDirectoryBlock.setLaunchConfigurationDialog(dialog);
	}
	
	@Override
	public String getErrorMessage() {
		String m = super.getErrorMessage();
		if (m == null) {
			return fWorkingDirectoryBlock.getErrorMessage();
		}
		return m;
	}
	
	@Override
	public String getMessage() {
		String m = super.getMessage();
		if (m == null) {
			return fWorkingDirectoryBlock.getMessage();
		}
		return m;
	}
	
	@Override
	public Image getImage() {
		return LangImages.getImage(LangImages.IMG_VIEW_ARGUMENTS_TAB);
	}
	
	@Override
	protected void updateLaunchConfigurationDialog() {
		super.updateLaunchConfigurationDialog();
	}
	
}