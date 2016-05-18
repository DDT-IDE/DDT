/*******************************************************************************
 * Copyright (c) 2016 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.util.swt.components;

import melnorme.utilbox.fields.IFieldView;
import melnorme.utilbox.fields.validation.ValidationSource;
import melnorme.utilbox.status.IStatusMessage;

public interface IValidatableWidget extends IWidgetComponent, ValidationSource {
	
	@Override
	public default IStatusMessage getValidationStatus() {
		return getStatusField().getFieldValue();
	}
	
	IFieldView<IStatusMessage> getStatusField();
	
}