/*******************************************************************************
 * Copyright (c) 2015, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.core.operations;

import melnorme.lang.ide.core.LangCore_Actual;
import mmrnmhrm.core.build.DeeBuildManager;


public class BuildManager {
	
	private static final DeeBuildManager instance = LangCore_Actual.createBuildManager();
	
	public static DeeBuildManager getInstance() {
		return instance;
	}
	
	/* -----------------  ----------------- */
	
	protected final BuildTarget[] buildConfigs;
	
	public BuildManager(BuildTarget[] buildConfigs) {
		this.buildConfigs = buildConfigs;
	}
	
}