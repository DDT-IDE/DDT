/*******************************************************************************
 * Copyright (c) 2014, 2014 IBM Corporation and others.
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

import melnorme.lang.ide.ui.dialogs.AbstractProjectPropertyPage;
import melnorme.lang.ide.ui.preferences.LangProjectOptionsBlock;
import mmrnmhrm.ui.DeeUIMessages;


public class DeeBuildConfigurationPropertyPage extends AbstractProjectPropertyPage {
	
	@Override
	protected LangProjectOptionsBlock createProjectOptionsComponent(IProject project) {
		return new LangProjectOptionsBlock(project) {
			@Override
			protected String init_getArgumentsField_Label() {
				return DeeUIMessages.DUB_PROJECT_OPTIONS__ExtraBuildOptions;
			}
		};
	}
	
}