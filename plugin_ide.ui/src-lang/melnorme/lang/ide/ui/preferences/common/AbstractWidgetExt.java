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
package melnorme.lang.ide.ui.preferences.common;


import melnorme.lang.tooling.data.IStatusMessage;
import melnorme.lang.tooling.data.validation.ValidationField;
import melnorme.util.swt.components.AbstractWidget;
import melnorme.util.swt.components.IValidatableWidget;
import melnorme.utilbox.fields.IFieldView;

public abstract class AbstractWidgetExt extends AbstractWidget implements IValidatableWidget {
	
	protected final ValidationField validation = new ValidationField();
	
	public AbstractWidgetExt() {
		super();
	}
	
	@Override
	public final IStatusMessage getValidationStatus() {
		return IValidatableWidget.super.getValidationStatus();
	}
	
	@Override
	public final IFieldView<IStatusMessage> getStatusField() {
		return validation;
	}
	
	public ValidationField getValidation() {
		return validation;
	}
	
}