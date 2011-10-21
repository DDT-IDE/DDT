package mmrnmhrm.core.projectmodel;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.eclipse.dltk.core.IScriptProject;

import mmrnmhrm.core.launch.CommonDeeInstall;
import mmrnmhrm.tests.BaseDeeTest;

public class DeeProjectModelTest extends BaseDeeTest {
	
	public static CommonDeeInstall checkInstall(IScriptProject project, String installTypeId, String installId) {
		CommonDeeInstall install = DeeProjectModel.getInstallForProject(project);
		assertNotNull(install);
		assertTrue(install.getInterpreterInstallType().getId().endsWith(installTypeId));
		assertTrue(install.getId().startsWith(installId));
		return install;
	}
	
}
