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

import melnorme.util.swt.components.AbstractComponent;
import melnorme.util.swt.components.fields.TextComponent;
import mmrnmhrm.core.DeeCorePreferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class DubProjectOptionsBlock extends AbstractComponent {
	
	protected IProject project;
	
	protected final TextComponent dubBuildExtraOptions = new TextComponent("Extra build options for dub build:") {
		@Override
		protected Text createText(Composite parent) {
			return new Text(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		}
	};
	
	public DubProjectOptionsBlock() {
	}
	
	public void initializeFrom(IProject project) {
		this.project = project;
		
		updateFromInput();
	}
	
	protected void updateFromInput() {
		if(dubBuildExtraOptions.isCreated()) {
			dubBuildExtraOptions.setValue(DeeCorePreferences.getDubBuildOptions(project));
		}
	}
	
	@Override
	protected void createContents(Composite topControl) {
		dubBuildExtraOptions.createComponentInlined(topControl);
		dubBuildExtraOptions.getTextControl().setLayoutData(
			GridDataFactory.fillDefaults().grab(true, true).hint(200, SWT.DEFAULT).create());
		
		if(project != null) {
			updateFromInput();
		}
	}
	
	public boolean performOk() {
		if(project == null) {
			return false;
		}
		DeeCorePreferences.putDubBuildOptions(project, dubBuildExtraOptions.getValue());
		return true;
	}
	
	public void restoreDefaults() {
		if(project != null) {
			dubBuildExtraOptions.setValue(DeeCorePreferences.getDubBuildOptionsDefault());
			updateFromInput();
		}
	}
	
}