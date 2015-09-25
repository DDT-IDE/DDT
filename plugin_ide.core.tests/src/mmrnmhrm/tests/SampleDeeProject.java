/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.tests;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static mmrnmhrm.tests.ITestResourcesConstants.TR_SAMPLE_SRC1;
import melnorme.lang.ide.core.LangNature;
import melnorme.lang.ide.core.tests.CommonCoreTest;
import melnorme.lang.ide.core.tests.LangCoreTestResources;
import mmrnmhrm.core.CommonDeeWorkspaceTestNew;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public class SampleDeeProject implements AutoCloseable {
	
	public final IProject project;
	
	public SampleDeeProject(String name) throws CoreException {
		project = CommonCoreTest.createAndOpenProject(name, true);
		fillProject();
		CommonDeeWorkspaceTestNew.setupLangProject(project, false);
		assertTrue(project.getNature(LangNature.NATURE_ID) != null);
	}
	
	protected void fillProject() throws CoreException {
		LangCoreTestResources.createFolderFromCoreTestsResource(TR_SAMPLE_SRC1, project.getFolder(TR_SAMPLE_SRC1));
		
		CommonDeeWorkspaceTestNew.writeDubManifest(project, project.getName(), TR_SAMPLE_SRC1);
	}
	
	public IProject getProject() {
		return project;
	}
	
	public void cleanUp() throws CoreException {
		project.delete(true, null);
	}
	
	@Override
	public void close() throws CoreException {
		cleanUp();
	}
	
}