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

import melnorme.util.swt.SWTFactoryUtil;
import melnorme.util.swt.components.AbstractField;

import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TextField2 extends AbstractField<String> {
	
	protected final String label;
	protected final int textLimit;
	
	protected Label labelControl;
	protected Text textControl;
	
	public TextField2(String label, int textLimit) {
		this.label = label;
		this.textLimit = textLimit;
	}
	
	@Override
	public String getDefaultFieldValue() {
		return "";
	}
	
	@Override
	protected void createContents(Composite topControl) {
		PixelConverter pixelConverter = new PixelConverter(topControl);
		
		labelControl = SWTFactoryUtil.createLabel(topControl, SWT.NONE, label, new GridData()); 
		textControl = createFieldText(this, topControl, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData();
		gd.widthHint = pixelConverter.convertWidthInCharsToPixels(textLimit + 1);
		textControl.setLayoutData(gd);
		textControl.setTextLimit(textLimit);
	}
	
	@Override
	public Text getFieldControl() {
		return textControl;
	}
	
	@Override
	protected void doUpdateComponentFromValue() {
		textControl.setText(getFieldValue());
	}
	
	public void setEnabled(boolean enabled) {
		labelControl.setEnabled(enabled);
		textControl.setEnabled(enabled);
	}
	
}