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
package mmrnmhrm.core.projectmodel;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static mmrnmhrm.tests.DeeCoreTestResources.getWorkingDirPath;

import java.util.List;

import mmrnmhrm.tests.CommonDeeWorkspaceTest;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.launching.InterpreterStandin;
import org.junit.Test;

import dtool.tests.MockCompilerInstalls;

public class SearchCompilersTask_Test extends CommonDeeWorkspaceTest {
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		testWithPathVar(MockCompilerInstalls.MULTIPLE_IN_ONE_PATH.toString());
		
		String PATH_SEP = SearchAndAddCompilersOnPathTask.getPathsSeparator();
		testWithPathVar(getWorkingDirPath("__NON_EXISTING___###__").toOSString() + PATH_SEP +
			MockCompilerInstalls.MULTIPLE_IN_ONE_PATH.toString());
	}
	
	protected void testWithPathVar(String pathsString) {
		SearchAndAddCompilersOnPathTask compilerSearchTask = new SearchAndAddCompilersOnPathTask(
			new NullProgressMonitor());
		compilerSearchTask.searchPathsString(pathsString, "_dummy_");
		
		List<InterpreterStandin> foundInstalls = compilerSearchTask.getFoundInstalls();
		assertTrue(foundInstalls.size() == 2);
		assertEquals(foundInstalls.get(0).getInstallLocation().getPath(),
			epath(MockCompilerInstalls.MULTIPLE_IN_ONE_PATH).append("gdc"));
		assertEquals(foundInstalls.get(1).getInstallLocation().getPath(),
			epath(MockCompilerInstalls.MULTIPLE_IN_ONE_PATH).append("ldc2"));
	}
	
}