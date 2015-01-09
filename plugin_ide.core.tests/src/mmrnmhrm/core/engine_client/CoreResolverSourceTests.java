/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.engine_client;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.misc.MiscUtil.nullToOther;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import melnorme.lang.ide.core.tests.CommonCoreTest;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.misc.MiscUtil;
import mmrnmhrm.core.CommonDeeWorkspaceTestNew;
import mmrnmhrm.tests.DeeCoreTestResources;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import dtool.engine.CommonSemanticsTest;
import dtool.resolver.BaseResolverSourceTests;
import dtool.sourcegen.AnnotatedSource;

public abstract class CoreResolverSourceTests extends BaseResolverSourceTests {
	
	protected static final Location COMPILER_PATH = CommonSemanticsTest.DEFAULT_TestsCompilerInstall.getCompilerPath();
	
	static {
		MiscUtil.loadClass(CommonDeeWorkspaceTestNew.class);
	}
	
	@BeforeClass
	public static void setUpExceptionListenerStatic() throws Exception {
		CommonCoreTest.setUpExceptionListenerStatic();
	}
	@AfterClass
	public static void checkLogErrorListenerStatic() throws Throwable {
		CommonCoreTest.checkLogErrorListenerStatic();
	}
	
	@After
	@Before
	public void _checkLogErrors() throws Throwable {
		CommonCoreTest.checkLogErrors_();
	}
	
	/* -----------------  ----------------- */
	
	public CoreResolverSourceTests(String testUIDescription, File file) {
		super(testUIDescription, file);
	}
	
	protected static HashMap<String, IProject> defaultFixtureProjects = new HashMap<>();
	
	protected TestsProjectFileOverlay fixtureSourceOverlay;
	protected ISourceModule sourceModule;
	
	
	@Override
	public void prepareTestCase(String moduleName, String projectFolderName, AnnotatedSource testCase) {
		try {
			prepareTestCase_do(moduleName, projectFolderName, testCase);
		} catch(CoreException | IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	public void prepareTestCase_do(String explicitModuleName, String projectFolderName, AnnotatedSource testCase)
		throws CoreException, IOException {
		IProject project = defaultFixtureProjects.get(projectFolderName /*Can be null*/);
		
		if(project == null) {
			File projectDir = projectFolderName == null ? null : getProjectDirectory(projectFolderName);
			project = createProjectForResolverTestCase(projectDir);
			defaultFixtureProjects.put(projectFolderName, project);
		}
		
		String moduleName = nullToOther(explicitModuleName, DEFAULT_MODULE_NAME);
		fixtureSourceOverlay = new TestsProjectFileOverlay(project, moduleName, testCase.source);
		
		mr = null; // Redundant
		
		sourceModule = (ISourceModule) DLTKCore.create(fixtureSourceOverlay.overlayedFile);
		checkModuleSetupConsistency();
		
		explicitModuleName = explicitModuleName != null ? explicitModuleName : "_dummy.d";
		
		if(moduleName == CoreResolverSourceTests.DEFAULT_MODULE_NAME) {
			// Avoid doing TestsProjectFileOverlay cleanup if it is not necessary. 
			// This is done for performance reasons, 
			// since UI tests gets slow if a file with an attached editor gets deleted
			// (Opening an editor is somewhat expensive apparently)
			fixtureSourceOverlay = null;
		}
	}
	
	public static IProject createProjectForResolverTestCase(File projectSourceDir) throws CoreException {
		String projectName = projectSourceDir == null ? "r__emptyProject" : "r_" + projectSourceDir.getName();
		
		IProject project = CommonDeeWorkspaceTestNew.createLangProject(projectName, false);
		
		if(projectSourceDir == null) {
			CommonDeeWorkspaceTestNew.writeDubManifest(project, projectName, ".");
			return project;
		} else {
			DeeCoreTestResources.createFolderFromDirectory(projectSourceDir, project, "src-dtool");
			CommonDeeWorkspaceTestNew.writeDubManifest(project, projectName, "src-dtool");
		}
		return project;
	}
	
	@Override
	public void cleanupTestCase() {
		if(fixtureSourceOverlay != null) {
			fixtureSourceOverlay.cleanupChanges();
		}
	}
	
	@Override
	public void processResolverTestMetadata(AnnotatedSource testCase) {
		checkModuleSetupConsistency();
		super.processResolverTestMetadata(testCase);
	}
	
	public void checkModuleSetupConsistency() {
		assertTrue(sourceModule != null && sourceModule.getResource().exists());
		try {
			assertTrue(sourceModule.getSource().equals(testCase.source));
		} catch(ModelException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
}