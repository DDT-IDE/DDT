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
package melnorme.util.swt.components;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.eclipse.swt.widgets.Control;

import melnorme.lang.ide.ui.preferences.common.AbstractWidgetExt;
import melnorme.util.swt.SWTUtil;

/**
 * An {@link AbstractWidget} extended with {@link #setEnabled(boolean)} functionality.
 */
public abstract class AbstractDisableableWidget extends AbstractWidgetExt 
	implements IDisableableWidget {
	
	protected AbstractDisableableWidget parentWidget;
	protected boolean enabled = true;
	public boolean onlyValidateWhenEnabled = true;
	
	public AbstractDisableableWidget() {
	}
	
	public void setParent(AbstractDisableableWidget parent) {
		assertTrue(this.parentWidget == null);
		this.parentWidget = assertNotNull(parent);
	}
	
	public boolean isEnabled() {
		return enabled && (parentWidget == null || parentWidget.isEnabled());
	}
	
	@Override
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
		updateControlEnablement2();
	}
	
	protected void updateControlEnablement2() {
		doSetEnabled(isEnabled());
		updateValidationStatusForEnablement();
	}
	
	protected abstract void doSetEnabled(boolean enabled);
	
	protected void updateValidationStatusForEnablement() {
		if(onlyValidateWhenEnabled) {
			if(!isEnabled()) {
				validation.set(null);
			} else {
				validation.updateFieldValue();
			}
		}
	}
	
	@Override
	public void updateWidgetFromInput() {
		updateControlEnablement2();
		doUpdateWidgetFromInput();
	}
	
	protected void doUpdateWidgetFromInput() {
	}
	
	/* -----------------  ----------------- */
	
	protected static void setControlEnabled(Control control, boolean enabled) {
		SWTUtil.setEnabledIfOk(control, enabled);
	}
	
}