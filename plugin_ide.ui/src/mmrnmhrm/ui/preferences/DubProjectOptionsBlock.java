/*******************************************************************************
 * Copyright (c) 2010, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.preferences;

import melnorme.lang.ide.ui.fields.ArgumentsGroupField;
import melnorme.lang.ide.ui.utils.UIOperationExceptionHandler;
import melnorme.util.swt.components.AbstractComponentExt;
import mmrnmhrm.core.DeeCorePreferences;
import mmrnmhrm.ui.DeeUIMessages;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.prefs.BackingStoreException;

public class DubProjectOptionsBlock extends AbstractComponentExt {
	
	protected IProject project;
	
	protected final ArgumentsGroupField dubBuildExtraOptions = new ArgumentsGroupField(
		DeeUIMessages.DUB_PROJECT_OPTIONS__ExtraBuildOptions
	);
	
	public DubProjectOptionsBlock() {
	}
	
	@Override
	protected void createContents(Composite topControl) {
		dubBuildExtraOptions.createComponent(topControl,
			GridDataFactory.fillDefaults().grab(true, false).hint(200, SWT.DEFAULT).create());
	}
	
	@Override
	public void updateComponentFromInput() {
		if(project != null) {
			dubBuildExtraOptions.setFieldValue(DeeCorePreferences.DUB_BUILD_OPTIONS.get(project));
		}
	}
	
	// Note this can be called before the component is created
	public void initializeFrom(IProject project) {
		this.project = project;
		updateComponentFromInput();
	}
	
	public boolean performOk() {
		if(project == null) {
			return false;
		}
		try {
			DeeCorePreferences.DUB_BUILD_OPTIONS.set(project, dubBuildExtraOptions.getFieldValue());
		} catch (BackingStoreException e) {
			UIOperationExceptionHandler.handleException(e, "Error saving preferences.");
		}
		return true;
	}
	
	public void restoreDefaults() {
		dubBuildExtraOptions.setFieldValue(DeeCorePreferences.DUB_BUILD_OPTIONS.getDefault());
	}
	
}