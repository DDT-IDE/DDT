/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *     CDT - certain methods
 *******************************************************************************/
package melnorme.lang.ide.ui.launch;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractLaunchConfigurationTabExt extends AbstractLaunchConfigurationTab {
	
	protected ILaunchConfiguration launchConfig;
	
	protected ILaunchConfiguration getCurrentLaunchConfiguration() {
		return launchConfig;
	}
	
	protected void setCurrentLaunchConfiguration(ILaunchConfiguration config) {
		launchConfig = config;
	}
	
	@Override
	public void initializeFrom(ILaunchConfiguration config) {
		setCurrentLaunchConfiguration(config);
	}
	
	
	/**
	 * Creates a button that allows user to insert build variables.
	 * 
	 * @since 7.1
	 */
	protected Button createVariablesButton(Composite parent, String label, final Text textField) {
		Button variablesButton = createPushButton(parent, label, null); 
		variablesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				handleVariablesButtonSelected(textField);
			}
		});
		return variablesButton;
	}
	
	/**
	 * A variable entry button has been pressed for the given text
	 * field. Prompt the user for a variable and enter the result
	 * in the given field.
	 */
	private void handleVariablesButtonSelected(Text textField) {
		String variable = getVariable();
		if (variable != null) {
			textField.insert(variable);
		}
	}
	
	/**
	 * Prompts the user to choose and configure a variable and returns
	 * the resulting string, suitable to be used as an attribute.
	 */
	private String getVariable() {
		StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(getShell());
		dialog.open();
		return dialog.getVariableExpression();
	}
	
}