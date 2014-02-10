/*******************************************************************************
 * Copyright (c) 2011, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.tests;

import java.io.IOException;
import java.nio.file.Path;

import melnorme.utilbox.concurrency.ExternalProcessOutputReader;
import melnorme.utilbox.core.DevelopmentCodeMarkers.Tests_HasExternalDependencies;
import melnorme.utilbox.misc.ArrayUtil;

public class DToolTests implements Tests_HasExternalDependencies {
	
	public static final String DTOOL_PREFIX = "DTool.";
	public static final boolean TESTS_LITE_MODE = System.getProperty(DTOOL_PREFIX + "TestsLiteMode") != null;
	
	public static final String DUB_PROGRAM_PATH = "dub";
	
	public static ExternalProcessOutputReader startDubDescribe(Path path, String... arguments) throws IOException {
		String[] command = ArrayUtil.prepend(DUB_PROGRAM_PATH, arguments);
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.directory(path.toFile());
		
		ExternalProcessOutputReader processHelper = ExternalProcessOutputReader.startProcess(pb, false);
		return processHelper;
	}
	
}