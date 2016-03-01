/*******************************************************************************
 * Copyright (c) 2016 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.preferences.pages;

import melnorme.lang.ide.core.DeeToolPreferences;
import melnorme.lang.ide.ui.preferences.LangSDKConfigBlock;
import melnorme.lang.ide.ui.preferences.PreferencesMessages;
import melnorme.lang.ide.ui.preferences.common.PreferencesPageContext;
import melnorme.util.swt.components.AbstractGroupWidget;
import melnorme.util.swt.components.fields.ButtonTextField;
import melnorme.util.swt.components.fields.CheckBoxField;
import melnorme.util.swt.components.fields.FileTextField;
import mmrnmhrm.core.build.DubLocationValidator;

public class DeeSDKConfigBlock extends LangSDKConfigBlock {
	
	public DeeSDKConfigBlock(PreferencesPageContext prefContext) {
		super(prefContext);
		
		addSubComponent(new DeeFmtLocationGroup());
	}
	
	@Override
	protected DubLocationValidator getSDKValidator() {
		return new DubLocationValidator();
	}
	
	@Override
	protected LanguageSDKLocationGroup init_createSDKLocationGroup() {
		return new LanguageSDKLocationGroup() {
			@Override
			protected ButtonTextField createSdkLocationField() {
				return new FileTextField(PreferencesMessages.ROOT_SDKGroup_path_Label);
			}
		};
	}
	
	
	public class DeeFmtLocationGroup extends AbstractGroupWidget {
		
		public DeeFmtLocationGroup() {
			super("dfmt:", 3);
			
			ButtonTextField toolLocationField = new FileTextField("Path:");
			bindToDerivedPreference(toolLocationField, DeeToolPreferences.DFMT_PATH);
			addSubComponent(toolLocationField);
			
			CheckBoxField formatOnSaveField = new CheckBoxField(
					"Format automatically on editor save.");
			prefContext.bindToPreference(formatOnSaveField, DeeToolPreferences.FORMAT_ON_SAVE);
			addSubComponent(formatOnSaveField);
		}
		
	}
	
}