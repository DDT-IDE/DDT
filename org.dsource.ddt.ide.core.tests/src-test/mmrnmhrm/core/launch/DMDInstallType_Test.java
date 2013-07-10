package mmrnmhrm.core.launch;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;

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
		DMDInstallType dmdInstallType = new DMDInstallType();
		File compilerInstallExe = DeeCoreTestResources.getWorkingDirFile(MOCK_DMD2_TESTDATA_PATH);
		Path compilerPath = new Path(compilerInstallExe.getAbsolutePath());
		LibraryLocation[] libLocations = getLibraryLocations(dmdInstallType, compilerPath);
		
		assertTrue(libLocations.length == 2);
		IPath compilerBasePath = compilerPath.removeLastSegments(3);
		checkLibLocation(libLocations[0], compilerBasePath, "src/druntime/import");
		checkLibLocation(libLocations[1], compilerBasePath, "src/phobos");
	}
	
	protected static final String MOCK_DMD2SYSTEM_PATH = MOCK_DEE_COMPILERS_PATH+"DMDInstall-system/usr/bin/dmd";
	
	@Test
	public void testLibraryLocUnix() throws Exception { testLibraryLocUnix$(); }
	public void testLibraryLocUnix$() throws Exception {
		DMDInstallType dmdInstallType = new DMDInstallType();
		File compilerInstallExe = DeeCoreTestResources.getWorkingDirFile(MOCK_DMD2SYSTEM_PATH);
		Path compilerPath = new Path(compilerInstallExe.getAbsolutePath());
		LibraryLocation[] libLocations = getLibraryLocations(dmdInstallType, compilerPath);

		assertTrue(libLocations.length == 2);
		IPath compilerBasePath = compilerPath.removeLastSegments(3);
		checkLibLocation(libLocations[0], compilerBasePath, "include/d/dmd/druntime/import");
		checkLibLocation(libLocations[1], compilerBasePath, "include/d/dmd/phobos");
	}
	
	public static LibraryLocation[] getLibraryLocations(CommonInstallType dmdInstallType, Path compilerPath) {
		IEnvironment env = LocalEnvironment.getInstance();
		IFileHandle file = PlatformFileUtils.findAbsoluteOrEclipseRelativeFile(env, compilerPath);
		return dmdInstallType.getDefaultLibraryLocations(file);
	}
	
	
	public static void checkLibLocation(LibraryLocation libLocation, IPath compilerBasePath, String string) {
		IPath libraryPath = libLocation.getLibraryPath();
		assertEqualArrays(libraryPath.segments(), compilerBasePath.append(string).segments());
	}
	
}
