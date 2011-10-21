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
import java.net.URISyntaxException;

import mmrnmhrm.core.launch.GDCInstallType;
import mmrnmhrm.tests.BaseDeeTest;
import mmrnmhrm.tests.DeeCoreTestResources;
import mmrnmhrm.tests.ITestResourcesConstants;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IScriptProject;
import org.junit.Test;

public class DeeGDCProject_Test extends BaseDeeTest implements ITestResourcesConstants {
	
	@Test
	public void test_Main() throws CoreException, URISyntaxException, IOException {
		IScriptProject deeProj2 = createAndOpenDeeProject("__BuilderProject",
				GDCInstallType.INSTALLTYPE_ID, DEFAULT_GDC_MOCKINSTALL_NAME);
		IScriptProject deeProj1 = deeProj2;
		IScriptProject deeProj = deeProj1;
		IProject project = deeProj.getProject();
		
		CommonDeeBuilderListener checkBuild = new CommonDeeBuilderListener() {
			@Override
			public void processAboutToStart(String[] cmdLine) {
				assertTrue(cmdLine.length > 0);
				String compilerPath = new Path(cmdLine[0]).toString();
				assertTrue(compilerPath.endsWith(DEFAULT_GDC_MOCKINSTALL_NAME));
			}
		};
		
		try {
			DeeProjectBuilder.addDataListener(checkBuild);
			//UITestUtils.runEventLoop();
			doProjectBuild(deeProj);
			
			DeeCoreTestResources.createSrcFolderFromDeeCoreResource(TR_BUILD_SRC, project.getFolder("buildSrc"));
			doProjectBuild(deeProj);
			
		} finally {
			project.delete(true, null);
			DeeProjectBuilder.removeDataListener(checkBuild);
		}
	}
	
	protected void doProjectBuild(IScriptProject deeProj) throws CoreException {
		IProject project = deeProj.getProject();
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}
	
}
