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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.util.swt.components.AbstractField;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public class ComboBoxField extends AbstractField<Integer> {
	
	protected final String[] labels;
	protected final String[] values;
	
	protected final String label;
	protected Label labelControl;
	protected Combo combo;
	
	public ComboBoxField(String label, String[] labels, String[] values) {
		this.label = label;
		this.labels = labels;
		this.values = values;
		assertTrue(labels != null && values != null && labels.length == values.length);
		assertTrue(labels.length > 0);
	}
	
	@Override
	public Integer getDefaultFieldValue() {
		return 0;
	}
	
	@Override
	protected void createContents(Composite topControl) {
		labelControl = new Label(topControl, SWT.NONE);
		labelControl.setText(label);
		
		combo = createFieldCombo(this, topControl, SWT.SINGLE | SWT.READ_ONLY);
		combo.setFont(topControl.getFont());
		combo.setItems(labels);
		
		labelControl.setLayoutData(GridDataFactory.swtDefaults().create());
		combo.setLayoutData(GridDataFactory.swtDefaults().create());
	}
	
	@Override
	public Combo getFieldControl() {
		return combo;
	}
	
	@Override
	protected void doUpdateComponentFromValue() {
		int indexValue = getFieldValue();
		if(indexValue == -1) {
			return;
		}
		String label = labels[indexValue];
		combo.setText(label);
		assertTrue(combo.getSelectionIndex() == indexValue);
	}
	
	public String getFieldStringValue() {
		int indexValue = getFieldValue();
		return getPrefValueFromIndex(indexValue);
	}
	
	protected String getPrefValueFromIndex(int index) {
		return index == -1 ? "" : values[index];
	}
	
	public void setFieldStringValue(String stringValue) {
		int indexValue = findIndexForStringValue(stringValue);
		setFieldValue(indexValue);
	}
	
	protected int findIndexForStringValue(String stringValue) {
		int index;
		for (index = 0; index < values.length; index++) {
			if(values[index].equals(stringValue)) {
				return index;
			}
		}
		return -1;
	}
	
}