/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.genie;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import static melnorme.utilbox.core.CoreUtil.array;
import static melnorme.utilbox.misc.StringUtil.newSpaceFilledString;

import java.io.PrintStream;

import melnorme.utilbox.misc.ArrayUtil;
import dtool.genie.cmdline.StartServerOperation;


public class GenieMain {
	
	public static final String CMDLINE_PROGRAM_NAME = "genie";
	
	public static final AbstractCmdlineOperation[] commands = array(
		new StartServerOperation(),
		new CmdlineHelpOperation()
	);
	
	public static void main(String[] args) {
		
		args = args.length == 0 ? array("help") : args;
		String commandName = args[0];
		String[] newArgs = ArrayUtil.removeAt(args, 0);
		
		for (AbstractCmdlineOperation command : commands) {
			if(command.tryHandling(commandName, newArgs)) {
				return;
			}
		}
		
		System.err.println("Unknown command: " + commandName);
		System.exit(1);
	}
	
	public static abstract class AbstractCmdlineOperation extends ProgramArgumentsHelper {
		
		protected final String commandName;
		protected String[] rawArgs;
		
		public AbstractCmdlineOperation(String commandName) {
			this.commandName = commandName;
		}
		
		public boolean tryHandling(String commandString, String[] args) {
			if(!commandString.equals(commandName)) {
				return false;
			}
			this.rawArgs = args;
			parseArgs(args);
			handle();
			return true;
		}
		
		@Override
		protected RuntimeException handleArgumentsError(String message) {
			return errorBail(message, null);
		}
		
		protected RuntimeException errorBail(String message, Throwable throwable) {
			System.out.println(message);
			if(throwable != null) {
				System.out.println(throwable);
			}
			System.exit(1);
			throw assertUnreachable();
		}
		
		protected abstract void handle();
		
		public void printOneLineSummary(PrintStream out) {
			out.print("   " + commandName + newSpaceFilledString(10 - commandName.length()) + " - ");
			out.println(getOneLineSummary());
		}
		
		public abstract String getOneLineSummary();
		
	}
	
	public static class CmdlineHelpOperation extends AbstractCmdlineOperation {
		
		public CmdlineHelpOperation() {
			super("help");
		}
		
		@Override
		public void handle() {
			System.out.println(GenieServer.ENGINE_NAME + " - " + GenieServer.ENGINE_VERSION);
			System.out.println();
			System.out.println("usage: " + CMDLINE_PROGRAM_NAME + " <command> [<args>]");
			System.out.println();
			System.out.println("Available commands:");
			
			for (AbstractCmdlineOperation command : commands) {
				command.printOneLineSummary(System.out);
			}
			
			System.out.println();
		}
		
		@Override
		public String getOneLineSummary() {
			return "Display help.";
		}
		
	}
	
}