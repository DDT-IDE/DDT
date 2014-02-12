package mmrnmhrm.core.projectmodel;

import static dtool.dub.CommonDubTest.dep;
import static dtool.dub.CommonDubTest.paths;
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
import dtool.dub.DubBundle.DubBundleException;
import dtool.dub.DubBundleDescription;
import dtool.dub.DubParserTest;

public class DubProjectModelTest extends CommonDubModelTest {
	
//	protected DubDependenciesContainer getDubContainer(IScriptProject dubProject) {
//		return DubProjectModel.getDefault().getDubElement(dubProject.getProject());
//	}
	
	/* ************************************ */
	
	public static final String DUB_TEST = "DubTest";
	public static Path DUB_WORKSPACE = DubParserTest.DUB_WORKSPACE;
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		
		long taskCount = getDubExecutorAgent().getSubmittedTaskCount();
		IScriptProject dubTestProject = createAndOpenDeeProject(DUB_TEST, true);
		// check no change:
		assertTrue(getDubExecutorAgent().getSubmittedTaskCount() == taskCount); 
		assertTrue(DubProjectModel.getDefault().getBundleInfo(DUB_TEST) == null);
		
		try {
			runBasicTestSequence(dubTestProject);
		} finally {
			dubTestProject.getProject().delete(true, null); // cleanup
		}
	}
	
	public void runBasicTestSequence(IScriptProject dubTestProject) throws Exception {
		Path location = dubTestProject.getProject().getLocation().toFile().toPath();
		
		writeDubJsonAndVerifyStatus("{"+ jsEntry("name", "xptobundle")+ "}",
				dubTestProject, 
				dep(location, null, "xptobundle", "~master", srcFolders()));
		
		writeDubJsonAndVerifyStatus("{"+
				jsEntry("name", "xptobundle")+
				jsEntry("importPaths", jsArray("src", "src-test"))+
				"\"blah\":null}",
				
				dubTestProject, 
				dep(location, null, "xptobundle", "~master", srcFolders("src", "src-test"))
				);
		
		
		writeDubJsonAndVerifyStatus(
				readFileContents(DUB_WORKSPACE.resolve("XptoBundle/dub.json")),
		
				dubTestProject,
				dep(location, null, "xptobundle", "~master", srcFolders("src", "src-test")), 
				dep(DUB_WORKSPACE.resolve("bar_lib"), null, "bar_lib", "~master", paths("source")),
				dep(DUB_WORKSPACE.resolve("foo_lib"), null, "bar_lib", "~master", paths("src", "src2"))
				);
		
		
		writeDubJsonAndVerifyStatus("{"+ jsEntry("nameMISSING", "xptobundle")+ "}",
				dubTestProject, 
				dep("dub returned non-zero", null));
		
		writeDubJsonAndVerifyStatus("{"+ jsEntry("name", "xptobundle")+ "}",
				dubTestProject, 
				dep(location, null, "xptobundle", "~master", srcFolders()));
		
	}
	
	public final void writeDubJsonAndVerifyStatus(String dubJson, IScriptProject dubProject, 
			DubBundleChecker mainBundle, DubBundleChecker... deps) throws CoreException {
		LatchRunnable preWriteLatch = writeDubJson(dubProject, dubJson);
		checkModel(preWriteLatch, dubProject, mainBundle, deps);
	}
	
	public void checkModel(LatchRunnable preWriteLatch, IScriptProject dubProject, DubBundleChecker expMainBundle,
			DubBundleChecker... deps) throws CoreException {
		
		DubBundleDescription dubBundle;
//		DubBundleDescription dubBundle = getExistingDubBundleInfo(dubProject.getElementName());
//		assertTrue(!dubBundle.isResolved());
		
		preWriteLatch.releaseAll();
		DubProjectModel.getDefault().syncPendingUpdates();
		
		dubBundle = getExistingDubBundleInfo(dubProject.getElementName());
//		assertTrue(dubBundle.isResolved());
		
		IProject project = dubProject.getProject();
		
		DubBundleException error = dubBundle.getError();
		
		if(error != null) {
			
			IMarker[] markers = DubProjectModel.getDubErrorMarkers(dubProject.getProject());
			assertTrue(markers.length == 1);
			IMarker errorMarker = markers[0];
			assertTrue(errorMarker.getAttribute(IMarker.MESSAGE, "").startsWith(error.getMessage()));
			assertEquals(errorMarker.getAttribute(IMarker.SEVERITY), IMarker.SEVERITY_ERROR);
			
		} else {
			expMainBundle.check(dubBundle.getMainBundle()); // TODO always have a main bundle, even with errors
			checkRawBuildpath(dubProject.getRawBuildpath(), expMainBundle.sourceFolders);
			
			checkResolvedBuildpath(dubProject.getResolvedBuildpath(false), expMainBundle.sourceFolders, deps);
			
			IMarker[] dubErrorMarkers = DubProjectModel.getDubErrorMarkers(project);
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