/*******************************************************************************
 * Copyright (c) 2010, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.compiler_installs;

import static mmrnmhrm.core.compiler_installs.DMDInstallType_Test.checkLibLocations;
import static mmrnmhrm.core.compiler_installs.DMDInstallType_Test.getLibraryLocations;
import mmrnmhrm.core.compiler_installs.GDCInstallType;
import mmrnmhrm.tests.CommonDeeWorkspaceTest;

import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.launching.LibraryLocation;
import org.junit.Test;

import dtool.engine.CompilerInstallDetector_Test;

public class GDCInstallType_Test extends CommonDeeWorkspaceTest {
	
	@Test
	public void testLibraryLocations() throws Exception { testLibraryLocations$(); }
	public void testLibraryLocations$() throws Exception {		
		Path compilerPath = epath(CompilerInstallDetector_Test.MOCK_GDC_CMDPATH);
		LibraryLocation[] libLocations = getLibraryLocations(new GDCInstallType(), compilerPath);
		
		checkLibLocations(libLocations, compilerPath.removeLastSegments(2), 
			"include/d2/4.5.2");	
	}
	
	@Test
	public void stestLibraryLocations_2() throws Exception { stestLibraryLocations_2$(); }
	public void stestLibraryLocations_2$() throws Exception {
		Path compilerPath = epath(CompilerInstallDetector_Test.MOCK_GDC_B_CMDPATH);
		LibraryLocation[] libLocations = getLibraryLocations(new GDCInstallType(), compilerPath);
		
		checkLibLocations(libLocations, compilerPath.removeLastSegments(2), 
			"include/d/4.6.1");			 
	}
	
}