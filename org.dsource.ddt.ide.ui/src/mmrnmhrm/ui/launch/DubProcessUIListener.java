package mmrnmhrm.ui.launch;

import java.io.IOException;

import melnorme.lang.ide.ui.utils.ConsoleUtils;
import melnorme.utilbox.concurrency.ExternalProcessOutputHelper;
import melnorme.utilbox.concurrency.ExternalProcessOutputHelper.IProcessOutputListener;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.projectmodel.DubModelManager.RunsInDubExecutor;
import mmrnmhrm.core.projectmodel.IDubProcessListener;

import org.dsource.ddt.ui.DeeUIPlugin;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class DubProcessUIListener implements IDubProcessListener {
	
	private static final String CONSOLE_NAME = "dub describe";
	
	public class DubConsole extends MessageConsole {
		
		public DubConsole(String name, ImageDescriptor imageDescriptor) {
			super(name, imageDescriptor);
		}
		
	}
	
	protected static IColorManager getColorManager() {
		return DeeUIPlugin.getDefault().getTextTools().getColorManager();
	}
	
	@RunsInDubExecutor
	@Override
	public void handleProcessStarted(ExternalProcessOutputHelper processHelper, ProcessBuilder pb) {
		final DubProcessUIConsoleOutputHandler outputListener = new DubProcessUIConsoleOutputHandler(processHelper);
		
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
			outputListener.metaOut.write("--------  " + StringUtil.collToString(pb.command(), " ") + " --------\n");
			outputListener.metaOut.write("---- @ " + pb.directory() +" ----\n");
		} catch (IOException e) {
			return;
		}
		
		processHelper.getOutputListeningHelper().addListener(outputListener);
	}
	
	public static class DubProcessUIConsoleOutputHandler implements IProcessOutputListener {
		
		protected final ExternalProcessOutputHelper processHelper;
		
		private final MessageConsole console;
		private final MessageConsoleStream metaOut;
		private final MessageConsoleStream stdOut;
		private final MessageConsoleStream stdErr;
		
		public DubProcessUIConsoleOutputHandler(ExternalProcessOutputHelper processHelper) {
			this.processHelper = processHelper;
			
			console = ConsoleUtils.findOrCreateMessageConsole(CONSOLE_NAME);
			console.clearConsole();
			
			metaOut = console.newMessageStream();
			
			stdOut = console.newMessageStream();
			stdErr = console.newMessageStream();
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
				metaOut.write("--------  Terminated, exit code: " + exitCode +  "  --------\n");
			} catch (IOException e) {
				// Ignore
			}
		}
		
	}
	
}