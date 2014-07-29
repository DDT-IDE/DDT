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

import dtool.genie.GenieServer.JsonCommandHandler;

public class FindDefinitionCommandHandler extends JsonCommandHandler {
	
	public FindDefinitionCommandHandler() {
		super("find_definition");
	}
	
	@Override
	protected void parseCommandInput() throws IOException {
		super.parseCommandInput();
	}
	
	@Override
	protected void writeResponseJsonContents() throws IOException {
	}
	
}