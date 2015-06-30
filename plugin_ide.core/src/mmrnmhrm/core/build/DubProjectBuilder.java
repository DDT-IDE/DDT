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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import melnorme.lang.ide.core.operations.BuildTarget;
import melnorme.lang.ide.core.operations.IBuildTargetOperation;
import melnorme.lang.ide.core.operations.LangBuildManagerProjectBuilder;
import melnorme.lang.ide.core.operations.LangProjectBuilder;
import melnorme.lang.tooling.data.PathValidator;
import melnorme.utilbox.core.CommonException;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCorePreferences;


public class DubProjectBuilder extends LangBuildManagerProjectBuilder {
	
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
	protected IBuildTargetOperation newBuildOperation(IProject project, LangProjectBuilder projectBuilder,
			BuildTarget buildConfig) {
		return new DubBuildOperation(project, projectBuilder, null, buildConfig.getTargetName());
	}
	
	@Override
	protected IBuildTargetOperation newOperationMessageTask(String msg, boolean clearConsole) {
		return new BuildMessageOperation(clearConsole, msg) {
			@Override
			protected void executeDo() {
				// Run message output in dub process manager
				DeeCore.getDubProcessManager().submitDubCommand(this);
			}
		};
	}
}