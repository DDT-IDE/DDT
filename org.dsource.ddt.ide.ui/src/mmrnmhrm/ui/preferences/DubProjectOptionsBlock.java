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

import melnorme.lang.jdt.ui.wizards.dialogfields.DialogField;
import melnorme.lang.jdt.ui.wizards.dialogfields.FieldLayoutUtilExt;
import melnorme.lang.jdt.ui.wizards.dialogfields.IDialogFieldListener;
import melnorme.lang.jdt.ui.wizards.dialogfields.StringDialogField;
import melnorme.util.swt.SWTLayoutUtil;
import mmrnmhrm.core.DeeCorePreferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;

public class DubProjectOptionsBlock {
	
	private final class FieldListener implements IDialogFieldListener {
		@Override
		public void dialogFieldChanged(DialogField field) {
		}
	}
	
	protected final StringDialogField fExtraOptions;
	
	protected IProject project;
	
//	protected TextComponent textComponent;
	
	public DubProjectOptionsBlock() {
		fExtraOptions = new StringDialogField() {
			@Override
			protected Text createTextControl(Composite parent) {
				return new Text(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
			}
		};
		fExtraOptions.setLabelText("Extra build options for dub build:");
		fExtraOptions.setDialogFieldListener(new FieldListener());
		
//		textComponent = new TextComponent("Extra build options for dub build:") {
//			@Override
//			protected Text createText(Composite parent) {
//				return new Text(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
//			}
//		};
	}
	
	public void initializeFrom(IProject project) {
		this.project = project;
		String dubBuildOptions = DeeCorePreferences.getDubBuildOptions(project);
		
		fExtraOptions.setTextWithoutUpdate(dubBuildOptions);
	}
	
	public Composite createControl(Composite parent) {
		
		Composite content = new Composite(parent, SWT.NONE);
		content.setLayout(SWTLayoutUtil.createGridLayout(2));
		
		FieldLayoutUtilExt.doDefaultLayout2(content, true, fExtraOptions);
		
		fExtraOptions.getTextControl(null).setLayoutData(
			GridDataFactory.fillDefaults().grab(true, true).hint(200, SWT.DEFAULT).create());
		
//		textComponent.createComponentInlined(parent);
//		textComponent.getTextControl().setLayoutData(
//			GridDataFactory.fillDefaults().grab(true, true).hint(200, SWT.DEFAULT).create());
		
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