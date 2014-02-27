package mmrnmhrm.core.projectmodel;

import static dtool.dub.CommonDubTest.ERROR_DUB_RETURNED_NON_ZERO;
import static dtool.dub.CommonDubTest.bundle;
import static dtool.dub.CommonDubTest.main;
import static dtool.dub.CommonDubTest.paths;
import static dtool.dub.CommonDubTest.rawDeps;
import static dtool.dub.DubBundle.DEFAULT_VERSION;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import melnorme.utilbox.concurrency.LatchRunnable;
import melnorme.utilbox.misc.CollectionUtil;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.projectmodel.DubDependenciesContainer.DubErrorElement;
import mmrnmhrm.core.projectmodel.DubDependenciesContainer.ICommonDepElement;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.junit.Test;

import dtool.dub.CommonDubTest.DubBundleChecker;
import dtool.dub.DubBundle;
import dtool.dub.DubBundle.DubBundleException;
import dtool.dub.DubBundleDescription;
import dtool.dub.DubManifestParser;
import dtool.dub.DubManifestParserTest;

public class DubModelManagerTest extends CommonDubModelTest {
	
	protected DubDependenciesContainer getDubContainer(IProject project) {
		return DubModel.getDubContainer(project);
	}
	
	/* ************************************ */
	
	public static final String DUB_TEST = "DubTest";
	public static Path DUB_WORKSPACE = DubManifestParserTest.DUB_WORKSPACE;
	
	protected static final DubBundleChecker FOO_LIB_BUNDLE = bundle(DUB_WORKSPACE.resolve("foo_lib"), 
		null, "foo_lib", DEFAULT_VERSION, paths("src", "src2"));
	protected static final DubBundleChecker BAR_LIB_BUNDLE = bundle(DUB_WORKSPACE.resolve("bar_lib"), 
		null, "bar_lib", DEFAULT_VERSION, paths("source"));
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		IProject project;
		long taskCount = getModelAgent().getSubmittedTaskCount();
		project = createAndOpenDeeProject(DUB_TEST, true).getProject();
		// check no change:
		assertTrue(getModelAgent().getSubmittedTaskCount() == taskCount); 
		assertTrue(DubModel.getBundleInfo(DUB_TEST) == null);
		
		try {
			runBasicTestSequence______________(project);
			project.delete(true, null); // cleanup
		} finally {
			
		}
		assertTrue(DubModel.getBundleInfo(project.getName()) == null);
		assertTrue(getDubContainer(project) == null);
		
		// Verify code path where a project that already has dub manifest is added.
		project = createAndOpenProject(DUB_TEST, true);
		LatchRunnable latch = writeDubJson(project, "{"+ jsEntry("name", "xptobundle")+ jsFileEnd());
		setupStandardDeeProject(project);
		DubBundleDescription unresolvedBundleDesc = getExistingDubBundleInfo(project.getName());
		latch.releaseAll();
		
		Path location = project.getLocation().toFile().toPath();
		checkDubModel(unresolvedBundleDesc, project, 
			main(location, null, "xptobundle", DubBundle.DEFAULT_VERSION, srcFolders(), rawDeps()));
	}
	
	public void runBasicTestSequence______________(IProject project) throws Exception {
		Path location = project.getLocation().toFile().toPath();
		
		writeDubJsonAndCheckDubModel("{"+ jsEntry("name", "xptobundle")+ jsFileEnd(),
			project, 
			main(location, null, "xptobundle", DEFAULT_VERSION, srcFolders(), rawDeps()));
		
		writeDubJsonAndCheckDubModel("{"+
			jsEntry("name", "xptobundle")+
			jsEntry("importPaths", jsArray("src", "src-test"))+
			jsEntry("version", "2.1")+
			jsFileEnd(),
			
			project, 
			main(location, null, "xptobundle", "2.1", srcFolders("src", "src-test"), rawDeps())
		);
		
		
		writeDubJsonAndCheckDubModel(
			readFileContents(DUB_WORKSPACE.resolve("XptoBundle/dub.json")),
			
			project,
			main(location, null, "xptobundle", DEFAULT_VERSION, srcFolders("src", "src-test"), 
				rawDeps("foo_lib"), 
				FOO_LIB_BUNDLE,
				BAR_LIB_BUNDLE
			)
		);
		
		// TODO: Test error in raw DUB.json, check that project info should stay same as before:
		writeDubJsonAndCheckDubModel("{"+ jsEntry("nameMISSING", "xptobundle")+ jsFileEnd(),
			project, 
			bundle(DubManifestParser.ERROR_BUNDLE_NAME_UNDEFINED, IGNORE_STR));
		
		writeDubJsonAndCheckDubModel("{"+ jsEntry("name", "xptobundle")+ jsFileEnd(),
			project, 
			main(location, null, "xptobundle", DEFAULT_VERSION, srcFolders(), rawDeps()));
		
		
		// Test errors while running dub describe
		writeDubJsonAndCheckDubModel(
			readFileContents(DUB_WORKSPACE.resolve("ErrorBundle_MissingDep/dub.json")),
			
			project,
			main(location, ERROR_DUB_RETURNED_NON_ZERO, "ErrorBundle_MissingDep", DEFAULT_VERSION, srcFolders("src"), 
				rawDeps("foo_lib", "NonExistantDep"), 
				FOO_LIB_BUNDLE,
				bundle("" , "NonExistantDep")
			)
		);
		
	}
	
	public void writeDubJsonAndCheckDubModel(String dubJson, IProject project, DubBundleChecker expMainBundle)
			throws CoreException {
		LatchRunnable preWriteLatch = writeDubJson(project, dubJson);
		DubBundleDescription unresolvedBundleDesc = getExistingDubBundleInfo(project.getName());
		preWriteLatch.releaseAll();
		
		checkDubModel(unresolvedBundleDesc, project, expMainBundle);
	}
	
	public void checkDubModel(DubBundleDescription unresolvedDubBundle, IProject project, 
			DubBundleChecker expMainBundle) throws CoreException {
		checkUnresolvedBundle(project, expMainBundle, unresolvedDubBundle);
		
		DubModelManager.getDefault().syncPendingUpdates();
		
		DubBundleDescription dubBundle = getExistingDubBundleInfo(project.getName());
		if(unresolvedDubBundle.hasErrors()) {
			// Check that we did not attempt to call dub describe on a manifest with errors
			assertTrue(unresolvedDubBundle == dubBundle);
			return;
		}
		
		DubBundleException error = dubBundle.getError();
		if(error != null) {
			expMainBundle.checkBundleDescription(unresolvedDubBundle, false);
			testDubContainerUnresolved(project, expMainBundle, true);
			
			IMarker[] markers = DubModelManager.getDubErrorMarkers(project);
			assertTrue(markers.length == 1);
			IMarker errorMarker = markers[0];
			assertTrue(errorMarker.getAttribute(IMarker.MESSAGE, "").startsWith(error.getMessage()));
			assertEquals(errorMarker.getAttribute(IMarker.SEVERITY), IMarker.SEVERITY_ERROR);
			
		} else {
			expMainBundle.checkBundleDescription(dubBundle, true);
			testDubContainer(project, expMainBundle);

			DubBundleChecker[] deps = expMainBundle.deps;
			IScriptProject dubProject = DLTKCore.create(project);
			checkRawBuildpath(dubProject.getRawBuildpath(), expMainBundle.sourceFolders);
			
			checkResolvedBuildpath(dubProject.getResolvedBuildpath(false), expMainBundle.sourceFolders, deps);
			
			IMarker[] dubErrorMarkers = DubModelManager.getDubErrorMarkers(project);
			assertTrue(dubErrorMarkers.length == 0);
		}
	}
	
	protected void checkUnresolvedBundle(IProject project, DubBundleChecker expMainBundle,
			DubBundleDescription dubBundleDesc) {
		expMainBundle.checkBundleDescription(dubBundleDesc, false);
		testDubContainerUnresolved(project, expMainBundle, !expMainBundle.isResolvedOnlyError());
	}
	
	public static void checkRawBuildpath(IBuildpathEntry[] rawBuildpath, Path[] srcFolders) throws ModelException {
		HashSet<Path> sourcePaths = hashSet(srcFolders);
		
		for (IBuildpathEntry bpEntry : rawBuildpath) {
			IPath entryPath = bpEntry.getPath();
			if(entryPath.segment(0).equals(ScriptRuntime.INTERPRETER_CONTAINER)) {
				continue;
			}
			
			if((bpEntry.getEntryKind() == IBuildpathEntry.BPE_CONTAINER)) {
				assertTrue(entryPath.toString() .equals(DubBuildpathContainerInitializer.ID));
				continue;
			}
			
			assertTrue(bpEntry.getEntryKind() == IBuildpathEntry.BPE_SOURCE);
			assertTrue(bpEntry.isExternal() == false);
			IPath folderPath = entryPath.removeFirstSegments(1); // Remove project segment
			assertTrue(sourcePaths.remove(folderPath.toFile().toPath()));
			continue;
		}
		
		// Ensure we matched every entry
		assertTrue(sourcePaths.isEmpty());
	}
	
	public static void checkResolvedBuildpath(IBuildpathEntry[] buildpath, Path[] srcFolders, 
			DubBundleChecker[] deps) throws ModelException {
		HashSet<Path> sourcePaths = hashSet(srcFolders);
		
		HashSet<Path> depSourcePaths = new HashSet<>();
		for (DubBundleChecker bundleDep : deps) {
			for (Path path : bundleDep.sourceFolders) {
				Path srcFolderAbsolute = bundleDep.location.resolve(path);
				depSourcePaths.add(srcFolderAbsolute);
			}
		}
		
		for (IBuildpathEntry bpEntry : buildpath) {
			IPath entryPath = EnvironmentPathUtils.getLocalPath(bpEntry.getPath());
			
			if(bpEntry.getEntryKind() == IBuildpathEntry.BPE_LIBRARY && !DubContainer.isDubBuildpathEntry(bpEntry)) {
				String entryPathStr = entryPath.toString();
				assertTrue(
						entryPathStr.endsWith("druntime/import") || 
						entryPathStr.endsWith("phobos") ||
						entryPathStr.startsWith("#special#builtin"));
				continue;
			}
			
			
			if(bpEntry.getEntryKind() == IBuildpathEntry.BPE_LIBRARY) {
				assertTrue(bpEntry.isExternal());
				assertTrue(depSourcePaths.remove(entryPath.toFile().toPath()));
				continue;
			}
			
			assertTrue(bpEntry.getEntryKind() == IBuildpathEntry.BPE_SOURCE);
			assertTrue(bpEntry.isExternal() == false);
			IPath folderPath = entryPath.removeFirstSegments(1); // Remove project segment
			assertTrue(sourcePaths.remove(folderPath.toFile().toPath()));
			continue;
		}
		
		// Ensure we matched every entry
		assertTrue(sourcePaths.isEmpty());
		assertTrue(depSourcePaths == null || depSourcePaths.isEmpty());
	}
	
	protected void testDubContainerUnresolved(IProject project, DubBundleChecker expMainBundle, 
			boolean expectedError) {
		DubDependenciesContainer dubContainer = getDubContainer(project);
		assertNotNull(dubContainer);
		LinkedList<CommonDubElement> depChildren = CollectionUtil.createLinkedList(dubContainer.getChildren());
		for (String rawDep : expMainBundle.rawDeps) {
			removeChild(depChildren, rawDep);
		}
		if(expectedError) {
			removeErrorElement(expMainBundle, depChildren);
		}
		assertTrue(depChildren.isEmpty());
	}
	
	protected void testDubContainer(IProject project, DubBundleChecker expMainBundle) {
		DubDependenciesContainer dubContainer = getDubContainer(project);
		assertNotNull(dubContainer);
		assertTrue(dubContainer.getBundleInfo().isResolved());
		
		CommonDubElement[] children = dubContainer.getChildren();
		LinkedList<CommonDubElement> depChildren = CollectionUtil.createLinkedList(children);
		for (DubBundleChecker dep : expMainBundle.deps) {
			removeChild(depChildren, dep.bundleName);
		}
		removeErrorElement(expMainBundle, depChildren);
		assertTrue(depChildren.isEmpty());
	}
	
	protected void removeErrorElement(DubBundleChecker expMainBundle, LinkedList<CommonDubElement> depChildren) {
		if(expMainBundle.errorMsgStart != null) {
			assertTrue(depChildren.size() > 0);
			CommonDubElement removed = depChildren.remove(0);
			DubErrorElement dubErrorElement = assertCast(removed, DubErrorElement.class);
			assertTrue(dubErrorElement.errorDescription.startsWith(expMainBundle.errorMsgStart));
		}
	}
	
	protected void removeChild(Collection<CommonDubElement> children, String name) {
		for (CommonDubElement dubElement : children) {
			if(dubElement instanceof ICommonDepElement) {
				ICommonDepElement depElement = (ICommonDepElement) dubElement;
				if(depElement.getBundleName().equals(name)) {
					assertTrue(children.remove(dubElement));
					return;
				}
			}
		}
		assertFail(); // Must have removed
	}
	
	@Test
	public void testShutdown() throws Exception { testShutdown$(); }
	public void testShutdown$() throws Exception {
		DubModelManager dmm = new DubModelManager(new DubModel()); 
		dmm.initializeModelManager();
		final CountDownLatch latch = new CountDownLatch(1);
		
		dmm.modelAgent.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				latch.countDown();
				new CountDownLatch(1).await(); // wait until interrupted
				throw DeeCore.createCoreException("error", new Exception());
			}
		});
		latch.await();
		// Test that shutdown happens successfully even with pending task, and no log entries are made.
		dmm.shutdownManager(); 
	}
}