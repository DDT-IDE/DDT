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

import mmrnmhrm.core.compiler_installs.GDCInstallType;
import mmrnmhrm.tests.ITestResourcesConstants;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IScriptProject;

public class DeeGDCProject_Test extends DeeBuilder_Test implements ITestResourcesConstants {
	
	public DeeGDCProject_Test() {
		mockInstallTestdataPath = MOCK_GDC_INSTALL_PATH;
	}
	
	@Override
	protected IScriptProject createBuildProject(String projectName) throws CoreException {
		this.projectName = projectName;
		return createAndOpenDeeProject(projectName, true, GDCInstallType.INSTALLTYPE_ID, MOCK_GDC_INSTALL_NAME);
	}
	
	@Override
	protected void checkResponseFileForBuildSrc(IProject project) throws CoreException, IOException {
		String contents = readContentsOfIFile(project.getFile("build.rf"));
		String responseBegin =
			"-o\"bin/" + projectName+OS_EXT+"\""+NL+
			""+NL+
			"-I\"buildSrc\""+NL
		;
		assertTrue(contents.startsWith(responseBegin));
		assertTrue(contents.contains("\"buildSrc/foofile.d\""));
		assertTrue(contents.contains("\"buildSrc/packs1/mod1.d\""));
	}
	
	@Override
	protected void checkResponseForTest_SpacesInNames(String contents) {
		String responseBegin =
			"-o\"bin/"+"Spaces in Project name"+OS_EXT+"\""+NL+
			""+NL+
			"-I\"src copy\""+NL
		;
		assertTrue(contents.startsWith(responseBegin));
	}
	
}
