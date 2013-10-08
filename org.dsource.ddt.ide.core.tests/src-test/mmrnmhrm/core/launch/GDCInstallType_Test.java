package mmrnmhrm.core.launch;

import static mmrnmhrm.core.launch.DMDInstallType_Test.checkLibLocations;
import static mmrnmhrm.core.launch.DMDInstallType_Test.getLibraryLocations;

import java.io.File;

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
		File compilerInstallExe = DeeCoreTestResources.getWorkingDirFile(MOCK_GDC_INSTALL_PATH);
		Path compilerPath = new Path(compilerInstallExe.getAbsolutePath());
		LibraryLocation[] libLocations = getLibraryLocations(new GDCInstallType(), compilerPath);
		
		checkLibLocations(libLocations, compilerPath.removeLastSegments(2), 
			"include/d2/4.5.2");	
	}
	
}