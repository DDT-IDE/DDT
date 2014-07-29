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

import java.io.IOException;

import dtool.engine.DToolServer;
import dtool.genie.GenieMain.AbstractCmdlineOperation;
import dtool.genie.GenieServer;

public class StartServerOperation extends AbstractCmdlineOperation {
	
	public StartServerOperation() {
		super("start");
	}
	
	@Override
	protected void handle() {
		int portNumber = 0;
		
//		File file = new File(System.getProperty("user.home"), ".dtoolgenie");
//		try {
//			boolean created = file.createNewFile();
//			if(created == false) {
//				// ER
//			}
//		} catch (IOException e) {
//			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
//		}
		
		DToolServer dtoolServer = new DToolServer();
		try {
			new GenieServer(dtoolServer, portNumber).runServer();
		} catch (IOException ioe) {
			dtoolServer.logError("Error trying to listen for connection on port " + portNumber + ".", ioe);
		}
	}
	
	@Override
	public String getOneLineSummary() {
		return "Start the DToolGenie server.";
	}
	
}