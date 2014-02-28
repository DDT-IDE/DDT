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

import melnorme.util.swt.GridComposite;
import melnorme.util.swt.SWTLayoutUtil;
import melnorme.util.ui.fields.FieldUtil;
import melnorme.util.ui.fields.StringDialogField;
import mmrnmhrm.core.DeeCorePreferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.prefs.BackingStoreException;

public class DeeProjectBuildOptionsBlock {
	
	private final class FieldListener implements IDialogFieldListener {
		@Override
		public void dialogFieldChanged(DialogField field) {
		}
	}
	
	protected final StringDialogField fExtraOptions;
	
	protected IProject project;
	
	public DeeProjectBuildOptionsBlock() {
		fExtraOptions = new StringDialogField(SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		fExtraOptions.setLabelText("Extra build options for dub build:");
		fExtraOptions.setDialogFieldListener(new FieldListener());
	}
	
	public void initializeFrom(IScriptProject scriptProject) {
		project = scriptProject.getProject();
		String dubBuildOptions = DeeCorePreferences.getDubBuildOptions(project);
		
		fExtraOptions.setTextWithoutUpdate(dubBuildOptions);
	}
	
	public Composite createControl(Composite parent) {
		Composite content = parent;
		content = new GridComposite(parent);
		
		GridComposite rowComposite = new GridComposite(content);
		SWTLayoutUtil.setWidthHint(rowComposite, 200);
		SWTLayoutUtil.enableDiagonalExpand(rowComposite);

		Composite comp;
		
		comp = FieldUtil.createCompose(rowComposite, true, fExtraOptions);
		SWTLayoutUtil.enableDiagonalExpand(comp);
		SWTLayoutUtil.enableDiagonalExpand(fExtraOptions.getTextControl(null));
		SWTLayoutUtil.setHeightHint(fExtraOptions.getTextControl(null), 200);
		
		return content;
	}
	
	public boolean hasBeenInitialized() {
		return project != null;
	}
	
	public boolean performOk() {
		DeeCorePreferences.putDubBuildOptions(project, fExtraOptions.getText());
		try {
			DeeCorePreferences.getProjectPreferences(project).flush();
		} catch (BackingStoreException e) {
			return false;
		}
		return true;
	}
	
}