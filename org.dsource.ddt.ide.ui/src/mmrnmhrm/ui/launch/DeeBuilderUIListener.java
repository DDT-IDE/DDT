package mmrnmhrm.ui.launch;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.io.IOException;

import mmrnmhrm.core.build.CommonDeeBuilderListener;
import mmrnmhrm.core.build.IDeeBuilderListener;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class DeeBuilderUIListener extends CommonDeeBuilderListener implements IDeeBuilderListener {
	
	private static final String CONSOLE_NAME = "DDT Build output:";
	
	@Override
	public void buildCommandsCreated(String buildCommands) {
		final MessageConsole myConsole = findOrCreateConsole(CONSOLE_NAME);
		// BM: There used to be a race condition here with Console code itself, but not more
		myConsole.clearConsole(); 
		handleProcessOutputLine("--------  Build Commands:  --------\n" + buildCommands + "\n"); 
		// We would still have a race to the console if multiple D builds could ever be running concurrently
	}
	
	@Override
	public void handleProcessOutputLine(String line) {
		// TODO synchronize? Consider one message string only
		
		// TODO:, listen for different project outputs
		String name = CONSOLE_NAME;
		MessageConsole builderConsole = findOrCreateConsole(name);
		MessageConsoleStream out = builderConsole.newMessageStream();
		out.println(line);
		
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			assertFail();
		}
		
		builderConsole.activate();
	}
	
	
	public static MessageConsole findOrCreateConsole(String name) {
		MessageConsole console = findConsole(name);
		if(console == null) {
			//no console found, so create a new one
			console = new MessageConsole(name, null);
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{console});
		}
		return console;
	}
	
	private static MessageConsole findConsole(String name) {
		IConsoleManager consoleMgr = ConsolePlugin.getDefault().getConsoleManager();
		IConsole[] existing = consoleMgr.getConsoles();
		for (int i = 0; i < existing.length; i++) {
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		}
		return null;
	}
}
