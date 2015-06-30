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

import dtool.dub.BundlePath;
import melnorme.lang.ide.core.operations.BuildTarget;
import melnorme.lang.ide.core.project_model.BuildManager;
import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.utilbox.collections.ArrayList2;

public class DeeBuildManager extends BuildManager {
	
	public DeeBuildManager() {
		super();
	}
	
	@Override
	protected ManagerResourceListener init_createResourceListener() {
		return new ManagerResourceListener(EclipseUtils.epath(BundlePath.DUB_MANIFEST_Path));
	}
	
	@Override
	protected ArrayList2<BuildTarget> createDefaultProjectBuildInfo() {
		return ArrayList2.create(
			new BuildTarget(true, null),
			new BuildTarget(true, DubBuildType.UNITTEST.getBuildTypeString())
		);
	}
	
}