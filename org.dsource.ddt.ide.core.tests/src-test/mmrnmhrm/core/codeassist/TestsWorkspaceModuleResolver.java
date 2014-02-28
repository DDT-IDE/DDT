package mmrnmhrm.core.codeassist;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import melnorme.utilbox.misc.StreamUtil;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.tests.BaseDeeTest;
import mmrnmhrm.tests.DeeCoreTestResources;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;

import dtool.resolver.BaseResolverSourceTests.ITestsModuleResolver;

/**
 * Module resolver helper for the ResolverSourceTests fixture
 */
public class TestsWorkspaceModuleResolver extends DeeProjectModuleResolver implements ITestsModuleResolver {
	
	protected final IFile customFile;
	protected final boolean customFileRequiresCleanup;
	protected final byte[] customFilePreviousContents;
	protected final IScriptProject scriptProject;
	
	public TestsWorkspaceModuleResolver(IScriptProject scriptProject, String moduleName, String source) 
		throws IOException, CoreException {
		super(scriptProject);
		this.scriptProject = scriptProject;
		
		if(moduleName == null) {
			moduleName = CoreResolverSourceTests.DEFAULT_MODULE_NAME; 
		}
		Path filePath = new Path(moduleName.replaceAll("\\.", "/") + ".d");
		IContainer srcFolder = scriptProject.getProject().getFolder("src-dtool");
		if(!srcFolder.exists()) {
			srcFolder = scriptProject.getProject();
		}
		customFile = srcFolder.getFile(filePath);
		
		if(moduleName == CoreResolverSourceTests.DEFAULT_MODULE_NAME) {
			// Avoid doing custom file cleanup if possible. This is done for performance reasons,
			// since UI tests gets slow if a file with an attached editor gets deleted
			// (Opening an editor is somewhat expensive apparently)
			customFileRequiresCleanup = false;
		} else {
			customFileRequiresCleanup = true;
		}
		
		ByteArrayInputStream is = new ByteArrayInputStream(source.getBytes(StringUtil.UTF8));
		if(customFile.exists()) {
			customFilePreviousContents = StreamUtil.readAllBytesFromStream(customFile.getContents()).toByteArray();
			customFile.setContents(is, IResource.NONE, null);
		} else {
			customFilePreviousContents = null;
			customFile.create(is, IResource.NONE, null);
		}
	}
	
	@Override
	public void cleanupChanges() {
		try {
			doCleanupChanges();
		} catch(CoreException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	public void doCleanupChanges() throws CoreException {
		if(customFile != null && customFileRequiresCleanup) {
			if(customFilePreviousContents == null) {
				customFile.delete(false, null);
			} else {
				ByteArrayInputStream is = new ByteArrayInputStream(customFilePreviousContents);
				customFile.setContents(is, IResource.NONE, null);
			}
		}
	}
	
	public static IScriptProject createTestsWorkspaceProject(File projectSourceDir) throws CoreException {
		String projectName = projectSourceDir == null ? "r__emptyProject" : "r_" + projectSourceDir.getName();
		
		IScriptProject resolverProject = BaseDeeTest.createAndOpenDeeProject(projectName);
		resolverProject.setRawBuildpath(new IBuildpathEntry[] {}, null); // Remove library entry
		
		if(projectSourceDir == null) {
			DeeCoreTestResources.addSourceFolder(resolverProject.getProject(), null);
			return resolverProject;
		}
		DeeCoreTestResources.createSrcFolderFromDirectory(projectSourceDir, resolverProject, "src-dtool");
		return resolverProject;
	}
}