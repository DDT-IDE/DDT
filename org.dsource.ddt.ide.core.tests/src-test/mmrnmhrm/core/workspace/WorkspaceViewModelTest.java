/*******************************************************************************
 * Copyright (c) 2014, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.workspace;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import mmrnmhrm.core.workspace.viewmodel.DubDependenciesContainer;
import mmrnmhrm.core.workspace.viewmodel.DubDependencyElement;

import org.eclipse.core.resources.IProject;
import org.junit.Test;

import dtool.dub.CommonDubTest;
import dtool.dub.DubBundle;
import dtool.dub.DubBundleDescription;

public class WorkspaceViewModelTest extends AbstractDubModelManagerTest {
	
	public static final String DUB_TEST = "DubTest";
	public static final String DUB_LIB = "DubLib";
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		
		IProject libProject = createAndOpenDeeProject(DUB_LIB, true).getProject();
		libProject.getFolder("src").create(true, true, null);
		
		IProject project = createAndOpenDeeProject(DUB_TEST, true).getProject();
		project.getFolder("source").create(true, true, null);
		
		DubBundle mainBundle = new DubBundle(bpath(project), "dub_test", null,
			"~master", array("source"), CommonDubTest.paths("source"), 
			null,
			array(new DubBundle.DubDependecyRef("dub_lib", null)), 
			null, null);
		
		DubBundle[] bundleDeps = array(new DubBundle(bpath(libProject), "dub_lib", null,
			"~master", array("src"), CommonDubTest.paths("src"), 
			null,
			null, 
			null, null));
		
		DubBundleDescription bundleDesc = new DubBundleDescription(mainBundle, bundleDeps);
		
		getModelManager().addProjectInfo(project, bundleDesc);
		
		DubDependenciesContainer dubContainer = getDubContainer(project);
		assertTrue(dubContainer.getChildren().length == 1);
		
		DubDependencyElement libDepElement = 
				assertCast(dubContainer.getChildren()[0], DubDependencyElement.class);
		
		assertTrue(libDepElement.getChildren().length == 1);
	}
	
}