package mmrnmhrm.core.projectmodel;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import mmrnmhrm.core.WorkspaceUtils;
import mmrnmhrm.tests.BaseDeeTest;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.junit.Test;

import dtool.dub.DubBundle;

public class DubProjectModelTest extends BaseDeeTest {
	
	public static void writeStringToFile(IScriptProject dubTestProject, String name, String contents) 
			throws CoreException {
		IFile file = dubTestProject.getProject().getFile(name);
		WorkspaceUtils.writeFile(file, new ByteArrayInputStream(contents.getBytes()));
	}
	
	public static String[] srcFolders(String... elems) {
		return elems;
	}
	
	public static final String DUB_TEST = "DubTest";
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		IScriptProject dubTestProject = createAndOpenDeeProject(DUB_TEST, true);
		checkBundle(dubTestProject, DUB_TEST, srcFolders());
		
		writeStringToFile(dubTestProject, "package.json","{"+
				jsEntry("name", "xptobundle")+
				"}");
		checkBundle(dubTestProject, "xptobundle", srcFolders());

		writeStringToFile(dubTestProject, "package.json", "{"+
				jsEntry("name", "xptobundle")+
				jsEntry("sourcePaths", jsArray("src", "src-test"))+
				"blah:blah}");
				
		DubProjectModel.getDefault().syncPendingUpdates();
		
		checkBundle(dubTestProject, "xptobundle", srcFolders("src", "src-test"));

		dubTestProject.getProject().delete(true, null); // cleanup
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
	
	public static void checkBundle(IScriptProject dubProject, String dubName, 
			String[] srcFolders) throws ModelException {
		
		DubBundle dubBundle = DubProjectModel.getDefault().dubBundleInfos.get(dubProject.getElementName());
		
		Path location = Paths.get(dubProject.getResource().getLocationURI());
		assertAreEqual(dubBundle.name, dubName);
		assertAreEqual(dubBundle.location, location);
		
		checkSourceFolders(dubProject.getRawBuildpath(), srcFolders);
	}
	
	public static void checkSourceFolders(IBuildpathEntry[] rawBuildpath, String[] srcFolders) throws ModelException {
		HashSet<String> sourcePaths = hashSet(srcFolders);
		
		for (IBuildpathEntry bpEntry : rawBuildpath) {
			IPath entryPath = bpEntry.getPath();
			if(entryPath.segment(0).equals(ScriptRuntime.INTERPRETER_CONTAINER)) {
				continue;
			}
			
			assertTrue(bpEntry.getEntryKind() == IBuildpathEntry.BPE_SOURCE);
			IPath folderPath = entryPath.removeFirstSegments(1); // Remove project segment
			if(sourcePaths.remove(folderPath.toString())) {
				continue;
			}
			
			assertFail();
		}
		assertTrue(sourcePaths.isEmpty());
	}
	
}