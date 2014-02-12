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
import java.nio.file.Paths;

import melnorme.utilbox.concurrency.ExternalProcessOutputReader;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;
import dtool.tests.DToolBaseTest;
import dtool.tests.DToolTests;

public class CommonDubTest extends DToolBaseTest {
	
	public CommonDubTest() {
		super();
	}
	
	public static Path[] paths(String... str) {
		Path[] newArray = new Path[str.length];
		for (int i = 0; i < str.length; i++) {
			newArray[i] = Paths.get(str[i]); 
		}
		return newArray;
	}
	
	public static abstract class DubBundleChecker implements Checker<DubBundle> {
		
		public final Path location;
		public final Path[] sourceFolders;
		
		public DubBundleChecker(Path location, Path[] sourceFolders) {
			this.location = location;
			this.sourceFolders = sourceFolders;
		}
		
	}
	
	public static DubBundleChecker dep(final Path location, final String errorMsgStart, final String name, 
			final String version, final Path[] srcFolders) {
		return new DubBundleChecker(location, srcFolders) {
			@Override
			public Void check(DubBundle bundle) {
				assertAreEqual(bundle.location, location);
				assertAreEqual(bundle.name, name);
				assertAreEqual(bundle.version, version);
				
				assertEqualArrays(bundle.getSourceFolders(), srcFolders);
				assertEqualArrays(bundle.dependencies, null);
				
				assertExceptionMsgStart(bundle.error, errorMsgStart);
				return null;
			}
		};
	}
	
	public static DubBundleChecker dep(final String errorMsgStart, final String name) {
		return new DubBundleChecker(null, null) {
			@Override
			public Void check(DubBundle bundle) {
				assertAreEqual(bundle.name, name);
				
				assertExceptionMsgStart(bundle.error, errorMsgStart);
				return null;
			}
		};
	}
	
	public static DubBundleChecker depNoCheck() {
		return new DubBundleChecker(null, null) {
			@Override
			public Void check(DubBundle bundle) {
				return null;
			}
		};
	}
	
	protected static void checkResolvedBundle(DubBundleDescription dubDescribe, String dubDescribeError, 
			String bundleName, DubBundleChecker mainBundle, DubBundleChecker... deps) {
		assertAreEqual(dubDescribe.bundleName, bundleName);
		assertExceptionContains(dubDescribe.error, dubDescribeError);
		assertTrue(dubDescribe.isResolved());
		
		mainBundle.check(dubDescribe.getMainBundle());
		
		assertTrue(deps.length == dubDescribe.getBundleDependencies().length);
		for (int ix = 0; ix < deps.length; ix++) {
			DubBundleChecker dubDepChecker = deps[ix];
			dubDepChecker.check(dubDescribe.getBundleDependencies()[ix]);
		}
	}
	
	/* ------------------------------ */
	
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