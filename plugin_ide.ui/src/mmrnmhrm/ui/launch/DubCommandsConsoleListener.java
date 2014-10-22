/*******************************************************************************
 * Copyright (c) 2014, 2014 IBM Corporation and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.launch;

import java.io.IOException;

import melnorme.lang.ide.core.utils.process.IStartProcessListener;
import melnorme.lang.ide.ui.tools.console.AbstractToolsConsoleListener;
import melnorme.lang.ide.ui.tools.console.ToolsConsole;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.process.ExternalProcessNotifyingHelper;
import mmrnmhrm.core.engine_client.DubProcessManager.IDubOperation;
import mmrnmhrm.core.engine_client.IDubProcessListener;
import mmrnmhrm.ui.DeeImages;
import mmrnmhrm.ui.DeeUIMessages;

import org.eclipse.core.resources.IProject;

public class DubCommandsConsoleListener extends AbstractToolsConsoleListener implements IDubProcessListener {
	
	public DubCommandsConsoleListener() {
	}
	
	@Override
	protected String getOperationConsoleName(IProject project) {
		return DeeUIMessages.DUB_CONSOLE_NAME + " " + getProjectNameSuffix(project);
	}
	
	@Override
	protected ToolsConsole createConsole(String name) {
		return new DubCommandsConsole(name);
	}
	
	public static class DubCommandsConsole extends ToolsConsole {
		
		public DubCommandsConsole(String name) {
			super(name, DeeImages.DUB_PROCESS.getDescriptor());
		}
		
	}
	
	@Override
	public void handleDubOperationStarted(IDubOperation dubOperation) {
		final ToolsConsole console = getOperationConsole(dubOperation.getProject(), true);
		try {
			console.infoOut.write("************  " + dubOperation.getOperationName() + "  ************\n");
		} catch (IOException e) {
			return;
		}
		
		dubOperation.addExternalProcessListener(new IStartProcessListener() {
			
			@Override
			public void handleProcessStartResult(ProcessBuilder pb, IProject project,
					ExternalProcessNotifyingHelper processHelper, CommonException ce) {
				
				new ProcessUIConsoleHandler(pb, project, "> ", false, processHelper, ce) {
					@Override
					protected ToolsConsole getConsole() {
						return console;
					}
				};
			}
			
		});
	}
	
	@Override
	public void engineDaemonStart(ProcessBuilder pb, CommonException ce, ExternalProcessNotifyingHelper processHelper) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void engineClientToolStart(ProcessBuilder pb, CommonException ce,
			ExternalProcessNotifyingHelper processHelper) {
		// TODO Auto-generated method stub
	}
	
}