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

import java.io.IOException;

import dtool.engine.DToolServer;


public class GenieMain {
	
	public static void main	(String[] args) {
		System.out.println("D Genie Tool - 0.0.1");
		
		int portNumber = 2501;
		
		if (args.length > 1) {
			portNumber = Integer.parseInt(args[0]);
		}
		
		DToolServer dtoolServer = new DToolServer();
		try {
			new GenieServer(dtoolServer, portNumber).runServer();
		} catch (IOException e) {
			System.out.println("Error trying to listen for connection on port " + portNumber + ".");
			System.out.println(e.getMessage());
		}
		
	}
	
}