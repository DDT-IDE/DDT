package mmrnmhrm.ui.wizards;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.ide.ui.utils.WorkbenchUtils;
import melnorme.utilbox.misc.MiscUtil;
import mmrnmhrm.core.DLTKUtils;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.projectmodel.DubModelManager;
import mmrnmhrm.tests.SampleMainProject;
import mmrnmhrm.tests.ui.accessors.ProjectWizardFirstPage__Accessor;
import mmrnmhrm.tests.ui.accessors.WizardDialog__Accessor;
import mmrnmhrm.ui.CommonDeeUITest;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class DeeProjectWizardTest extends CommonDeeUITest {
	
	private DeeProjectWizard wizard;
	private WizardDialog__Accessor wizDialog;
	
	final static String NEWPROJNAME = "WizardCreationProject";
	
	@Before
	public void setUp() throws Exception {
		MiscUtil.loadClass(SampleMainProject.class);
		
		tearDown();
		//WorkbenchPlugin.getDefault().getNewWizardRegistry().findWizard(id);
		wizard = new DeeProjectWizard();
		IWorkbenchWindow window = WorkbenchUtils.getActiveWorkbenchWindow();
		wizard.init(window.getWorkbench(), null);
		
		Shell parent = WorkbenchUtils.getActiveWorkbenchShell();
		wizDialog = new WizardDialog__Accessor(parent, wizard);
		wizDialog.setBlockOnOpen(false);
		wizDialog.open();
	}
	
	
	@After
	public void tearDown() throws Exception {
		// Should undo all wizard actions
		DubModelManager.getDefault().syncPendingUpdates(); // ensure DUB process finished
		ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				IProject project = DeeCore.getWorkspaceRoot().getProject(NEWPROJNAME);
				if(project.exists()) {
					project.delete(true, monitor);
				}
			}
		}, null);
	}
	
	
	private void simulateEnterPage2() {
		wizDialog.nextPressed();
	}
	
	private void simulatePage2GoBack() {
		wizDialog.backPressed();
	}
	
	private void simulatePressCancel() {
		wizDialog.cancelPressed();
	}
	
	private void simulatePressFinish() {
		wizDialog.finishPressed();
	}
	
	@Test
	public void test_P1Validation() throws Throwable { test_P1Validation$(); }
	public void test_P1Validation$() throws Throwable {
		ProjectWizardFirstPage__Accessor.access_fNameGroup(wizard.fFirstPage).setName(SampleMainProject.SAMPLEPROJNAME);
		assertTrue(!wizard.canFinish());
		
		simulatePressCancel();
		assertTrue(checkNoChanges());
	}
	
	@Test
	public void test_P1_Finish() throws Throwable {
		wizard.fFirstPage.getProjectName();
		ProjectWizardFirstPage__Accessor.access_fNameGroup(wizard.fFirstPage).setName(NEWPROJNAME);
		assertTrue(wizard.canFinish());
		
		simulatePressFinish();
		assertTrue(checkProjectCreated());
	}
	
	@Test
	public void test_P1_P2_P1_Finish() throws Throwable {
		ProjectWizardFirstPage__Accessor.access_fNameGroup(wizard.fFirstPage).setName(NEWPROJNAME);
		assertTrue(wizard.canFinish());
		simulateEnterPage2();
		
		simulatePage2GoBack();
		
		simulatePressFinish();
		assertTrue(checkProjectCreated());
	}
	
	
	/* ---- */
	
	@Test
	public void test_P1_Cancel() throws Throwable {
		ProjectWizardFirstPage__Accessor.access_fNameGroup(wizard.fFirstPage).setName(NEWPROJNAME);
		assertTrue(wizard.canFinish());
		
		
		simulatePressCancel();
		assertTrue(checkNoChanges());
	}
	
	
	protected boolean checkNoChanges() throws Throwable {
		logErrorListener.checkErrors();
		
		return DLTKUtils.getDLTKModel().getScriptProject(NEWPROJNAME).exists() == false;
	}
	
	protected boolean checkProjectCreated() throws Throwable {
		logErrorListener.checkErrors();
		return DLTKUtils.getDLTKModel().getScriptProject(NEWPROJNAME).exists();
	}
}
