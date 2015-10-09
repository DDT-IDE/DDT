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

import melnorme.lang.ide.core.operations.ToolchainPreferences;
import melnorme.lang.ide.ui.preferences.LangRootPreferencePage;
import melnorme.lang.ide.ui.preferences.LangSDKConfigBlock;
import melnorme.lang.ide.ui.preferences.LangSDKConfigBlock.LanguageSDKLocationGroup;
import melnorme.lang.ide.ui.preferences.PreferencesMessages;
import melnorme.util.swt.components.fields.ButtonTextField;
import melnorme.util.swt.components.fields.FileTextField;
import mmrnmhrm.core.build.DubLocationValidator;

/**
 * The root preference page for D
 */
public class DeeRootPreferencePage extends LangRootPreferencePage {
	
	public DeeRootPreferencePage() {
	}
	
	@Override
	protected String getHelpId() {
		return null;
	}
	
	@Override
	protected LangSDKConfigBlock createLangSDKConfigBlock() {
		LangSDKConfigBlock langSDKConfigBlock = new LangSDKConfigBlock() {
			@Override
			protected LanguageSDKLocationGroup createSDKLocationGroup() {
				return new DeeSDKLocationGroup();
			}
		};
		
		connectStringField(ToolchainPreferences.SDK_PATH.key, langSDKConfigBlock.getLocationField(), 
			getSDKValidator());
		
		return langSDKConfigBlock;
	}
	
	public static class DeeSDKLocationGroup extends LanguageSDKLocationGroup {
		@Override
		protected ButtonTextField createSdkLocationField() {
			return new FileTextField(PreferencesMessages.ROOT_SDKGroup_path_Label);
		}
	}
	
	@Override
	protected DubLocationValidator getSDKValidator() {
		return new DubLocationValidator();
	}
	
}