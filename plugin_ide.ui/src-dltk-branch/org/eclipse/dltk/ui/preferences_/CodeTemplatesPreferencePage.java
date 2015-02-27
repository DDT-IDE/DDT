/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.preferences_;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.internal.ui.dialogs.StatusUtil;
import org.eclipse.dltk.internal.ui.preferences.PropertyAndPreferencePage;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.preferences.PreferencesMessages;
import org.eclipse.dltk.ui.text.templates.ICodeTemplateArea;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

/*
 * The page to configure the code templates.
 * 
 * TODO extends AbstractConfigurationBlockPropertyAndPreferencePage?
 */
public abstract class CodeTemplatesPreferencePage extends
		PropertyAndPreferencePage {

	public static final String DATA_SELECT_TEMPLATE = "CodeTemplatePreferencePage.select_template"; //$NON-NLS-1$

	private final IDLTKUILanguageToolkit toolkit;
	private final ICodeTemplateArea codeTemplateArea;

	private CodeTemplateBlock fCodeTemplateConfigurationBlock;

	protected CodeTemplatesPreferencePage(IDLTKUILanguageToolkit toolkit,
			ICodeTemplateArea codeTemplateArea) {
		this.toolkit = toolkit;
		this.codeTemplateArea = codeTemplateArea;
		setTitle(PreferencesMessages.CodeTemplatesPreferencePage_title);
	}

	/*
	 * @see PreferencePage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
		fCodeTemplateConfigurationBlock = new CodeTemplateBlock(
				getNewStatusChangedListener(), getProject(), container,
				toolkit, codeTemplateArea.getTemplateAccess());

		super.createControl(parent);
		// TODO PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
		// IJavaHelpContextIds.CODE_TEMPLATES_PREFERENCE_PAGE);
	}

	/*
	 * @see PropertyAndPreferencePage#createPreferenceContent(Composite)
	 */
	@Override
	protected Control createPreferenceContent(Composite composite) {
		return fCodeTemplateConfigurationBlock.createContents(composite);
	}

	/*
	 * @see PropertyAndPreferencePage#enableProjectSpecificSettings(boolean)
	 */
	@Override
	protected void enableProjectSpecificSettings(
			boolean useProjectSpecificSettings) {
		super.enableProjectSpecificSettings(useProjectSpecificSettings);
		if (fCodeTemplateConfigurationBlock != null) {
			fCodeTemplateConfigurationBlock
					.useProjectSpecificSettings(useProjectSpecificSettings);
		}
	}

	/*
	 * @see IPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		if (fCodeTemplateConfigurationBlock != null) {
			return fCodeTemplateConfigurationBlock
					.performOk(useProjectSettings());
		}
		return true;
	}

	/*
	 * @see PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		super.performDefaults();
		if (fCodeTemplateConfigurationBlock != null) {
			fCodeTemplateConfigurationBlock.performDefaults();
		}
	}

	/*
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	@Override
	public void dispose() {
		if (fCodeTemplateConfigurationBlock != null) {
			fCodeTemplateConfigurationBlock.dispose();
		}
		super.dispose();
	}

	/*
	 * @see IStatusChangeListener#statusChanged(IStatus)
	 */
	public void statusChanged(IStatus status) {
		setValid(!status.matches(IStatus.ERROR));
		StatusUtil.applyToStatusLine(this, status);
	}

	/*
	 * @see org.eclipse.jface.preference.IPreferencePage#performCancel()
	 */
	@Override
	public boolean performCancel() {
		if (fCodeTemplateConfigurationBlock != null) {
			fCodeTemplateConfigurationBlock.performCancel();
		}
		return super.performCancel();
	}

	/*
	 * @see
	 * PropertyAndPreferencePage#hasProjectSpecificOptions(org.eclipse.core.
	 * resources.IProject)
	 */
	@Override
	protected boolean hasProjectSpecificOptions(IProject project) {
		return fCodeTemplateConfigurationBlock
				.hasProjectSpecificOptions(project);
	}

	/*
	 * @see PreferencePage#applyData(java.lang.Object)
	 */
	@Override
	public void applyData(Object data) {
		if (data instanceof Map) {
			Object id = ((Map) data).get(DATA_SELECT_TEMPLATE);
			if (id instanceof String) {
				final TemplatePersistenceData[] templates = fCodeTemplateConfigurationBlock.fTemplateStore
						.getTemplateData();
				for (int index = 0; index < templates.length; index++) {
					TemplatePersistenceData template = templates[index];
					if (id.equals(template.getId())
							|| id.equals(template.getTemplate().getName())) {
						fCodeTemplateConfigurationBlock
								.postSetSelection(template);
						break;
					}
				}
			}
		}
		super.applyData(data);
	}

	/*
	 * @see PropertyAndPreferencePage#getPreferencePageId()
	 */
	@Override
	protected String getPreferencePageId() {
		return codeTemplateArea.getTemplatePreferencePageId();
	}

	/*
	 * @see PropertyAndPreferencePage#getPropertyPageId()
	 */
	@Override
	protected String getPropertyPageId() {
		return codeTemplateArea.getTemplatePropertyPageId();
	}

}
