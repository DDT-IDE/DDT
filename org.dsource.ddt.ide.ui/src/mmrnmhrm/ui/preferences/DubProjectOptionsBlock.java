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

import melnorme.util.swt.components.AbstractComponentExt;
import melnorme.util.swt.components.fields.TextField;
import mmrnmhrm.core.DeeCorePreferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class DubProjectOptionsBlock extends AbstractComponentExt {
	
	protected IProject project;
	
	protected final TextField dubBuildExtraOptions = new TextField("Extra build options for dub build:") {
		@Override
		protected Text createText(Composite parent) {
			return new Text(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		}
	};
	
	public DubProjectOptionsBlock() {
	}
	
	@Override
	protected void createContents(Composite topControl) {
		dubBuildExtraOptions.createComponentInlined(topControl);
		dubBuildExtraOptions.getFieldControl().setLayoutData(
			GridDataFactory.fillDefaults().grab(true, true).hint(200, SWT.DEFAULT).create());
	}
	
	@Override
	public void updateComponentFromInput() {
		if(project != null) {
			dubBuildExtraOptions.setFieldValue(DeeCorePreferences.getDubBuildOptions(project));
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
		DeeCorePreferences.putDubBuildOptions(project, dubBuildExtraOptions.getFieldValue());
		return true;
	}
	
	public void restoreDefaults() {
		updateComponentFromInput();
	}
	
}