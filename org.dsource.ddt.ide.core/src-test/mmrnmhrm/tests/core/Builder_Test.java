package mmrnmhrm.tests.core;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import mmrnmhrm.core.build.CommonDeeBuilderListener;
import mmrnmhrm.core.build.DeeProjectBuilder;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeProjectOptions;
import mmrnmhrm.tests.BaseDeeTest;
import mmrnmhrm.tests.DeeCoreTestUtils;
import mmrnmhrm.tests.ITestDataConstants;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IScriptProject;
import org.junit.Test;

public class Builder_Test extends BaseDeeTest {
	
	protected IScriptProject createBuildProject() throws CoreException {
		IScriptProject deeProj = createAndOpenProject("__BuilderProject");
		
		DeeProjectOptions deeProjectInfo = DeeModel.getDeeProjectInfo(deeProj);
		
		deeProjectInfo.compilerOptions.buildToolCmdLine = deeProjectInfo.compilerOptions.buildToolCmdLine;
		return deeProj;
	}
	
	@Test
	public void test() throws CoreException, URISyntaxException, IOException {
		IScriptProject deeProj = createBuildProject();
		IProject project = deeProj.getProject();
		
		CommonDeeBuilderListener buildListener = new CommonDeeBuilderListener() {
			@Override
			public void processAboutToStart(String[] cmdLine) {
				String compilerPath = new Path(cmdLine[0]).toString();
				assertTrue(compilerPath.endsWith(DMD2_TESTDATA_PATH));
			}
		};
		
		try {
			DeeProjectBuilder.addDataListener(buildListener);
			//UITestUtils.runEventLoop();
			doProjectBuild(deeProj);
			
			DeeCoreTestUtils.createSrcFolderInProject(ITestDataConstants.BUILD_SRC, project.getFolder("buildSrc"));
			doProjectBuild(deeProj);
			
			
			DeeCoreTestUtils.createSrcFolderInProject(ITestDataConstants.SAMPLE_SRC1, project.getFolder("src1"));
			doProjectBuild(deeProj);
			
			DeeCoreTestUtils.createSrcFolderInProject(ITestDataConstants.SAMPLE_SRC3, project.getFolder("src3"));
			doProjectBuild(deeProj);
			
			DeeCoreTestUtils.createSrcFolderInProject(ITestDataConstants.SAMPLE_SRC1, project.getFolder("src1-copy"));
			doProjectBuild(deeProj);
			
			DeeModel.getDeeProjectInfo(deeProj).compilerOptions.outputDir = new Path("out");
			doProjectBuild(deeProj);
		} finally {
			project.delete(true, null);
			DeeProjectBuilder.removeDataListener(buildListener);
		}
	}
	
	@Test
	public void test_OutputFolderInsideSrcFolder() throws CoreException, URISyntaxException, IOException {
		IScriptProject deeProj = createBuildProject();
		IProject project = deeProj.getProject();
		
		try {
			doProjectBuild(deeProj);
			DeeCoreTestUtils.createSrcFolderInProject(ITestDataConstants.BUILD_SRC, project);
			doProjectBuild(deeProj);
			
		} finally {
			project.delete(true, null);
		}
	}
	
	private void doProjectBuild(IScriptProject deeProj) throws CoreException {
		IProject project = deeProj.getProject();
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}
}
