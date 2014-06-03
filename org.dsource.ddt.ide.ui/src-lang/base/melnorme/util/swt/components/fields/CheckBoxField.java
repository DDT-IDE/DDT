/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.util.swt.components.fields;

import melnorme.util.swt.components.AbstractField;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class CheckBoxField extends AbstractField<Boolean> {
	
	protected String labelText;
	protected Button checkBox;
	
	public CheckBoxField(String labelText) {
		this.labelText = labelText;
	}
	
	@Override
	public Boolean getDefaultFieldValue() {
		return false;
	}
	
	@Override
	protected void createContents(Composite topControl) {
		checkBox = createFieldCheckbox(this, topControl, SWT.NONE);
		checkBox.setText(labelText);
		checkBox.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).create());
	}
	
	@Override
	public Button getFieldControl() {
		return checkBox;
	}
	
	@Override
	protected void doUpdateComponentFromValue() {
		checkBox.setSelection(getFieldValue());
	}
	
}