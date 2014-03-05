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
package mmrnmhrm.core.launch;

import static mmrnmhrm.core.launch.DMDInstallType_Test.checkLibLocations;
import static mmrnmhrm.core.launch.DMDInstallType_Test.getLibraryLocations;
import mmrnmhrm.core.compiler_installs.GDCInstallType;
import mmrnmhrm.tests.BaseDeeTest;
import mmrnmhrm.tests.DeeCoreTestResources;

import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.launching.LibraryLocation;
import org.junit.Test;

public class GDCInstallType_Test extends BaseDeeTest {
	
	@Test
	public void testLibraryLocations() throws Exception { testLibraryLocations$(); }
	public void testLibraryLocations$() throws Exception {		
		Path compilerPath = DeeCoreTestResources.getWorkingDirPath(MOCK_GDC_INSTALL_PATH);
		LibraryLocation[] libLocations = getLibraryLocations(new GDCInstallType(), compilerPath);
		
		checkLibLocations(libLocations, compilerPath.removeLastSegments(2), 
			"include/d2/4.5.2");	
	}
	
	protected static final String MOCK_GDC_INSTALL_PATH_B = MOCK_DEE_COMPILERS_PATH+"gdcInstallB/bin/gdc";
	
	@Test
	public void stestLibraryLocations_2() throws Exception { stestLibraryLocations_2$(); }
	public void stestLibraryLocations_2$() throws Exception {
		Path compilerPath = DeeCoreTestResources.getWorkingDirPath(MOCK_GDC_INSTALL_PATH_B);
		LibraryLocation[] libLocations = getLibraryLocations(new GDCInstallType(), compilerPath);
		
		checkLibLocations(libLocations, compilerPath.removeLastSegments(2), 
			"include/d/4.6.1");			 
	}
	
}