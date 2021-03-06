/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.wizards;

import org.junit.After;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.ui.dialogs.LangProjectWizardTest;


public class DeeProjectWizardTest extends LangProjectWizardTest {
	
	@Override
	protected DeeProjectWizard createNewProjectWizard() {
		return new DeeProjectWizard();
	}
	
	@Override
	@After
	public void tearDown() throws Exception {
		LangCore.deeBundleModelManager().syncPendingUpdates(); // ensure DUB process finished
		super.tearDown();
	}
	
}