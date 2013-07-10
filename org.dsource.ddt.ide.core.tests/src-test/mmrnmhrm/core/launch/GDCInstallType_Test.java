package mmrnmhrm.core.launch;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static mmrnmhrm.core.launch.DMDInstallType_Test.checkLibLocation;
import static mmrnmhrm.core.launch.DMDInstallType_Test.getLibraryLocations;

import java.io.File;

import mmrnmhrm.tests.BaseDeeTest;
import mmrnmhrm.tests.DeeCoreTestResources;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.launching.LibraryLocation;
import org.junit.Test;

public class GDCInstallType_Test extends BaseDeeTest {
	
	@Test
	public void testLibraryLocations() throws Exception { testLibraryLocations$(); }
	public void testLibraryLocations$() throws Exception {		
		CommonInstallType dmdInstallType = new GDCInstallType();
		File compilerInstallExe = DeeCoreTestResources.getWorkingDirFile(MOCK_GDC_INSTALL_PATH);
		Path compilerPath = new Path(compilerInstallExe.getAbsolutePath());
		LibraryLocation[] libLocations = getLibraryLocations(dmdInstallType, compilerPath);
		
		assertTrue(libLocations.length == 1);
		IPath compilerBasePath = compilerPath.removeLastSegments(2);
		checkLibLocation(libLocations[0], compilerBasePath, "include/d2/4.5.2");
	}
	
}
