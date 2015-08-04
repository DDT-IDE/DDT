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


import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

import dtool.dub.DubBundle.BundleFile;
import dtool.dub.DubBundle.DubDependecyRef;
import dtool.tests.CommonDToolTest;
import dtool.tests.DToolTestResources;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.process.ExternalProcessHelper;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;

public class CommonDubTest extends CommonDToolTest {
	
	public static final Location DUB_TEST_BUNDLES = DToolTestResources.getTestResourceLoc("dub");	
	
	public static final BundlePath XPTO_BUNDLE_PATH = bundlePath(DUB_TEST_BUNDLES, "XptoBundle");
	
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
		
		public final Location location;
		public final String bundleName;
		public final String errorMsgStart;
		public final String version;
		public final Path[] sourceFolders;
		public final String[] rawDeps;
		public final DubBundleChecker[] expectedDeps;
		
		public DubBundleChecker(Location location, String bundleName) {
			this(location, bundleName, null, IGNORE_STR, null, IGNORE_RAW_DEPS, IGNORE_DEPS);
		}
		
		public DubBundleChecker(Location location, String bundleName, String errorMsgStart, String version,
				Path[] sourceFolders, String[] rawDeps, DubBundleChecker[] deps) {
			this.location = location;
			this.bundleName = bundleName;
			this.errorMsgStart = errorMsgStart;
			this.version = version;
			this.sourceFolders = sourceFolders;
			this.rawDeps = rawDeps;
			this.expectedDeps = deps;
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
			checkAreEqual(bundle.getLocation(), location);
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
			
			if(expectedDeps == IGNORE_DEPS) 
				return;
			
			DubBundle[] deps = bundleDescription.getBundleDependencies();
			assertTrue(expectedDeps.length == deps.length);
			for (int ix = 0; ix < expectedDeps.length; ix++) {
				DubBundleChecker dubDepChecker = expectedDeps[ix];
				dubDepChecker.check(deps[ix], true);
			}
		}
		
		protected void checkResolvedBundle(DubBundleDescription bundleDescription, String dubDescribeError) {
			assertExceptionContains(bundleDescription.error, dubDescribeError);
			
			boolean isResolved = dubDescribeError == null;
			checkBundleDescription(bundleDescription, isResolved);
		}
		
	}
	
	public static DubBundleChecker main(Location location, String errorMsgStart, String name, 
			String version, Path[] srcFolders, String[] rawDeps, DubBundleChecker... deps) {
		return new DubBundleChecker(location, name, errorMsgStart, version, srcFolders, rawDeps, deps);
	}
	
	public static DubBundleChecker bundle(Location location, String errorMsgStart, String name, 
			String version, Path[] srcFolders) {
		return main(location, errorMsgStart, name, version, srcFolders, IGNORE_RAW_DEPS, IGNORE_DEPS);
	}
	
	public static DubBundleChecker bundle(Location location, String name) {
		return new DubBundleChecker(location, name, null, IGNORE_STR, null, IGNORE_RAW_DEPS, IGNORE_DEPS);
	}
	
	public static DubBundleChecker bundle(String errorMsgStart, String name) {
		return new DubBundleChecker(IGNORE_PATH, name, errorMsgStart, IGNORE_STR, null, IGNORE_RAW_DEPS, IGNORE_DEPS);
	}
	
	public static BundleFile bf(String filePath) {
		return new BundleFile(filePath, false);
	}
	
	public static String[] rawDeps(String... rawDeps) {
		return rawDeps;
	}
	
	protected void checkResolvedBundle(DubBundleDescription bundleDescription, String dubDescribeError, 
			DubBundleChecker mainBundleChecker) {
		mainBundleChecker.checkResolvedBundle(bundleDescription, dubDescribeError);
	}
	
	/* ------------------------------ */
	
	protected String runDubDescribe(BundlePath workingDir) throws Exception {
		ExternalProcessResult processResult = startDubProcess(workingDir, false, "describe")
				.awaitTerminationAndResult(2000);
		
		return processResult.getStdOutBytes().toString(StringUtil.UTF8);
	}
	
	public static ExternalProcessHelper startDubProcess(BundlePath bundlePath, 
			boolean redirectStdErr, String... arguments) 
			throws IOException {
		String[] command = ArrayUtil.prepend(testsDubPath(), arguments);
		ProcessBuilder pb = new ProcessBuilder(command);
		if(bundlePath != null) {
			pb.directory(bundlePath.getLocation().toFile());
		}
		pb.redirectErrorStream(redirectStdErr);
		return new ExternalProcessHelper(pb);
	}
	
	public static void dubAddPath(Location packageRootDir) {
		String packageRootDirStr = packageRootDir.toString();
		System.out.println(":::: Adding DUB package root path: " + packageRootDirStr);
		String[] arguments = array("add-path", packageRootDirStr);
		runDubCommand(arguments);
	}
	
	public static void dubRemovePath(Location packageRootDir) {
		String packageRootDirStr = packageRootDir.toString();
		System.out.println(":::: Removing DUB package root path: " + packageRootDirStr);
		String[] arguments = array("remove-path", packageRootDirStr);
		runDubCommand(arguments);
	}
	
	public static void runDubCommand(String[] arguments) {
		try {
			ExternalProcessHelper processHelper = startDubProcess(null, true, arguments);
			ExternalProcessResult result = processHelper.awaitTerminationAndResult(3000);
			System.out.println(result.getStdOutBytes().toString(StringUtil.UTF8));
			assertTrue(processHelper.getProcess().exitValue() == 0);
		} catch (TimeoutException | InterruptedException | IOException e) {
			throw assertFail();
		}
	}
	
}