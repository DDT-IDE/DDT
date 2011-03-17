/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.build;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import melnorme.utilbox.misc.StreamUtil;
import melnorme.utilbox.misc.StringUtil;
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

public class DeeBuilder_Test extends BaseDeeTest {
	
	protected IScriptProject createBuildProject(String projectName) throws CoreException {
		IScriptProject deeProj = createAndOpenProject(projectName);
		
		DeeProjectOptions deeProjectInfo = DeeModel.getDeeProjectInfo(deeProj);
		
		deeProjectInfo.compilerOptions.buildToolCmdLine = deeProjectInfo.compilerOptions.buildToolCmdLine;
		return deeProj;
	}
	
	@Test
	public void test() throws CoreException, URISyntaxException, IOException {
		IScriptProject deeProj = createBuildProject("__BuilderProject");
		IProject project = deeProj.getProject();
		
		CommonDeeBuilderListener checkBuild = new CommonDeeBuilderListener() {
			@Override
			public void processAboutToStart(String[] cmdLine) {
				String compilerPath = new Path(cmdLine[0]).toString();
				assertTrue(compilerPath.endsWith(DMD2_TESTDATA_PATH));
			}
		};
		
		try {
			DeeProjectBuilder.addDataListener(checkBuild);
			//UITestUtils.runEventLoop();
			doProjectBuild(deeProj);
			
			DeeCoreTestUtils.createSrcFolderInProject(ITestDataConstants.BUILD_SRC, project.getFolder("buildSrc"));
			doProjectBuild(deeProj);
			
			
			DeeCoreTestUtils.createSrcFolderInProject(ITestDataConstants.SAMPLE_SRC1, project.getFolder("src1"));
			doProjectBuild(deeProj);
			
			DeeCoreTestUtils.createSrcFolderInProject(ITestDataConstants.SAMPLE_SRC3, project.getFolder("src3"));
			doProjectBuild(deeProj);
			
			DeeCoreTestUtils.createSrcFolderInProject(ITestDataConstants.SAMPLE_SRC1, project.getFolder("src1 copy"));
			doProjectBuild(deeProj);
			
			DeeModel.getDeeProjectInfo(deeProj).compilerOptions.outputDir = new Path("out");
			doProjectBuild(deeProj);
		} finally {
			project.delete(true, null);
			DeeProjectBuilder.removeDataListener(checkBuild);
		}
	}
	
	protected abstract class RunWithTemporaryProject {
		String projName;
		
		public RunWithTemporaryProject(String projName) throws Exception {
			IScriptProject deeProj = createBuildProject(projName);
			IProject project = deeProj.getProject();
			try {
				run(deeProj, project);
			} finally {
				project.delete(true, null);
			}
		}
		
		protected abstract void run(IScriptProject deeProj, IProject project) throws Exception;
	}
	
	@Test
	public void test_OutputFolderInsideSrcFolder() throws Exception {
		new RunWithTemporaryProject("__BuilderProject") {
			@Override
			protected void run(IScriptProject deeProj, IProject project) throws Exception {
				doProjectBuild(deeProj);
				// Output Folder Inside src project
				DeeCoreTestUtils.createSrcFolderInProject(ITestDataConstants.BUILD_SRC, project);
				doProjectBuild(deeProj);
			}
		};
	}
	
	protected void doProjectBuild(IScriptProject deeProj) throws CoreException {
		IProject project = deeProj.getProject();
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}
	
	public static final String SEP = System.getProperty("file.separator");
	public static final String NL = "\n";
	
	@Test
	public void test_SpacesInNames() throws Exception {
		new RunWithTemporaryProject("Spaces in Project name") {
			@Override
			protected void run(IScriptProject deeProj, IProject project) throws Exception {
				doProjectBuild(deeProj);
				DeeCoreTestUtils.
				createSrcFolderInProject(ITestDataConstants.SAMPLE_SRC1, project.getFolder("src1 copy"));
				doProjectBuild(deeProj);
				
				InputStream buildFileIS = project.getFile("build.rf").getContents();
				String contents = StreamUtil.readStringFromReader(new InputStreamReader(buildFileIS, StringUtil.UTF8));
				String responseBegin =
					"-od\"bin\""+NL+
					"-of\"bin"+SEP+"Spaces in Project name.exe\""+NL+
					""+NL+
					"-I\"src1 copy\""+NL
					;
				assertTrue(contents.startsWith(responseBegin));
			}
		};
	}
	
}
