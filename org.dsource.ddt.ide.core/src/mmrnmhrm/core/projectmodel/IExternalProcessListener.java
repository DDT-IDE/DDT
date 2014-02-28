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
package mmrnmhrm.core.projectmodel;

import java.io.IOException;

import melnorme.utilbox.concurrency.ExternalProcessOutputHelper;



public interface IExternalProcessListener {
	
	void handleProcessStarted(ProcessBuilder pb, String projectName, ExternalProcessOutputHelper processHelper);
	
	void handleProcessFailedToStarted(ProcessBuilder pb, String projectName, IOException e);
	
}