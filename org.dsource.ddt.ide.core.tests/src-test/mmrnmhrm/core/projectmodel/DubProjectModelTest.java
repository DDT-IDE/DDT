package mmrnmhrm.core.projectmodel;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import melnorme.utilbox.concurrency.IExecutorAgent;
import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.misc.MiscUtil.InvalidPathExceptionX;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.WorkspaceUtils;
import mmrnmhrm.tests.BaseDeeTest;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.junit.Test;

import dtool.dub.DubBundle;
import dtool.dub.DubBundle.DubBundleDescription;
import dtool.dub.DubParserTest;

public class DubProjectModelTest extends BaseDeeTest {
	
	public static String readFileContents(Path path) throws IOException {
		assertTrue(path.isAbsolute());
		return FileUtil.readStringFromFile(path.toFile(), StringUtil.UTF8);
	}
	
	public static void writeStringToFile(IScriptProject dubTestProject, String name, String contents) 
			throws CoreException {
		IFile file = dubTestProject.getProject().getFile(name);
		WorkspaceUtils.writeFile(file, new ByteArrayInputStream(contents.getBytes()));
	}
	
	
	public static String jsEntry(String key, String value) {
		return "\""+key+"\" : \""+value+"\",";
	}
	
	public static String jsEntry(String key, CharSequence value) {
		return "\"" + key + "\" : " + jsToString(value) + ",";
	}
	
	private static String jsToString(CharSequence value) {
		if(value instanceof String) {
			return "\""+value+"\"";
		} else {
			return value.toString();
		}
	}
	
	public static StringBuilder jsArray(CharSequence... objs) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (CharSequence obj : objs) {
			sb.append(jsToString(obj));
			sb.append(",");
		}
		sb.append("]");
		return sb;
	}
	
	protected static IExecutorAgent getDubExecutorAgent() {
		return DubProjectModel.getDefault().internal_getExecutorAgent();
	}
	
	/* ************************************ */
	
	public static final String DUB_TEST = "DubTest";
	public static Path DUB_WORKSPACE = DubParserTest.DUB_WORKSPACE;
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		
		long taskCount = getDubExecutorAgent().getSubmittedTaskCount();
		IScriptProject dubTestProject = createAndOpenDeeProject(DUB_TEST, true);
		
		assertTrue(getDubExecutorAgent().getSubmittedTaskCount() == taskCount); // check no change
		assertTrue(DubProjectModel.getDefault().getBundleInfo(DUB_TEST) == null);
		
		try {
			runBasicTest(dubTestProject);
		} finally {
			dubTestProject.getProject().delete(true, null); // cleanup
		}
	}
	
	public void runBasicTest(IScriptProject dubTestProject) throws Exception {
		
		writeJsonFile(dubTestProject, "{"+
				jsEntry("name", "xptobundle")+
				"}");
		checkBundle(dubTestProject, "xptobundle", srcFolders());

		writeJsonFile(dubTestProject, "{"+
				jsEntry("name", "xptobundle")+
				jsEntry("importPaths", jsArray("src", "src-test"))+
				"\"blah\":null}");
		checkBundle(dubTestProject, "xptobundle", srcFolders("src", "src-test"));
		
		
		writeJsonFile(dubTestProject, 
				readFileContents(DUB_WORKSPACE.resolve("XptoBundle/package.json")));
		
		checkBundle(dubTestProject, "xptobundle", srcFolders("src", "src-test"), 
				dep(DUB_WORKSPACE.resolve("bar_lib"), "source"),
				dep(DUB_WORKSPACE.resolve("foo_lib"), "src"),
				dep(DUB_WORKSPACE.resolve("foo_lib"), "src2")
				);
		
	}

	protected static void writeJsonFile(IScriptProject dubTestProject, String contents) throws CoreException {
		writeStringToFile(dubTestProject, "package.json", contents);
		DubProjectModel.getDefault().syncPendingUpdates();
	}
	
	public static String[] srcFolders(String... elems) {
		return elems;
	}
	
	public static DubBundle dep(Path baseLocation, String... elems) throws InvalidPathExceptionX {
		Path[] paths = new Path[elems.length];
		for (int i = 0; i < elems.length; i++) {
			paths[i] = MiscUtil.createPath(elems[i]);
			
		}
		return new DubBundle(baseLocation, "", null, paths, null, null, null);
	}
	
	public static void checkBundle(IScriptProject dubProject, String dubName, String[] srcFolders,
			DubBundle... deps) throws ModelException {
		DubBundleDescription dubBundle = DubProjectModel.getDefault().getBundleInfo(dubProject.getElementName());
		
		Path location = Paths.get(dubProject.getResource().getLocationURI());
		assertAreEqual(dubBundle.getMainBundle().name, dubName);
		assertAreEqual(dubBundle.getMainBundle().location, location);
		
		checkRawBuildpath(dubProject.getRawBuildpath(), srcFolders);
		
		checkResolvedBuildpath(dubProject.getResolvedBuildpath(false), srcFolders, deps);
	}
	
	public static void checkRawBuildpath(IBuildpathEntry[] rawBuildpath, String[] srcFolders) throws ModelException {
		HashSet<String> sourcePaths = hashSet(srcFolders);
		
		for (IBuildpathEntry bpEntry : rawBuildpath) {
			IPath entryPath = bpEntry.getPath();
			if(entryPath.segment(0).equals(ScriptRuntime.INTERPRETER_CONTAINER)) {
				continue;
			}
			
			if((bpEntry.getEntryKind() == IBuildpathEntry.BPE_CONTAINER)) {
				assertTrue(entryPath.toString() .equals(DubContainerInitializer.ID));
				continue;
			}
			
			assertTrue(bpEntry.getEntryKind() == IBuildpathEntry.BPE_SOURCE);
			assertTrue(bpEntry.isExternal() == false);
			IPath folderPath = entryPath.removeFirstSegments(1); // Remove project segment
			assertTrue(sourcePaths.remove(folderPath.toString()));
			continue;
		}
		
		// Ensure we matched every entry
		assertTrue(sourcePaths.isEmpty());
	}
	
	public static void checkResolvedBuildpath(IBuildpathEntry[] buildpath, String[] srcFolders, 
			DubBundle[] deps) throws ModelException {
		HashSet<String> sourcePaths = hashSet(srcFolders);
		
		HashSet<Path> depSourcePaths = new HashSet<>();
		for (DubBundle bundleDep : deps) {
			for (Path path : bundleDep.getRawSourceFolders()) {
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
			assertTrue(sourcePaths.remove(folderPath.toString()));
			continue;
		}
		
		// Ensure we matched every entry
		assertTrue(sourcePaths.isEmpty());
		assertTrue(depSourcePaths == null || depSourcePaths.isEmpty());
	}
	
}