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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;

import java.io.File;
import java.io.IOException;

import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.StringUtil;
import dtool.engine.DToolServer;
import dtool.genie.GenieMain.AbstractCmdlineOperation;
import dtool.genie.GenieServer;

public class StartServerOperation extends AbstractCmdlineOperation {
	
	public StartServerOperation() {
		super("start");
	}
	
	@Override
	public String getOneLineSummary() {
		return "Start the DToolGenie server.";
	}
	
	protected DToolServer dtoolServer;

	@Override
	protected void handle() {
		int requestedPortNumber = 0;
		boolean force = true;
		
		dtoolServer = new DToolServer();
		
		final File sentinelFile = new File(System.getProperty("user.home"), ".dtoolgenie");
		try {
			boolean created = sentinelFile.createNewFile();
			if(created == false && force == false) {
				String message = "Failed to create server sentinel file, perhaps server is running already?";
				errorBail(message + "  use argument 'force' to start anyways", null); // TODO
			}
		} catch (IOException e) {
			errorBail("Error creating sentinel file " + sentinelFile, e);
		}
		sentinelFile.deleteOnExit();
		
		GenieServer genieServer;
		try {
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
	
	protected RuntimeException errorBail(String message, Throwable throwable) {
		dtoolServer.logError(message, throwable);
		System.exit(1);
		throw assertUnreachable();
	}
	
}