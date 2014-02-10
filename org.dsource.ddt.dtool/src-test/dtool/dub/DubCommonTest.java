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
package dtool.dub;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

import melnorme.utilbox.concurrency.ExternalProcessOutputReader;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;
import dtool.tests.DToolBaseTest;
import dtool.tests.DToolTests;

public class DubCommonTest extends DToolBaseTest {
	
	public DubCommonTest() {
		super();
	}
	
	protected String runDubDescribe(java.nio.file.Path workingDir) throws Exception {
		ExternalProcessOutputReader processHelper = startDubProcess(workingDir, "describe");
		processHelper.awaitTermination(2000);
		
		return processHelper.getStdOutBytes().toString(StringUtil.UTF8);
	}
	
	public static ExternalProcessOutputReader startDubProcess(Path workingDir, String... arguments) 
			throws IOException {
		String[] command = ArrayUtil.prepend(DToolTests.DUB_PROGRAM_PATH, arguments);
		ProcessBuilder pb = new ProcessBuilder(command);
		if(workingDir != null) {
			pb.directory(workingDir.toFile());
		}
		
		ExternalProcessOutputReader processHelper = ExternalProcessOutputReader.startProcess(pb, false);
		return processHelper;
	}
	
	public static void dubAddPath(Path packageRootDir) {
		String packageRootDirStr = packageRootDir.toString();
		System.out.println(":::: Adding DUB package root path: " + packageRootDirStr);
		try {
			ExternalProcessOutputReader processHelper;
			processHelper = startDubProcess(null, "add-path", packageRootDirStr);
			processHelper.awaitTermination(2000);
			assertTrue(processHelper.getProcess().exitValue() == 0);
		} catch (InterruptedException | IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	public static void dubRemovePath(Path packageRootDir) {
		String packageRootDirStr = packageRootDir.toString();
		System.out.println(":::: Removing DUB package root path: " + packageRootDirStr);
		try {
			ExternalProcessOutputReader processHelper;
			processHelper = startDubProcess(null, "remove-path", packageRootDirStr);
			processHelper.awaitTermination(2000);
			assertTrue(processHelper.getProcess().exitValue() == 0);
		} catch (InterruptedException | IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
}