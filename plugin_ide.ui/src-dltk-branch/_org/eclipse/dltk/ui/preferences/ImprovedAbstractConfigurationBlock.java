/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/

package _org.eclipse.dltk.ui.preferences;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import _org.eclipse.dltk.internal.ui.dialogs.StatusUtil;
import _org.eclipse.dltk.ui.dialogs.StatusInfo;
import _org.eclipse.dltk.ui.preferences.OverlayPreferenceStore.OverlayKey;
import _org.eclipse.dltk.ui.util.IStatusChangeListener;

/**
 * Configures preferences.
 */
public abstract class ImprovedAbstractConfigurationBlock implements
		IPreferenceConfigurationBlock, IPreferenceDelegate<String> {

	private PreferencePage page;
	private OverlayPreferenceStore store;

	private final ControlBindingManager<String> bindManager;

	public ImprovedAbstractConfigurationBlock(OverlayPreferenceStore store,
			final PreferencePage page) {
		this.page = page;
		this.store = store;

		bindManager = new ControlBindingManager<String>(this,
				getStatusListener());
		addOverlayKeys();
	}

	@Override
	public void initialize() {
		initializeFields();
	}

	@Override
	public void performOk() {
		// do nothing
	}

	@Override
	public void performDefaults() {
		initializeFields();
	}

	@Override
	public void dispose() {
		// do nothing
	}

	@Override
	public boolean getBoolean(String key) {
		return store.getBoolean(key);
	}

	@Override
	public String getString(String key) {
		return store.getString(key);
	}

	@Override
	public void setBoolean(String key, boolean value) {
		store.setValue(key, value);
	}

	@Override
	public void setString(String key, String value) {
		store.setValue(key, value);
	}

	/**
	 * Create the {@link OverlayPreferenceStore.OverlayKey} keys for the
	 * preference page.
	 * 
	 * <p>
	 * Subclasses may return <code>null</code> in then event they are not
	 * storing any preference values.
	 * </p>
	 */
	protected abstract List<OverlayKey> createOverlayKeys();

	// Binding

	protected void bindControl(final Button button, final String key,
			Object enable) {
		bindControl(button, key, enable, null);
	}

	protected void bindControl(final Button button, final String key,
			Object enable, Control[] dependencies) {
		bindManager.bindRadioControl(button, key, enable, dependencies);
	}

	protected void bindControl(final Button button, final String key,
			final Control[] dependencies) {
		bindManager.bindControl(button, key, dependencies);
	}

	protected void bindControl(final Button button, final String key) {
		bindControl(button, key, null);
	}

	protected void bindControl(final Text text, final String key,
			IFieldValidator validator) {
		bindManager.bindControl(text, key, validator);
	}

	protected void bindControl(final Text text, final String key,
			IFieldValidator validator, ITextConverter transformer) {
		bindManager.bindControl(text, key, validator, transformer);
	}

	protected void bindControl(final Text text, final String key) {
		bindControl(text, key, null);
	}

	protected void bindControl(final Text text, IFieldValidator validator) {
		bindManager.bindControl(text, null, validator);
	}

	protected void bindControl(final Combo combo, final String key) {
		bindManager.bindControl(combo, key);
	}

	protected void createDependency(Button master, Control[] slaves) {
		createDependency(master, slaves, null);
	}

	protected void createDependency(Button master, Control[] slaves,
			ControlBindingManager.DependencyMode mode) {
		bindManager.createDependency(master, slaves, mode);
	}

	protected void initializeFields() {
		bindManager.initialize();
	}

	protected IPreferenceStore getPreferenceStore() {
		return store;
	}

	protected PreferencePage getPreferencePage() {
		return page;
	}

	private IStatusChangeListener getStatusListener() {
		return new IStatusChangeListener() {

			@Override
			public void statusChanged(IStatus status) {
				if (status == null) {
					status = new StatusInfo();
				}

				page.setValid(status.getSeverity() != IStatus.ERROR);
				StatusUtil.applyToStatusLine(page, status);
			}
		};
	}

	private void addOverlayKeys() {
		List<OverlayKey> overlayKeys = createOverlayKeys();
		if (overlayKeys != null) {
			OverlayKey[] keys = overlayKeys.toArray(new OverlayKey[overlayKeys
					.size()]);
			store.addKeys(keys);
		}
	}
}
