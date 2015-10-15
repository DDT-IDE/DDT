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

import melnorme.lang.ide.ui.preferences.LangRootPreferencePage;
import melnorme.lang.ide.ui.preferences.LangSDKConfigBlock;
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
	protected LangSDKConfigBlock init_createLangSDKConfigBlock() {
		return new DeeSDKConfigBlock();
	}
	
	public static class DeeSDKConfigBlock extends LangSDKConfigBlock {
		
		@Override
		protected DubLocationValidator getSDKValidator() {
			return new DubLocationValidator();
		}
		
		@Override
		protected LanguageSDKLocationGroup createSDKLocationGroup2() {
			return new LanguageSDKLocationGroup() {
				@Override
				protected ButtonTextField createSdkLocationField() {
					return new FileTextField(PreferencesMessages.ROOT_SDKGroup_path_Label);
				}
			};
		}
	}
	
}