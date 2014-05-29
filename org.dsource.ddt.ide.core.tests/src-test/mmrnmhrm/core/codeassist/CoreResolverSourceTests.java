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
package mmrnmhrm.core.codeassist;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.misc.MiscUtil.nullToOther;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import melnorme.utilbox.misc.MiscUtil;
import mmrnmhrm.tests.BaseDeeTest;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.env.ModuleSource;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

import dtool.resolver.BaseResolverSourceTests;
import dtool.sourcegen.AnnotatedSource;

public abstract class CoreResolverSourceTests extends BaseResolverSourceTests {
	
	static {
		MiscUtil.loadClass(BaseDeeTest.class);
	}
	
	public CoreResolverSourceTests(String testUIDescription, File file) {
		super(testUIDescription, file);
	}
	
	protected static HashMap<String, IScriptProject> fixtureProjects = new HashMap<>();
	
	protected TestsWorkspaceModuleResolver mrTestCleanup;
	protected ISourceModule sourceModule;
	protected IModuleSource moduleSource;
	
	
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
		IScriptProject scriptProject = fixtureProjects.get(projectFolderName /*Can be null*/);
		
		if(scriptProject == null) {
			File projectDir = projectFolderName == null ? null : getProjectDirectory(projectFolderName);
			scriptProject = TestsWorkspaceModuleResolver.createProjectForResolverTestCase(projectDir);
			fixtureProjects.put(projectFolderName, scriptProject);
		}
		
		String moduleName = nullToOther(explicitModuleName, DEFAULT_MODULE_NAME);
		mrTestCleanup = new TestsWorkspaceModuleResolver(scriptProject, moduleName, testCase.source);
		mr = new DeeProjectModuleResolver(scriptProject);
		
		sourceModule = (ISourceModule) DLTKCore.create(mrTestCleanup.customFile);
		checkModuleSetupConsistency();
		
		IModelElement modelElement = projectFolderName == null ? null : sourceModule;
		moduleSource = new ModuleSource(explicitModuleName, modelElement, testCase.source);
	}
	
	@Override
	public void cleanupTestCase() {
		mrTestCleanup.cleanupChanges();
	}
	
	@Override
	public void processResolverTestMetadata(AnnotatedSource testCase) {
		checkModuleSetupConsistency();
		super.processResolverTestMetadata(testCase);
	}
	
	public void checkModuleSetupConsistency() {
		assertTrue(sourceModule != null && sourceModule.exists());
		try {
			assertTrue(sourceModule.getSource().equals(testCase.source));
		} catch(ModelException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
}