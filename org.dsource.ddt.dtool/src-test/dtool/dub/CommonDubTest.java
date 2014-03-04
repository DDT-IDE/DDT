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
import java.util.concurrent.TimeoutException;

import melnorme.utilbox.concurrency.ExternalProcessOutputReader;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;
import dtool.dub.DubBundle.DubDependecyRef;
import dtool.tests.DToolBaseTest;
import dtool.tests.DToolTests;

public class CommonDubTest extends DToolBaseTest {
	
	public CommonDubTest() {
		super();
	}
	
	public static Path path(String str) {
		return Paths.get(str);
	}
	
	public static Path[] paths(String... str) {
		Path[] newArray = new Path[str.length];
		for (int i = 0; i < str.length; i++) {
			newArray[i] = Paths.get(str[i]); 
		}
		return newArray;
	}
	
	
	public static final DubBundleChecker[] IGNORE_DEPS = new DubBundleChecker[0];
	public static final String[] IGNORE_RAW_DEPS = new String[0];
	
	public static final String ERROR_DUB_RETURNED_NON_ZERO = "dub returned non-zero";
	
	public static class DubBundleChecker extends CommonChecker {
		
		public final Path location;
		public final String bundleName;
		public final String errorMsgStart;
		public final String version;
		public final Path[] sourceFolders;
		public final String[] rawDeps;
		public final DubBundleChecker[] deps;
		
		public DubBundleChecker(Path location, String bundleName, String errorMsgStart, String version,
				Path[] sourceFolders, String[] rawDeps, DubBundleChecker[] deps) {
			this.location = location;
			this.bundleName = bundleName;
			this.errorMsgStart = errorMsgStart;
			this.version = version;
			this.sourceFolders = sourceFolders;
			this.rawDeps = rawDeps;
			this.deps = deps;
		}
		
		@Override
		protected boolean isIgnoreArray(Object[] expected){
			return expected == IGNORE_DEPS || expected == IGNORE_ARR || expected == IGNORE_RAW_DEPS;
		}
		
		public boolean isResolvedOnlyError() {
			return errorMsgStart == ERROR_DUB_RETURNED_NON_ZERO;
		}
		
		public void check(DubBundle bundle, boolean isResolved) {
			checkAllExceptDepRefs(bundle, isResolved);
			checkDepRefs(bundle);
		}
		
		protected void checkAllExceptDepRefs(DubBundle bundle, boolean isResolved) {
			checkAreEqual(bundle.location, location);
			checkAreEqual(bundle.name, bundleName);
			if(isResolvedOnlyError() && !isResolved) {
				// Don't check, error occurs only in resolved bundles
			} else {
				assertExceptionMsgStart(bundle.error, errorMsgStart);
			}
			checkAreEqual(bundle.version, version);
			checkAreEqualArray(bundle.getEffectiveSourceFolders(), ignoreIfNull(sourceFolders));
		}
		
		protected void checkDepRefs(DubBundle bundle) {
			if(rawDeps == IGNORE_RAW_DEPS) {
				return;
			}
			assertEquals(bundle.getDependencyRefs().length, rawDeps.length);
			for (int i = 0; i < rawDeps.length; i++) {
				String expRawDep = rawDeps[i];
				DubDependecyRef depRef = bundle.getDependencyRefs()[i];
				checkAreEqual(depRef.bundleName, expRawDep);
			}
		}
		
		public void checkBundleDescription(DubBundleDescription bundleDescription, boolean isResolved) {
			assertTrue(bundleDescription.isResolved() == isResolved);
			
			if(!isResolved) {
				check(bundleDescription.getMainBundle(), isResolved);
				assertTrue(bundleDescription.hasErrors() ||
					bundleDescription.getBundleDependencies().length == 0);
				return;
			} else {
				checkAllExceptDepRefs(bundleDescription.getMainBundle(), isResolved);
			}
			
			if(deps == IGNORE_DEPS) 
				return;
			
			assertTrue(deps.length == bundleDescription.getBundleDependencies().length);
			for (int ix = 0; ix < deps.length; ix++) {
				DubBundleChecker dubDepChecker = deps[ix];
				dubDepChecker.check(bundleDescription.getBundleDependencies()[ix], true);
			}
		}
		
	}
	
	public static DubBundleChecker main(Path location, String errorMsgStart, String name, 
			String version, Path[] srcFolders, String[] rawDeps, DubBundleChecker... deps) {
		return new DubBundleChecker(location, name, errorMsgStart, version, srcFolders, rawDeps, deps);
	}
	
	public static DubBundleChecker bundle(Path location, String errorMsgStart, String name, 
			String version, Path[] srcFolders) {
		return main(location, errorMsgStart, name, version, srcFolders, IGNORE_RAW_DEPS, IGNORE_DEPS);
	}
	
	public static DubBundleChecker bundle(String errorMsgStart, String name) {
		return new DubBundleChecker(IGNORE_PATH, name, errorMsgStart, IGNORE_STR, null, IGNORE_RAW_DEPS, IGNORE_DEPS);
	}
	
	public static String[] rawDeps(String... rawDeps) {
		return rawDeps;
	}
	
	protected void checkResolvedBundle(DubBundleDescription bundleDescription, String dubDescribeError, 
			DubBundleChecker mainBundleChecker) {
		assertExceptionContains(bundleDescription.error, dubDescribeError);
		
		boolean isResolved = dubDescribeError == null;
		mainBundleChecker.checkBundleDescription(bundleDescription, isResolved);
	}
	
	/* ------------------------------ */
	
	protected String runDubDescribe(java.nio.file.Path workingDir) throws Exception {
		ExternalProcessOutputReader processHelper = startDubProcess(workingDir, "describe");
		processHelper.awaitTerminationStrict_destroyOnException(2000);
		
		return processHelper.getStdOutBytes().toString(StringUtil.UTF8);
	}
	
	public static ExternalProcessOutputReader startDubProcess(Path workingDir, String... arguments) 
			throws IOException {
		String[] command = ArrayUtil.prepend(DToolTests.DUB_PROGRAM_PATH, arguments);
		ProcessBuilder pb = new ProcessBuilder(command);
		if(workingDir != null) {
			pb.directory(workingDir.toFile());
		}
		
		return new ExternalProcessOutputReader(pb);
	}
	
	public static void dubAddPath(Path packageRootDir) {
		String packageRootDirStr = packageRootDir.toString();
		System.out.println(":::: Adding DUB package root path: " + packageRootDirStr);
		try {
			ExternalProcessOutputReader processHelper;
			processHelper = startDubProcess(null, "add-path", packageRootDirStr);
			processHelper.awaitTerminationStrict_destroyOnException(2000);
			assertTrue(processHelper.getProcess().exitValue() == 0);
		} catch (TimeoutException | InterruptedException | IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	public static void dubRemovePath(Path packageRootDir) {
		String packageRootDirStr = packageRootDir.toString();
		System.out.println(":::: Removing DUB package root path: " + packageRootDirStr);
		try {
			ExternalProcessOutputReader processHelper;
			processHelper = startDubProcess(null, "remove-path", packageRootDirStr);
			processHelper.awaitTerminationStrict_destroyOnException(2000);
			assertTrue(processHelper.getProcess().exitValue() == 0);
		} catch (TimeoutException | InterruptedException | IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
}