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
package dtool.genie.cmdline;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.StringUtil;
import dtool.engine.DToolServer;
import dtool.genie.GenieMain.AbstractCmdlineOperation;
import dtool.genie.GenieServer;

public class StartServerOperation extends AbstractCmdlineOperation {
	
	protected static final String SENTINEL_FILE_NAME = ".dtoolgenie";
	
	public StartServerOperation() {
		super("start");
	}
	
	@Override
	public String getOneLineSummary() {
		return "Start the DToolGenie server.";
	}
	
	@Override
	public void printCommandHelp(PrintStream out) {
		out.println(helpUsageIntro() + "[<port>] [force]");
		out.println();
		out.println("Start the Genie server, listening on given <port>. This will fail if an ");
		out.println("already running Genie server is detected, unless the 'force' option is given.");
		out.println();
		out.println("When the Genie server starts, a file is created in $HOME/" + SENTINEL_FILE_NAME); 
		out.println("with the port number of the started server. This file is deleted when the server");
		out.println("terminates, and thus can be used to determine if a Genie server is running.");
	}
	
	protected boolean force;
	protected int requestedPortNumber;
	
	@Override
	protected void processArgs() {
		force = getFlag("force");
		String portNumberArg = retrieveFirstUnparsedArgument(true);
		requestedPortNumber = portNumberArg == null ? 0 : validatePositiveInt(portNumberArg);
	}
	
	@Override
	protected void handle() {
		final File sentinelFile = new File(System.getProperty("user.home"), SENTINEL_FILE_NAME);
		try {
			boolean created = sentinelFile.createNewFile();
			if(created == false && force == false) {
				String message = "Failed to create server sentinel file, perhaps server is running already?";
				errorBail(message + "  use argument 'force' to start anyways", null);
			}
		} catch (IOException e) {
			errorBail("Error creating sentinel file " + sentinelFile, e);
		}
		sentinelFile.deleteOnExit();
		
		GenieServer genieServer;
		try {
			DToolServer dtoolServer = new DToolServer();
			genieServer = new GenieServer(dtoolServer, requestedPortNumber);
		} catch (IOException ioe) {
			throw errorBail("Error trying to listen for connection on port " + requestedPortNumber + ".", ioe);
		}
		
		try {
			FileUtil.writeStringToFile(sentinelFile, ""+genieServer.getServerPortNumber(), StringUtil.UTF8);
		} catch (IOException e) {
			errorBail("Error writing to sentinel file " + sentinelFile, e);
		}
		
		genieServer.runServer();
	}
	
}