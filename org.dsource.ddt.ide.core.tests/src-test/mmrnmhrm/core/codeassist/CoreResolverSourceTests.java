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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import melnorme.utilbox.misc.MiscUtil;
import mmrnmhrm.tests.BaseDeeTest;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;

import dtool.resolver.ResolverSourceTests;
import dtool.sourcegen.AnnotatedSource;

public abstract class CoreResolverSourceTests extends ResolverSourceTests {
	
	static {
		MiscUtil.loadClass(BaseDeeTest.class);
	}
	
	public CoreResolverSourceTests(String testUIDescription, File file) {
		super(testUIDescription, file);
	}
	
	protected static HashMap<String, IScriptProject> fixtureProjects = new HashMap<>();
	
	protected ISourceModule sourceModule;
	
	@Override
	public void setupTestProject(String moduleName, String projectFolderName) {
		try {
			setupTestProject_do(moduleName, projectFolderName);
		} catch(CoreException | IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	public void setupTestProject_do(String moduleName, String projectFolderName)
		throws CoreException, IOException {
		
		IScriptProject scriptProject = fixtureProjects.get(projectFolderName /*Can be null*/);
		
		if(scriptProject == null) {
			File projectDir = projectFolderName == null ? null : new File(file.getParent(), projectFolderName);
			scriptProject = TestsWorkspaceModuleResolver.createTestsWorkspaceProject(projectDir);
			fixtureProjects.put(projectFolderName, scriptProject);
		}
		
		mr = new TestsWorkspaceModuleResolver(scriptProject, moduleName, parseResult) {
			@Override
			public void doCleanupChanges() throws CoreException {
				super.doCleanupChanges();
				sourceModule = null;
			}
		};
		sourceModule = (ISourceModule) DLTKCore.create(getModuleResolver().customFile);
		assertTrue(sourceModule != null && sourceModule.exists());
		parseResult = null; // sourceModule is used instead of this.
	}
	
	protected TestsWorkspaceModuleResolver getModuleResolver() {
		return (TestsWorkspaceModuleResolver) mr; 
	}
	
	protected IScriptProject getScriptProject() {
		return getModuleResolver().scriptProject;
	}
	
	@Override
	public void processResolverTestMetadata(AnnotatedSource testCase) {
		assertTrue(sourceModule != null && sourceModule.exists());
		super.processResolverTestMetadata(testCase);
	}
	
}