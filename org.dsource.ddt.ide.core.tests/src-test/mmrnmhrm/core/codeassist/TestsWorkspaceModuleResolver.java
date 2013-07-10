package mmrnmhrm.core.codeassist;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import melnorme.utilbox.misc.StreamUtil;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.tests.BaseDeeTest;
import mmrnmhrm.tests.DeeCoreTestResources;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IScriptProject;

import dtool.parser.DeeParserResult;
import dtool.resolver.ResolverSourceTests.ITestsModuleResolver;

/**
 * Resolver and helper to setup tests project fixture
 */
public class TestsWorkspaceModuleResolver extends DeeProjectModuleResolver implements ITestsModuleResolver {
	
	protected final IFile customFile;
	protected final byte[] customFilePreviousContents;
	
	public TestsWorkspaceModuleResolver(IScriptProject scriptProject, 
		String moduleName, DeeParserResult parseResult) throws IOException, CoreException {
		super(scriptProject);
		
		if(moduleName == null) {
			customFile = null;
			customFilePreviousContents = null;
			return;
		}
		Path filePath = new Path(moduleName.replaceAll("\\.", "/") + ".d");
		customFile = scriptProject.getProject().getFolder("src-dtool").getFile(filePath);
		
		ByteArrayInputStream is = new ByteArrayInputStream(parseResult.source.getBytes(StringUtil.UTF8));
		if(customFile.exists()) {
			customFilePreviousContents = StreamUtil.readAllBytesFromStream(customFile.getContents());
			customFile.setContents(is, IResource.NONE, null);
		} else {
			customFilePreviousContents = null;
			customFile.create(is, IResource.NONE, null);
		}
	}
	
	@Override
	public void doCleanup() throws CoreException {
		if(customFile != null) {
			if(customFilePreviousContents == null) {
				customFile.delete(false, null);
			} else {
				ByteArrayInputStream is = new ByteArrayInputStream(customFilePreviousContents);
				customFile.setContents(is, IResource.NONE, null);
			}
		}
	}
	
	public static TestsWorkspaceModuleResolver updateTestsModuleResolver(File projectDir,
		String moduleName, DeeParserResult parseResult, TestsWorkspaceModuleResolver existingMR) throws CoreException, IOException {
		IScriptProject scriptProject;
		if(existingMR == null) {
			scriptProject = createCoreResolverTestsProject(projectDir);
		} else {
			scriptProject = existingMR.scriptProject;
		}
		return new TestsWorkspaceModuleResolver(scriptProject, moduleName, parseResult);
	}
	
	public static IScriptProject createCoreResolverTestsProject(File projectSourceDir) throws CoreException {
		IScriptProject resolverProject = BaseDeeTest.createAndOpenDeeProject("r_" + projectSourceDir.getName());
		
		DeeCoreTestResources.createSrcFolderFromDirectory(projectSourceDir, resolverProject, "src-dtool");
		return resolverProject;
	}
}