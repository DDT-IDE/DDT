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

import static melnorme.utilbox.core.CoreUtil.array;

import java.io.IOException;

import melnorme.lang.ide.core.utils.process.IExternalProcessListener;
import melnorme.lang.ide.ui.utils.ConsoleUtils;
import melnorme.lang.ide.ui.utils.AbstractProcessMessageConsole;
import melnorme.util.swt.jface.ColorManager;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.process.ExternalProcessNotifyingHelper;
import melnorme.utilbox.process.ExternalProcessNotifyingHelper.IProcessOutputListener;
import mmrnmhrm.core.projectmodel.DubProcessManager.IDubOperation;
import mmrnmhrm.core.projectmodel.DubProcessManager.IDubProcessListener;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.DeeUIMessages;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IOConsoleOutputStream;

public class DubCommandsConsoleListener implements IDubProcessListener {
	
	public DubCommandsConsoleListener() {
	}
	
	protected static ISharedTextColors getColorManager() {
		return ColorManager.getDefault();
	}
	
	public static DubCommandsConsole createOperationConsole(String name, boolean recreateConsole) {
		DubCommandsConsole console = ConsoleUtils.findConsole(name, DubCommandsConsole.class);
		if(console != null) {
			if(!recreateConsole){
				return console;
			}
			ConsolePlugin.getDefault().getConsoleManager().removeConsoles(array(console));
		}
		// create a new one
		console = new DubCommandsConsole(name);
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(array(console));
		return console;
	}
	
	protected DubCommandsConsole getConsoleForOperation(IProject project, boolean clearConsole) {
		String consoleName = DeeUIMessages.DUB_CONSOLE_NAME + " " + getConsoleQualifier(project);
		// We recreate a message console to have a clear console. 
		// console.clearConsole() is not used because of poor concurrency behavior: if more than one cleanConsole
		// is requested per a console lifetime, these aditional clears may appear out of order with regards
		// to input written to the console output streams.
		// since org.eclipse.ui.console_3.5.200.v20130514-0954
		boolean recreateConsole = clearConsole;
		return createOperationConsole(consoleName, recreateConsole);
	}
	
	protected String getConsoleQualifier(IProject project) {
		if(project == null) {
			return "(Global)";
		}
		return "["+ project.getName() +"]";
	}
	
	public static class DubCommandsConsole extends AbstractProcessMessageConsole {
		
		public final IOConsoleOutputStream metaOut;
		
		public DubCommandsConsole(String name) {
			super(name, DeePluginImages.DUB_PROCESS.getDescriptor());
			
			metaOut = newOutputStream();
			
			post_initOutputStreamColors();
		}
		
		@Override
		protected void ui_initOutputStreamColors() {
			metaOut.setColor(getColorManager().getColor(new RGB(0, 0, 180)));
			stdErr.setColor(getColorManager().getColor(new RGB(200, 0, 0)));
		}
		
	}
	
	@Override
	public void handleDubOperationStarted(IDubOperation dubOperation) {
		final DubCommandsConsole console = getConsoleForOperation(dubOperation.getProject(), true);
		try {
			console.metaOut.write("************  " + dubOperation.getOperationName() + "  ************\n");
		} catch (IOException e) {
			return;
		}
		
		dubOperation.addExternalProcessListener(new IExternalProcessListener() {
			
			@Override
			public void handleProcessStarted(ProcessBuilder pb, IProject project, 
					ExternalProcessNotifyingHelper processHelper) {
				try {
					writeProcessDescription(pb, console);
					
					DubProcessOutputToConsoleListener outputListener = new DubProcessOutputToConsoleListener(console);
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
					console.metaOut.write(">>>  Failed to start process, exception: \n");
					console.metaOut.write(processStartException.getMessage());
				} catch (IOException consoleIOE) {
					return;
				}
			}
		});
	}
	
	protected void writeProcessDescription(ProcessBuilder pb, DubCommandsConsole console) throws IOException {
		console.metaOut.write(StringUtil.collToString(pb.command(), " ") + "\n");
		console.metaOut.write("@ " + pb.directory() +"\n");
	}
	
	public static class DubProcessOutputToConsoleListener implements IProcessOutputListener {
		
		private final DubCommandsConsole console;
		
		public DubProcessOutputToConsoleListener(DubCommandsConsole console) {
			this.console = console;
		}
		
		@Override
		public void notifyStdOutListeners(byte[] buffer, int offset, int readCount) {
			try {
				console.stdOut.write(buffer, offset, readCount);
			} catch (IOException e) {
				// Ignore, it could simply mean the console page has been closed
			}
		}
		
		@Override
		public void notifyStdErrListeners(byte[] buffer, int offset, int readCount) {
			try {
				console.stdErr.write(buffer, offset, readCount);
			} catch (IOException e) {
				// Ignore, it could simply mean the console page has been closed
			}		
		}
		
		@Override
		public void notifyProcessTerminatedAndRead(int exitCode) {
			try {
				console.stdOut.flush();
				console.stdErr.flush();
				console.metaOut.write("--------  Terminated, exit code: " + exitCode +  "  --------\n");
				console.metaOut.flush();
			} catch (IOException e) {
				// Ignore
			}
		}
		
	}
	
}
