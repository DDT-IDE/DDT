package mmrnmhrm.ui.launch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import melnorme.lang.ide.ui.utils.ConsoleUtils;
import melnorme.utilbox.concurrency.ExternalProcessOutputHelper;
import melnorme.utilbox.concurrency.ExternalProcessOutputHelper.IProcessOutputListener;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.projectmodel.IExternalProcessListener;
import mmrnmhrm.core.projectmodel.DubProcessManager.RunsInDubProcessAgent;
import mmrnmhrm.ui.DeePluginImages;

import org.dsource.ddt.ui.DeeUIPlugin;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.MessageConsole;

public class DubProcessUIListener implements IExternalProcessListener {
	
	private static final String CONSOLE_NAME = "DUB Output";
	
	protected static IColorManager getColorManager() {
		return DeeUIPlugin.getDefault().getTextTools().getColorManager();
	}
	
	@RunsInDubProcessAgent
	@Override
	public void handleProcessStarted(ExternalProcessOutputHelper processHelper, ProcessBuilder pb) {
		String consoleQualifier = getConsoleQualifier(pb); 
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
		
		try {
			outputListener.metaOut.write("************  Running dub command  ************\n");
			outputListener.metaOut.write(StringUtil.collToString(pb.command(), " ") + "\n");
			outputListener.metaOut.write("@ " + pb.directory() +"\n");
		} catch (IOException e) {
			return;
		}
		
		processHelper.getOutputListeningHelper().addListener(outputListener);
	}
	
	protected String getConsoleQualifier(ProcessBuilder pb) {
		File directory = pb.directory();
		if(directory == null || directory.toPath().getNameCount() == 0) {
			return "(Global)";
		}
		Path dirPath = directory.toPath();
		return "["+ dirPath.getFileName() +"]";
	}
	
	public static class DubProcessUIConsoleOutputHandler implements IProcessOutputListener {
		
		private final MessageConsole console;
		private final IOConsoleOutputStream metaOut;
		private final IOConsoleOutputStream stdOut;
		private final IOConsoleOutputStream stdErr;
		
		public DubProcessUIConsoleOutputHandler(String consoleQualifier) {
			
			console = ConsoleUtils.recreateMessageConsole(CONSOLE_NAME + " " + consoleQualifier, 
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