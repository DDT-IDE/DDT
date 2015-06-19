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
package mmrnmhrm.core.build;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.nio.file.Path;

import melnorme.lang.ide.core.operations.IBuildTargetOperation;
import melnorme.lang.ide.core.operations.LangProjectBuilderExt;
import melnorme.lang.tooling.data.PathValidator;
import melnorme.utilbox.core.CommonException;
import mmrnmhrm.core.DeeCorePreferences;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;


public class DubProjectBuilder extends LangProjectBuilderExt {
	
	@Override
	public Path getBuildToolPath() throws CommonException {
		String pathString = DeeCorePreferences.getEffectiveDubPath();
		return getBuildToolPath(pathString);
	}
	
	@Override
	protected PathValidator getBuildToolPathValidator() {
		return new DubLocationValidator();
	}
	
	@Override
	protected void handleBeginWorkspaceBuild() {
		// No notification
	}
	
	@Override
	protected void handleEndWorkspaceBuild() {
		// No notification
	}
	
	/* ----------------- clean ----------------- */
	
	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException {
		IFolder dubCacheFolder = getProject().getFolder(".dub");
		if(dubCacheFolder.exists()) {
			dubCacheFolder.delete(true, monitor);
		}
		deleteProjectBuildMarkers();
	}
	
	@Override
	protected ProcessBuilder createCleanPB() throws CoreException, CommonException {
		throw assertFail();
	}
	
	/* ----------------- Build ----------------- */
	
	@Override
	protected IBuildTargetOperation createBuildOp() {
		return DeeBuildManager.getInstance().getBuildOperation(getProject(), this);
	}
	
}