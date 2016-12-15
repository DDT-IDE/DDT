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
package mmrnmhrm.core.build;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.operations.build.BuildManager;
import melnorme.lang.ide.core.project_model.LangBundleModel;
import melnorme.lang.ide.core.tests.CommonCoreTest;
import mmrnmhrm.tests.SampleDeeProject;

// TODO: need to review since now building is asynch, 
// and goes one behind the Eclipse workspace build
public class DubProjectBuilderTest extends CommonCoreTest {
	
	protected BuildManager buildManager = LangCore.getBuildManager();
	protected LangBundleModel bundleModel = LangCore.getBundleModel();
	
	protected static SampleDeeProject sampleProj;
	
	@Test
	public void test() throws Exception { test$(); }
	public void test$() throws Exception {
		try(SampleDeeProject sampleProj_ = new SampleDeeProject(getClass().getSimpleName())) {
			sampleProj = sampleProj_;
			IProject project = sampleProj.getProject();
			
			assertTrue(bundleModel.getBundleInfo(project) != null);
			assertTrue(buildManager.getBuildInfo(project) != null);
			testBuilder();
			
			// Await buildpath update, to prevent logging of error.
			LangCore.deeBundleModelManager().syncPendingUpdates();
		}
	}
	
	protected void testBuilder() throws CoreException {
		sampleProj.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
		sampleProj.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		sampleProj.getProject().build(IncrementalProjectBuilder.CLEAN_BUILD, null);
		sampleProj.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}
	
}