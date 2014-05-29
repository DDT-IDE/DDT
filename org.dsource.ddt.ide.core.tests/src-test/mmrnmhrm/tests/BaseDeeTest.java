package mmrnmhrm.tests;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;

import melnorme.lang.ide.core.tests.CommonCoreTest;
import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.utilbox.core.ExceptionAdapter;
import melnorme.utilbox.tests.TestsWorkingDir;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.compiler_installs.DMDInstallType;
import mmrnmhrm.core.compiler_installs.GDCInstallType;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceDescription;
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
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.InterpreterStandin;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.junit.After;
import org.junit.Before;

/**
 * Initializes a common Dee test setup:
 * - No autobuild, no DLTK indexer, creates mock compiler installs. 
 * - Creates common sample workspace projects.
 * Statically loads some read only projects, and prepares the workbench, in case it wasn't cleared.
 */
public abstract class BaseDeeTest extends CommonCoreTest {
	
	static {
		disableWorkspaceAutoBuild();
		disableDLTKIndexer();
		
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
	
	@SuppressWarnings("restriction")
	public static void enableDLTKIndexer(boolean waitUntilReady) {
		IndexManager indexManager = org.eclipse.dltk.internal.core.ModelManager.getModelManager().getIndexManager();
		indexManager.enable();
		if(waitUntilReady) {
			indexManager.waitUntilReady();
		}
	}
	
	public static final String MOCK_DMD2_INSTALL_NAME = "defaultDMD2Install";
	protected static final String MOCK_DEE_COMPILERS_PATH = "deeCompilerInstalls/";
	protected static final String MOCK_DMD2_TESTDATA_PATH = MOCK_DEE_COMPILERS_PATH+"DMDInstall/windows/bin/dmd.exe";
	public static final String MOCK_GDC_INSTALL_NAME = "gdcInstall";
	protected static final String MOCK_GDC_INSTALL_PATH = MOCK_DEE_COMPILERS_PATH+"gdcInstall/bin/gdc";
	
	protected static void setupTestDeeInstalls() {
		try {
			File destFolder = new File(TestsWorkingDir.getWorkingDir(), "deeCompilerInstalls");
			DeeCoreTestResources.copyTestFolderContentsFromDeeResource("deeCompilerInstalls", destFolder);
		} catch(CoreException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
		
		createFakeDeeInstall(DMDInstallType.INSTALLTYPE_ID, 
				MOCK_DMD2_INSTALL_NAME, MOCK_DMD2_TESTDATA_PATH, true);
		
		createFakeDeeInstall(GDCInstallType.INSTALLTYPE_ID, 
				MOCK_GDC_INSTALL_NAME, MOCK_GDC_INSTALL_PATH, false);
		
		checkTestSetupInvariants();
	}
	
	public static void checkTestSetupInvariants() {
		assertTrue(ScriptRuntime.getInterpreterInstallType(DMDInstallType.INSTALLTYPE_ID).
			getInterpreterInstalls().length > 0);
	}
	
	@Before
	@After
	public void checkTestSetupInvariants_do() {
		checkTestSetupInvariants();
	}
	
	protected static void createFakeDeeInstall(String installTypeId, String installName, String installExePath, 
			boolean setAsDefault) {
		IInterpreterInstallType deeDmdInstallType = ScriptRuntime.getInterpreterInstallType(installTypeId);
		InterpreterStandin install = new InterpreterStandin(deeDmdInstallType, installName + ".id");
		
		String installPathStr = DeeCoreTestResources.getWorkingDirFile(installExePath).getAbsolutePath();
		assertTrue(new File(installPathStr).exists());
		
		install.setInstallLocation(new LazyFileHandle(LocalEnvironment.ENVIRONMENT_ID, new Path(installPathStr)));
		install.setName(installName);
		install.setInterpreterArgs(null);
		install.setLibraryLocations(null); // Use default locations
		IInterpreterInstall realInstall = install.convertToRealInterpreter();
		if(setAsDefault) {
			try {
				ScriptRuntime.setDefaultInterpreterInstall(realInstall, null);
			} catch(CoreException e) {
				throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
			}
		}
	}
	
	public static IScriptProject createAndOpenDeeProject(String name) throws CoreException {
		return createAndOpenDeeProject(name, false, DMDInstallType.INSTALLTYPE_ID, MOCK_DMD2_INSTALL_NAME);
	}
	
	public static IScriptProject createAndOpenDeeProject(String name, boolean overwrite) throws CoreException {
		return createAndOpenDeeProject(name, overwrite, DMDInstallType.INSTALLTYPE_ID, MOCK_DMD2_INSTALL_NAME);
	}
	
	public static IScriptProject setupStandardDeeProject(IProject project) throws CoreException {
		return setupStandardDeeProject(project, DMDInstallType.INSTALLTYPE_ID, MOCK_DMD2_INSTALL_NAME);
	}
	
	public static IScriptProject createAndOpenDeeProject(
			String name, boolean overwrite, final String installTypeId, final String installId) throws CoreException {
		IProject project = createAndOpenProject(name, overwrite);
		return setupStandardDeeProject(project, installTypeId, installId);
	}
	
	public static IScriptProject setupStandardDeeProject(final IProject project, String installTypeId,
			String installId) throws CoreException {
		final String libraryBuildpathEntry = installTypeId + "/" + installId;
		EnvironmentManager.setEnvironmentId(project, null, false);
		ResourceUtils.getWorkspace().run(new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				setupDeeProject(project, libraryBuildpathEntry);
			}
		}, null);
		IScriptProject scriptProject = DLTKCore.create(project);
//		scriptProject.setOption(DLTKCore.INDEXER_ENABLED, false ? DLTKCore.ENABLED : DLTKCore.DISABLED);
//		scriptProject.setOption(DLTKCore.BUILDER_ENABLED, false ? DLTKCore.ENABLED : DLTKCore.DISABLED);
		
		checkInstall(scriptProject, installTypeId, installId);
		return scriptProject;
	}
	
	public static void checkInstall(IScriptProject project, String installTypeId, String installId) 
			throws CoreException {
		IInterpreterInstall install = ScriptRuntime.getInterpreterInstall(project);
		assertNotNull(install);
		assertTrue(install.getInterpreterInstallType().getId().endsWith(installTypeId));
		assertTrue(install.getId().startsWith(installId));
	}
	
	
	public static void setupDeeProject(IProject project, String libraryEntry) throws CoreException {
		IScriptProject dltkProj = DLTKCore.create(project);
		assertTrue(!dltkProj.exists());
		setupLangProject(project);
		assertTrue(dltkProj.exists());
		
		IBuildpathEntry entry = DLTKCore.newContainerEntry(
			ScriptRuntime.newDefaultInterpreterContainerPath().append(libraryEntry)		
		);
		dltkProj.setRawBuildpath(new IBuildpathEntry[] {entry}, null);
		assertNotNull(ScriptRuntime.getInterpreterInstall(dltkProj));
	}
	
}