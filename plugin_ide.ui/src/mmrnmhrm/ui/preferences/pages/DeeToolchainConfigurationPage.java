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
package mmrnmhrm.ui.preferences.pages;

import org.eclipse.core.resources.IProject;

import melnorme.lang.ide.core.operations.ToolchainPreferences;
import melnorme.lang.ide.ui.dialogs.AbstractLangPropertyPage;
import melnorme.lang.ide.ui.preferences.LangSDKConfigBlock.LanguageSDKLocationGroup;
import melnorme.lang.ide.ui.preferences.ProjectSDKSettingsBlock;
import melnorme.lang.ide.ui.preferences.common.IPreferencesWidgetComponent;

public class DeeToolchainConfigurationPage extends AbstractLangPropertyPage {
	
	@Override
	protected IPreferencesWidgetComponent createProjectConfigComponent(IProject project) {
		return new ProjectSDKSettingsBlock(project, 
			ToolchainPreferences.USE_PROJECT_SETTINGS, 
			ToolchainPreferences.SDK_PATH.getProjectPreference()) {
			@Override
			protected LanguageSDKLocationGroup init_createSDKLocationGroup() {
				return new LanguageSDKLocationGroup();
			}
		};
	}
	
}