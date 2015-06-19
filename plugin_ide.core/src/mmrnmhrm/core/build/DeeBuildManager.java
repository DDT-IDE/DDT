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
package mmrnmhrm.core.build;

import melnorme.lang.ide.core.operations.CompositeBuildOperation;
import melnorme.lang.ide.core.operations.IBuildTargetOperation;
import melnorme.utilbox.collections.ArrayList2;

import org.eclipse.core.resources.IProject;

public class DeeBuildManager {
	
	private static final DeeBuildManager instance = new DeeBuildManager();
	
	public static DeeBuildManager getInstance() {
		return instance;
	}
	
	public IBuildTargetOperation getBuildOperation(IProject project, DubProjectBuilder projectBuilder) {
		return new CompositeBuildOperation(project, projectBuilder, ArrayList2.create(
			new DubBuildOperation(project, projectBuilder, null, null)
//			,
//			new DubBuildOperation(project, projectBuilder, null, DubBuildType.UNITTEST)
		));
	}
	
}