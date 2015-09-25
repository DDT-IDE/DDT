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
import melnorme.lang.ide.core.tests.CommonCoreTest;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.tests.SampleDeeProject;

public class DubProjectBuilderTest extends CommonCoreTest {
	
	protected static SampleDeeProject sampleProj;
	
	@Test
	public void test() throws Exception { test$(); }
	public void test$() throws Exception {
		try(SampleDeeProject sampleProj_ = new SampleDeeProject(getClass().getSimpleName())) {
			sampleProj = sampleProj_;
			IProject project = sampleProj.getProject();
			
			assertTrue(LangCore.getBundleModel().getProjectInfo(project) != null);
			assertTrue(LangCore.getBuildManager().getBuildInfo(project) != null);
			testBuilder();
			
			// Await buildpath update, to prevent logging of error.
			DeeCore.getDeeBundleModelManager().syncPendingUpdates();
		}
	}
	
	protected void testBuilder() throws CoreException {
		sampleProj.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
		sampleProj.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		sampleProj.getProject().build(IncrementalProjectBuilder.CLEAN_BUILD, null);
		sampleProj.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}
	
}