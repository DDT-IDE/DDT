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
import mmrnmhrm.tests.DeeCoreTestResources;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.launching.InterpreterStandin;
import org.junit.Test;

public class SearchCompilersTask_Test extends CommonDeeWorkspaceTest {
	
	protected static final String MULTIPLE_IN_ONE_PATH = MOCK_DEE_COMPILERS_PATH+"_multipleInSameLocation/bin";
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		testWithPathVar(getWorkingDirFullPathString(MULTIPLE_IN_ONE_PATH));
		
		String PATH_SEP = SearchAndAddCompilersOnPathTask.getPathsSeparator();
		testWithPathVar(getWorkingDirFullPathString("__NON_EXISTING___###__") + PATH_SEP +
			getWorkingDirFullPathString(MULTIPLE_IN_ONE_PATH));
	}
	
	protected String getWorkingDirFullPathString(String workdingDirRelPath) {
		return getWorkingDirPath(workdingDirRelPath).toOSString();
	}
	
	protected void testWithPathVar(String pathsString) {
		SearchAndAddCompilersOnPathTask compilerSearchTask = new SearchAndAddCompilersOnPathTask(
			new NullProgressMonitor());
		compilerSearchTask.searchPathsString(pathsString, "_dummy_");
		
		List<InterpreterStandin> foundInstalls = compilerSearchTask.getFoundInstalls();
		assertTrue(foundInstalls.size() == 2);
		assertEquals(foundInstalls.get(0).getInstallLocation().getPath(),
			DeeCoreTestResources.getWorkingDirPath(MULTIPLE_IN_ONE_PATH).append("gdc"));
		assertEquals(foundInstalls.get(1).getInstallLocation().getPath(),
			DeeCoreTestResources.getWorkingDirPath(MULTIPLE_IN_ONE_PATH).append("ldc2"));
	}
	
}