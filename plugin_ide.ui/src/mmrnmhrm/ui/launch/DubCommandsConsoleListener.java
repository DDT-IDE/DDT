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

import melnorme.lang.ide.core.utils.process.IExternalProcessListener;
import melnorme.lang.ide.ui.tools.console.AbstractToolsConsoleListener;
import melnorme.lang.ide.ui.tools.console.ProcessOutputToConsoleListener;
import melnorme.lang.ide.ui.tools.console.ToolsConsole;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.process.ExternalProcessNotifyingHelper;
import mmrnmhrm.core.engine_client.DubProcessManager.IDubOperation;
import mmrnmhrm.core.engine_client.DubProcessManager.IDubProcessListener;
import mmrnmhrm.ui.DeeImages;
import mmrnmhrm.ui.DeeUIMessages;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.RGB;

public class DubCommandsConsoleListener extends AbstractToolsConsoleListener implements IDubProcessListener {
	
	public DubCommandsConsoleListener() {
	}
	
	@Override
	protected String getOperationConsoleName(IProject project) {
		return DeeUIMessages.DUB_CONSOLE_NAME + " " + getProjectNameSuffix(project);
	}
	
	public static class DubCommandsConsole extends ToolsConsole {
		
		public DubCommandsConsole(String name) {
			super(name, DeeImages.DUB_PROCESS.getDescriptor());
		}
		
		@Override
		protected void ui_initStreamColors() {
			infoOut.setColor(getColorManager().getColor(new RGB(0, 0, 180)));
			stdErr.setColor(getColorManager().getColor(new RGB(200, 0, 0)));
		}
		
	}
	
	@Override
	public void handleDubOperationStarted(IDubOperation dubOperation) {
		final DubCommandsConsole console = getOperationConsole(dubOperation.getProject(), true);
		try {
			console.infoOut.write("************  " + dubOperation.getOperationName() + "  ************\n");
		} catch (IOException e) {
			return;
		}
		
		dubOperation.addExternalProcessListener(new IExternalProcessListener() {
			
			@Override
			public void handleProcessStarted(ProcessBuilder pb, IProject project, 
					ExternalProcessNotifyingHelper processHelper) {
				try {
					writeProcessDescription(pb, console);
					
					ProcessOutputToConsoleListener outputListener = new ProcessOutputToConsoleListener(console);
					processHelper.getOutputListenersHelper().addListener(outputListener);
				} catch (IOException e) {
					return;
				}
			}
			
			@Override
			public void handleProcessStartFailure(ProcessBuilder pb, IProject project, 
					IOException processStartException) {
				try {
					writeProcessDescription(pb, console);
					console.infoOut.write(">>>  Failed to start process, exception: \n");
					console.infoOut.write(processStartException.getMessage());
				} catch (IOException consoleIOE) {
					return;
				}
			}
		});
	}
	
	protected void writeProcessDescription(ProcessBuilder pb, DubCommandsConsole console) throws IOException {
		console.infoOut.write(StringUtil.collToString(pb.command(), " ") + "\n");
		console.infoOut.write("@ " + pb.directory() +"\n");
	}
	
}