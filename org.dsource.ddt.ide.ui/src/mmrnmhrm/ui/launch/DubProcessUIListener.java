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
import melnorme.lang.ide.ui.utils.ConsoleUtils;
import melnorme.utilbox.concurrency.ExternalProcessOutputHelper;
import melnorme.utilbox.concurrency.ExternalProcessOutputHelper.IProcessOutputListener;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.projectmodel.DubProcessManager.RunsInDubProcessAgent;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.DeeUIMessages;
import mmrnmhrm.ui.DeeUIPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.MessageConsole;

public class DubProcessUIListener implements IExternalProcessListener {
	
	protected static IColorManager getColorManager() {
		return DeeUIPlugin.getDefault().getTextTools().getColorManager();
	}
	
	@RunsInDubProcessAgent
	@Override
	public void handleProcessStarted(ProcessBuilder pb, IProject project,
			ExternalProcessOutputHelper processHelper) {
		String consoleQualifier = getConsoleQualifier(project); 
		final DubProcessUIConsoleOutputHandler outputHandler = createProcessOutputHandler(consoleQualifier);
		
		try {
			writeProcessStartPrefix(pb, outputHandler);
			
			processHelper.getOutputListeningHelper().addListener(outputHandler);
		} catch (IOException e) {
			return;
		}
	}

	protected DubProcessUIConsoleOutputHandler createProcessOutputHandler(String consoleQualifier) {
		final DubProcessUIConsoleOutputHandler outputListener = 
				new DubProcessUIConsoleOutputHandler(consoleQualifier);
		
		// BM: it's not clear to me if a Color can be created outside UI thread, so do asyncExec
		// I would think one cant, but some Platform code (ProcessConsole) does freely create Color instances
		// on the UI thread, so maybe the asyncExec is not necessary.
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				outputListener.metaOut.setColor(getColorManager().getColor(new RGB(0, 0, 180)));
				outputListener.stdErr.setColor(getColorManager().getColor(new RGB(200, 0, 0)));
			}
		});
		return outputListener;
	}
	
	protected void writeProcessStartPrefix(ProcessBuilder pb, final DubProcessUIConsoleOutputHandler outputHandler)
			throws IOException {
		outputHandler.metaOut.write("************  Running dub command  ************\n");
		outputHandler.metaOut.write(StringUtil.collToString(pb.command(), " ") + "\n");
		outputHandler.metaOut.write("@ " + pb.directory() +"\n");
	}
	
	@RunsInDubProcessAgent
	@Override
	public void handleProcessStartFailure(ProcessBuilder pb, IProject project,
			IOException processStartException) {
		String consoleQualifier = getConsoleQualifier(project); 
		final DubProcessUIConsoleOutputHandler outputHandler = createProcessOutputHandler(consoleQualifier);
		
		try {
			writeProcessStartPrefix(pb, outputHandler);
			outputHandler.metaOut.write(">>>  Failed to start process: \n");
			outputHandler.metaOut.write(processStartException.getMessage());
		} catch (IOException consoleIOE) {
			return;
		}
	}
	
	protected String getConsoleQualifier(IProject project) {
		if(project == null) {
			return "(Global)";
		}
		return "["+ project.getName() +"]";
	}
	
	public static class DubProcessUIConsoleOutputHandler implements IProcessOutputListener {
		
		private final MessageConsole console;
		private final IOConsoleOutputStream metaOut;
		private final IOConsoleOutputStream stdOut;
		private final IOConsoleOutputStream stdErr;
		
		public DubProcessUIConsoleOutputHandler(String consoleQualifier) {
			
			console = ConsoleUtils.recreateMessageConsole(DeeUIMessages.DUB_CONSOLE_NAME + " " + consoleQualifier, 
				DeePluginImages.getDescriptor(DeePluginImages.DUB_PROCESS));
			// We recreate a message console to have a clear console. 
			// console.clearConsole() is not used because of poor concurrency behavior: if more than one cleanConsole
			// is request per a console lifetime, these aditional clears may appear out of order with regards
			// to input written to the console output streams.
			// since org.eclipse.ui.console_3.5.200.v20130514-0954
			console.clearConsole();
			
			metaOut = console.newOutputStream();
			
			stdOut = console.newOutputStream();
			stdErr = console.newOutputStream();
			stdErr.setActivateOnWrite(true);
		}
		
		@Override
		public void notifyStdOutListeners(byte[] buffer, int offset, int readCount) {
			try {
				stdOut.write(buffer, offset, readCount);
			} catch (IOException e) {
				// Ignore, it could simply mean the console page has been closed
			}
		}
		
		@Override
		public void notifyStdErrListeners(byte[] buffer, int offset, int readCount) {
			try {
				stdErr.write(buffer, offset, readCount);
			} catch (IOException e) {
				// Ignore, it could simply mean the console page has been closed
			}		
		}
		
		@Override
		public void notifyProcessTerminatedAndRead(int exitCode) {
			try {
				stdOut.flush();
				stdErr.flush();
				metaOut.write("--------  Terminated, exit code: " + exitCode +  "  --------\n");
				metaOut.flush();
				
				stdOut.close();
				stdErr.close();
				metaOut.close();
			} catch (IOException e) {
				// Ignore
			}
		}
		
	}
	
}