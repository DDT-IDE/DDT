package mmrnmhrm.tests;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;

import melnorme.utilbox.core.ExceptionAdapter;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.build.DeeBuilder__Accessor;
import mmrnmhrm.core.launch.DeeDmdInstallType;
import mmrnmhrm.core.projectmodel.ProjectModelUtil;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.internal.environment.LazyFileHandle;
import org.eclipse.dltk.core.internal.environment.LocalEnvironment;
import org.eclipse.dltk.core.search.indexing.IndexManager;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.InterpreterStandin;
import org.eclipse.dltk.launching.ScriptRuntime;

import dtool.tests.DToolTestResources;

/**
 * Common Plugin Test class. 
 * Statically loads some read only projects, and prepares the workbench,
 * in case it wasn't cleared.
 */
@SuppressWarnings("restriction")
public abstract class BaseDeeTest extends BaseDeeCoreTest {
	
	static {
		DToolResourcesPluginAdapter.initialize();
		
		disableWorkspaceAutoBuild();
		
		IndexManager indexManager = ModelManager.getModelManager().getIndexManager();
		indexManager.disable();
		
		DeeBuilder__Accessor.setTestsMode(true);
		setupTestDMDInstalls();
		
		SamplePreExistingProject.checkForExistanceOfPreExistingProject();
		SampleNonDeeProject.createAndSetupNonDeeProject();
	}
	
	private static void disableWorkspaceAutoBuild() {
		IWorkspaceDescription desc = DeeCore.getWorkspace().getDescription();
		desc.setAutoBuilding(false);
		try {
			DeeCore.getWorkspace().setDescription(desc);
		} catch (CoreException e) {
			throw ExceptionAdapter.unchecked(e);
		}
		assertTrue(DeeCore.getWorkspace().isAutoBuilding() == false);
	}
	
	protected static final String DMD2_TESTDATA_PATH = "defaultDMDInstall/windows/bin/dmd.exe";
	public static final String DEFAULT_DMD2_MOCKINSTALL_NAME = "defaultDMD2Install";
	
	private static void setupTestDMDInstalls() {
		IInterpreterInstallType deeDmdInstallType 
			= ScriptRuntime.getInterpreterInstallType(DeeDmdInstallType.INSTALLTYPE_ID);
		InterpreterStandin install = new InterpreterStandin(deeDmdInstallType, DEFAULT_DMD2_MOCKINSTALL_NAME + ".id");
		
		String installPathStr = DToolTestResources.getTestResource(DMD2_TESTDATA_PATH).getAbsolutePath();
		assertTrue(new File(installPathStr).exists());
		
		install.setInstallLocation(new LazyFileHandle(LocalEnvironment.ENVIRONMENT_ID, new Path(installPathStr)));
		install.setName(DEFAULT_DMD2_MOCKINSTALL_NAME);
		install.setInterpreterArgs(null);
		install.setLibraryLocations(null); // Use default location
		install.convertToRealInterpreter();
	}
	
	public static IScriptProject createAndOpenProject(String name) throws CoreException {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		
		IProject project;
		project = workspaceRoot.getProject(name);
		if(project.exists()) {
			project.delete(true, null);
		}
		project.create(null);
		project.open(null);
		EnvironmentManager.setEnvironmentId(project, null, false);
		setupDeeProject(project);
		return DLTKCore.create(project);
	}
	
	public static void setupDeeProject(IProject project) throws CoreException {
		assertTrue(project.exists());
		IScriptProject dltkProj = DLTKCore.create(project);
		assertTrue(!dltkProj.exists()); 
		ProjectModelUtil.addNature(project, DeeNature.NATURE_ID);
		assertTrue(dltkProj.exists());
		
		IBuildpathEntry entry = DLTKCore.newContainerEntry(ScriptRuntime.newDefaultInterpreterContainerPath()
				.append(DeeDmdInstallType.INSTALLTYPE_ID).append(DEFAULT_DMD2_MOCKINSTALL_NAME)		
		);
		dltkProj.setRawBuildpath(new IBuildpathEntry[] {entry}, null);
		assertNotNull(ScriptRuntime.getInterpreterInstall(dltkProj));
	}
	
}