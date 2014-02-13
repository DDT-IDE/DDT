package mmrnmhrm.core.projectmodel;

import static dtool.dub.CommonDubTest.bundle;
import static dtool.dub.CommonDubTest.main;
import static dtool.dub.CommonDubTest.paths;
import static dtool.dub.CommonDubTest.rawDeps;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;
import java.util.HashSet;

import melnorme.utilbox.concurrency.LatchRunnable;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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
		return DubModelManager.getDubContainer(project);
	}
	
	/* ************************************ */
	
	public static final String DUB_TEST = "DubTest";
	public static Path DUB_WORKSPACE = DubManifestParserTest.DUB_WORKSPACE;
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		
		long taskCount = getDubExecutorAgent().getSubmittedTaskCount();
		IScriptProject dubTestProject = createAndOpenDeeProject(DUB_TEST, true);
		// check no change:
		assertTrue(getDubExecutorAgent().getSubmittedTaskCount() == taskCount); 
		assertTrue(DubModelManager.getBundleInfo(DUB_TEST) == null);
		
		try {
			runBasicTestSequence(dubTestProject);
		} finally {
			dubTestProject.getProject().delete(true, null); // cleanup
		}
	}
	
	public void runBasicTestSequence(IScriptProject dubTestProject) throws Exception {
		Path location = dubTestProject.getProject().getLocation().toFile().toPath();
		
		writeDubJsonAndVerifyStatus("{"+ 
			jsEntry("name", "xptobundle")+ jsFileEnd(),
			dubTestProject, 
			main(location, null, "xptobundle", DubBundle.DEFAULT_VERSION, srcFolders(), rawDeps()));
		
		writeDubJsonAndVerifyStatus("{"+
			jsEntry("name", "xptobundle")+
			jsEntry("importPaths", jsArray("src", "src-test"))+
			jsEntry("version", "2.1")+
			jsFileEnd(),
			
			dubTestProject, 
			main(location, null, "xptobundle", "2.1", srcFolders("src", "src-test"), rawDeps())
		);
		
		
		writeDubJsonAndVerifyStatus(
			readFileContents(DUB_WORKSPACE.resolve("XptoBundle/dub.json")),
			
			dubTestProject,
			main(location, null, "xptobundle", DubBundle.DEFAULT_VERSION, srcFolders("src", "src-test"), 
				rawDeps("foo_lib"), 
				bundle(DUB_WORKSPACE.resolve("foo_lib"), null, "foo_lib", DubBundle.DEFAULT_VERSION, paths("src", "src2")),
				bundle(DUB_WORKSPACE.resolve("bar_lib"), null, "bar_lib", DubBundle.DEFAULT_VERSION, paths("source"))
			)
		);
		
		// Test error in raw DUB.json, check that project info should stay same as before: TODO
		writeDubJsonAndVerifyStatus("{"+ jsEntry("nameMISSING", "xptobundle")+ jsFileEnd(),
			dubTestProject, 
			bundle(DubManifestParser.ERROR_BUNDLE_NAME_UNDEFINED, IGNORE_STR));
		
		writeDubJsonAndVerifyStatus("{"+ jsEntry("name", "xptobundle")+ jsFileEnd(),
			dubTestProject, 
			main(location, null, "xptobundle", DubBundle.DEFAULT_VERSION, srcFolders(), rawDeps()));
		
	}
	
	public void writeDubJsonAndVerifyStatus(String dubJson, IScriptProject dubProject, DubBundleChecker mainBundle)
			throws CoreException {
		LatchRunnable preWriteLatch = writeDubJson(dubProject, dubJson);
		checkModel(preWriteLatch, dubProject, mainBundle);
	}
	
	public void checkModel(LatchRunnable preWriteLatch, IScriptProject dubProject, DubBundleChecker expMainBundle) 
			throws CoreException {
		final IProject project = dubProject.getProject();
		
		DubBundleDescription dubBundle = getExistingDubBundleInfo(project.getName());
		expMainBundle.checkBundleDescription(dubBundle, false);
		
		DubDependenciesContainer dubContainer = getDubContainer(project);
		assertNotNull(dubContainer);
		// TODO test children
		
		preWriteLatch.releaseAll();
		DubModelManager.getDefault().syncPendingUpdates();
		
		dubBundle = getExistingDubBundleInfo(project.getName());
		if(expMainBundle.errorMsgStart != null) {
			// Check that we did not attempt to call dub describe on a .json with errors
			assertTrue(!dubBundle.isResolved());
			return;
		}
		
		expMainBundle.checkBundleDescription(dubBundle, true);
		
		DubBundleException error = dubBundle.getError();
		DubBundleChecker[] deps = expMainBundle.deps;
		if(error != null) {
			
			IMarker[] markers = DubModelManager.getDubErrorMarkers(project);
			assertTrue(markers.length == 1);
			IMarker errorMarker = markers[0];
			assertTrue(errorMarker.getAttribute(IMarker.MESSAGE, "").startsWith(error.getMessage()));
			assertEquals(errorMarker.getAttribute(IMarker.SEVERITY), IMarker.SEVERITY_ERROR);
			
		} else {
			checkRawBuildpath(dubProject.getRawBuildpath(), expMainBundle.sourceFolders);
			
			checkResolvedBuildpath(dubProject.getResolvedBuildpath(false), expMainBundle.sourceFolders, deps);
			
			IMarker[] dubErrorMarkers = DubModelManager.getDubErrorMarkers(project);
			assertTrue(dubErrorMarkers.length == 0);
		}
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
	
//	protected void removeChild(HashSet<CommonDubElement> children, DubBundle dubBundle) {
//		String name = dubBundle.name;
//		for (CommonDubElement dubElement : children) {
//			if(dubElement instanceof DubDependencyElement) {
//				DubDependencyElement dubDependencyElement = (DubDependencyElement) dubElement;
//				if(dubDependencyElement.getBundleName().equals(name)) {
//					children.remove(dubElement);
//					return;
//				}
//			}
//		}
//	}
}