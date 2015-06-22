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

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.LangCore_Actual;
import melnorme.lang.ide.core.operations.CompositeBuildOperation;
import melnorme.lang.ide.core.operations.IBuildTargetOperation;
import melnorme.lang.ide.core.operations.OperationInfo;
import melnorme.utilbox.collections.ArrayList2;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

public class DeeBuildManager {
	
	private static final DeeBuildManager instance = new DeeBuildManager();
	
	public static DeeBuildManager getInstance() {
		return instance;
	}
	
	public IBuildTargetOperation getBuildOperation(IProject project, DubProjectBuilder projectBuilder) {
		
		String startMsg = headerBIG(" Building " + LangCore_Actual.LANGUAGE_NAME + " project: " + project.getName());
		String endMsg = headerBIG("Build terminated.");
		
		return new CompositeBuildOperation(project, projectBuilder, ArrayList2.create(
			getOperationTask(startMsg, true),
			new DubBuildOperation(project, projectBuilder, null, null),
			new DubBuildOperation(project, projectBuilder, null, DubBuildType.UNITTEST),
			getOperationTask(endMsg, false)
		));
		
	}
	
	protected IBuildTargetOperation getOperationTask(String msg, boolean clearConsole) {
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