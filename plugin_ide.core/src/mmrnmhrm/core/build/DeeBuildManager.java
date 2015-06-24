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

import static melnorme.lang.ide.core.operations.TextMessageUtils.headerBIG;

import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.LangCore_Actual;
import melnorme.lang.ide.core.operations.BuildManager;
import melnorme.lang.ide.core.operations.BuildTarget;
import melnorme.lang.ide.core.operations.CompositeBuildOperation;
import melnorme.lang.ide.core.operations.IBuildTargetOperation;
import melnorme.lang.ide.core.operations.OperationInfo;
import melnorme.utilbox.collections.ArrayList2;
import mmrnmhrm.core.DeeCore;

public class DeeBuildManager extends BuildManager {
	
	public DeeBuildManager() {
		super(ArrayList2.create(
			new BuildTarget(true, null),
			new BuildTarget(true, DubBuildType.UNITTEST.getBuildTypeString())
		));
	}
	
	protected IBuildTargetOperation newBuildOperation(IProject project, DubProjectBuilder projectBuilder,
			BuildTarget buildConfig) {
		return new DubBuildOperation(project, projectBuilder, null, buildConfig.getTargetName());
	}
	
	public IBuildTargetOperation getBuildOperation(IProject project, DubProjectBuilder projectBuilder) {
		
		ArrayList2<IBuildTargetOperation> operations = ArrayList2.create();
		
		String startMsg = headerBIG(" Building " + LangCore_Actual.LANGUAGE_NAME + " project: " + project.getName());
		operations.add(newOperationMessageTask(startMsg, true));
		
		for (BuildTarget buildConfig : buildConfigs) {
			if(buildConfig.isEnabled()) {
				operations.add(newBuildOperation(project, projectBuilder, buildConfig));
			}
		}
		
		operations.add(newOperationMessageTask(
			headerBIG("Build terminated."), false));
		
		return new CompositeBuildOperation(project, projectBuilder, operations);
	}
	
	protected IBuildTargetOperation newOperationMessageTask(String msg, boolean clearConsole) {
		return new IBuildTargetOperation() {
			
			@Override
			public IProject[] execute(IProject project, int kind, Map<String, String> args, IProgressMonitor monitor) {
				DeeCore.getDubProcessManager().submitDubCommand(new Callable<OperationInfo>() {
					@Override
					public OperationInfo call() throws Exception {
						OperationInfo opInfo = new OperationInfo(project, clearConsole, msg);
						LangCore.getToolManager().notifyOperationStarted(opInfo);
						return opInfo;
					}
				});
				return null;
			}
		};
	}
	
}