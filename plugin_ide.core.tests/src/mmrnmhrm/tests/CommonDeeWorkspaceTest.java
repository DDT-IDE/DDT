package mmrnmhrm.tests;


import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.utilbox.misc.MiscUtil;
import mmrnmhrm.core.CommonDeeWorkspaceTestNew;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.After;
import org.junit.Before;

import dtool.tests.MockCompilerInstalls;

/**
 * Initializes a common Dee test setup:
 * - Creates common sample workspace projects.
 * Statically loads some read only projects, and prepares the workbench, in case it wasn't cleared.
 */
public abstract class CommonDeeWorkspaceTest extends CommonDeeWorkspaceTestNew {
	
	static {
		setupTestDeeInstalls();
		
		SamplePreExistingProject.checkForExistanceOfPreExistingProject();
		SampleNonDeeProject.createAndSetupNonDeeProject();
	}
	
	protected static void setupTestDeeInstalls() {
		MiscUtil.loadClass(MockCompilerInstalls.class);
		
		checkTestSetupInvariants();
	}
	
	public static void checkTestSetupInvariants() {
	}
	
	@Before
	@After
	public void checkTestSetupInvariants_do() {
		checkTestSetupInvariants();
	}
	
	public static IProject createAndOpenDeeProject(String name) throws CoreException {
		return createAndOpenDeeProject(name, false);
	}
	
	public static IProject createAndOpenDeeProject(String name, boolean overwrite) throws CoreException {
		IProject project = createAndOpenProject(name, overwrite);
		return setupStandardDeeProject(project);
	}
	
	public static IProject setupStandardDeeProject(final IProject project) throws CoreException {
		ResourceUtils.getWorkspace().run(new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				setupLangProject(project);
			}
		}, null);
		return project;
	}
	
}