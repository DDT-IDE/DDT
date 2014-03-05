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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import mmrnmhrm.core.compiler_installs.CommonInstallType;
import mmrnmhrm.core.compiler_installs.DMDInstallType;
import mmrnmhrm.tests.BaseDeeTest;
import mmrnmhrm.tests.DeeCoreTestResources;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.core.internal.environment.LocalEnvironment;
import org.eclipse.dltk.launching.LibraryLocation;
import org.eclipse.dltk.utils.PlatformFileUtils;
import org.junit.Test;

public class DMDInstallType_Test extends BaseDeeTest {
	
	@Test
	public void testLibraryLocations() throws Exception { testLibraryLocations$(); }
	public void testLibraryLocations$() throws Exception {
		Path compilerPath = DeeCoreTestResources.getWorkingDirPath(MOCK_DMD2_TESTDATA_PATH);
		LibraryLocation[] libLocations = getLibraryLocations(new DMDInstallType(), compilerPath);
		
		checkLibLocations(libLocations, compilerPath.removeLastSegments(3), 
			"src/druntime/import", "src/phobos");	
	}
	
	protected static final String MOCK_DMD2_SYSTEM_PATH = MOCK_DEE_COMPILERS_PATH+"DMDInstall-linux/usr/bin/dmd";
	protected static final String MOCK_DMD2_SYSTEM_PATH2 = MOCK_DEE_COMPILERS_PATH+"DMDInstall-linux2/usr/bin/dmd";
	
	@Test
	public void testLibraryLocUnix() throws Exception { testLibraryLocUnix$(); }
	public void testLibraryLocUnix$() throws Exception {
		Path compilerPath = DeeCoreTestResources.getWorkingDirPath(MOCK_DMD2_SYSTEM_PATH);
		LibraryLocation[] libLocations = getLibraryLocations(new DMDInstallType(), compilerPath);
		
		checkLibLocations(libLocations, compilerPath.removeLastSegments(3), 
			"usr/include/dmd/druntime/import", "usr/include/dmd/phobos");		 
	}
	
	@Test
	public void testLibraryLocUnix2() throws Exception { testLibraryLocUnix2$(); }
	public void testLibraryLocUnix2$() throws Exception {
		Path compilerPath = DeeCoreTestResources.getWorkingDirPath(MOCK_DMD2_SYSTEM_PATH2);
		LibraryLocation[] libLocations = getLibraryLocations(new DMDInstallType(), compilerPath);
		
		checkLibLocations(libLocations, compilerPath.removeLastSegments(3), 
			"include/d/dmd/druntime/import", "include/d/dmd/phobos");	
	}
	
	public static LibraryLocation[] getLibraryLocations(CommonInstallType dmdInstallType, Path compilerPath) {
		IEnvironment env = LocalEnvironment.getInstance();
		IFileHandle file = PlatformFileUtils.findAbsoluteOrEclipseRelativeFile(env, compilerPath);
		return dmdInstallType.getDefaultLibraryLocations(file);
	}
	
	public static void checkLibLocations(LibraryLocation[] libLocations, IPath compilerBasePath, 
		String... expectedPaths) {
		assertTrue(libLocations.length == expectedPaths.length);
		
		compilerBasePath = compilerBasePath.setDevice(null); // get rid of local environment stuff
		
		for (int i = 0; i < expectedPaths.length; i++) {
			String expectedPath = expectedPaths[i];
			IPath libraryPath = libLocations[i].getLibraryPath().setDevice(null);
			assertEquals(libraryPath, compilerBasePath.append(expectedPath));
		}
	}
	
}