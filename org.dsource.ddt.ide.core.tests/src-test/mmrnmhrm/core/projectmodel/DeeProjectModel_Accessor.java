package mmrnmhrm.core.projectmodel;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import mmrnmhrm.core.compiler_installs.CommonDeeInstall;

import org.eclipse.dltk.core.IScriptProject;

public class DeeProjectModel_Accessor {
	
	public static CommonDeeInstall checkInstall(IScriptProject project, String installTypeId, String installId) {
		CommonDeeInstall install = DeeProjectModel.getInstallForProject(project);
		assertNotNull(install);
		assertTrue(install.getInterpreterInstallType().getId().endsWith(installTypeId));
		assertTrue(install.getId().startsWith(installId));
		return install;
	}
	
}
