package mmrnmhrm.tests;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;

import melnorme.utilbox.core.ExceptionAdapter;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.build.DeeBuilder__Accessor;
import mmrnmhrm.core.launch.DeeDmdInstallType;
import mmrnmhrm.core.launch.GDCInstallType;
import mmrnmhrm.core.projectmodel.ProjectModelUtil;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.internal.environment.LazyFileHandle;
import org.eclipse.dltk.core.internal.environment.LocalEnvironment;
import org.eclipse.dltk.core.search.indexing.IndexManager;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.InterpreterStandin;
import org.eclipse.dltk.launching.ScriptRuntime;

import dtool.tests.DToolTestResources;

/**
 * Initializes a common Dee test setup:
 * - No autobuild, no DLTK indexer, creates mock compiler installs. 
 * - Creates common sample workspace projects.
 * Statically loads some read only projects, and prepares the workbench, in case it wasn't cleared.
 */
public abstract class BaseDeeTest extends BaseDeeCoreTest {
	
	static {
		DToolResourcesPluginAdapter.initialize();
		
		disableWorkspaceAutoBuild();
		disableDLTKIndexer();
		
		DeeBuilder__Accessor.setTestsMode(true);
		setupTestDeeInstalls();
		
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
	
	@SuppressWarnings("restriction")
	public static void disableDLTKIndexer() {
		IndexManager indexManager = org.eclipse.dltk.internal.core.ModelManager.getModelManager().getIndexManager();
		indexManager.disable();
	}
	
	protected static final String DMD2INSTALL_TESTDATA_PATH = "deeCompilerInstalls/DMDInstall/windows/bin/dmd.exe";
	public static final String DEFAULT_DMD2_MOCKINSTALL_NAME = "defaultDMD2Install";
	
	protected static void setupTestDeeInstalls() {
		createFakeDeeInstall(
				DeeDmdInstallType.INSTALLTYPE_ID, 
				DEFAULT_DMD2_MOCKINSTALL_NAME, 
				DMD2INSTALL_TESTDATA_PATH);
		
		createFakeDeeInstall(
				GDCInstallType.INSTALLTYPE_ID, 
				"gdcInstall", 
				"deeCompilerInstalls/gdcInstall/bin/gdc");
	}
	
	protected static void createFakeDeeInstall(String installTypeId, String installName, String installExePath) {
		IInterpreterInstallType deeDmdInstallType = ScriptRuntime.getInterpreterInstallType(installTypeId);
		InterpreterStandin install = new InterpreterStandin(deeDmdInstallType, installName + ".id");
		
		String installPathStr = DToolTestResources.getTestResource(installExePath).getAbsolutePath();
		assertTrue(new File(installPathStr).exists());
		
		install.setInstallLocation(new LazyFileHandle(LocalEnvironment.ENVIRONMENT_ID, new Path(installPathStr)));
		install.setName(installName);
		install.setInterpreterArgs(null);
		install.setLibraryLocations(null); // Use default locations
		install.convertToRealInterpreter();
	}
	
	public static IScriptProject createAndOpenDeeProject(String name) throws CoreException {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		
		final IProject project;
		project = workspaceRoot.getProject(name);
		if(project.exists()) {
			project.delete(true, null);
		}
		project.create(null);
		project.open(null);
		EnvironmentManager.setEnvironmentId(project, null, false);
		project.getWorkspace().run(new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				setupDeeProject(project);
			}
		}, null, IWorkspace.AVOID_UPDATE, null);
		IScriptProject scriptProject = DLTKCore.create(project);
//		scriptProject.setOption(DLTKCore.INDEXER_ENABLED, false ? DLTKCore.ENABLED : DLTKCore.DISABLED);
//		scriptProject.setOption(DLTKCore.BUILDER_ENABLED, false ? DLTKCore.ENABLED : DLTKCore.DISABLED);
		return scriptProject;
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