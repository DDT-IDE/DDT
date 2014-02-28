package mmrnmhrm.ui.wizards;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.ide.ui.utils.WorkbenchUtils;
import melnorme.utilbox.misc.MiscUtil;
import mmrnmhrm.core.DLTKUtils;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.projectmodel.DubModelManager;
import mmrnmhrm.tests.SampleMainProject;
import mmrnmhrm.tests.ui.BaseDeeUITest;
import mmrnmhrm.tests.ui.accessors.ProjectWizardFirstPage__Accessor;
import mmrnmhrm.tests.ui.accessors.WizardDialog__Accessor;

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



public class DeeProjectWizardTest extends BaseDeeUITest {
	
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
		ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				/*DeeProject deeproj = ModelUtil.getDeeProject(NEWPROJNAME);
				if(deeproj != null) {
					deeproj.getProject().delete(true, monitor);
				}*/
				IProject project = DeeCore.getWorkspaceRoot().getProject(NEWPROJNAME);
				if(project.exists())
					project.delete(true, monitor);
			}
		}, null);
	}
	
	
	private void simulateEnterPage2() {
		wizDialog.nextPressed();
	}
	
	private void simulatePage2GoBack() {
		DubModelManager.getDefault().syncPendingUpdates(); // Make sure dub process terminates
		wizDialog.backPressed();
	}
	
	private void simulatePressCancel() {
		DubModelManager.getDefault().syncPendingUpdates(); // Make sure dub process terminates
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
	
	
	/*@Test
	public void test_P1_P2_Finish() throws Throwable {
		TestAccessor._NameGroup_setName(wizard.fFirstPage, NEWPROJNAME);
		assertTrue(wizard.canFinish());
		
		simulateEnterPage2();
		
		ProjectConfigBlockTest auxtest = new ProjectConfigBlockTest();
		auxtest.init(wizard.fSecondPage.projectConfigBlock);
		auxtest.doChangeSet1();
		
		simulatePressFinish();
		assertTrue(checkProjectCreated());
		//auxtest.assertChangeSet1Applied();
	}*/
	
	
	
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
	
	
	/*@Test
	public void test_P1_P2_Cancel() throws Throwable {
		wizard.fFirstPage.fNameGroup.setName(NEWPROJNAME);
		assertTrue(wizard.canFinish());
		
		simulateEnterPage2();
		ProjectConfigBlockTest auxtest = new ProjectConfigBlockTest();
		auxtest.init(wizard.fSecondPage.projectConfigBlock);
		auxtest.doChangeSet1();

		simulatePressCancel();
		assertTrue(checkNoChanges());
		auxtest.assertChangeSet1NotApplied();
	}*/
	
	
	/*@Test
	public void test_P1_P2_P1_Cancel() throws Throwable {
		wizard.fFirstPage.fNameGroup.setName(NEWPROJNAME);
		assertTrue(wizard.canFinish());
		
		simulateEnterPage2();
		ProjectConfigBlockTest auxtest = new ProjectConfigBlockTest();
		auxtest.init(wizard.fSecondPage.projectConfigBlock);
		auxtest.doChangeSet1();
		
		simulatePage2GoBack();
		
		simulatePressCancel();
		assertTrue(checkNoChanges());
		auxtest.assertChangeSet1NotApplied();
	}*/
	
	protected boolean checkNoChanges() throws Throwable {
		logErrorListener.checkErrors();
		
		return DLTKUtils.getDLTKModel().getScriptProject(NEWPROJNAME).exists() == false;
	}
	
	protected boolean checkProjectCreated() throws Throwable {
		logErrorListener.checkErrors();
		return DLTKUtils.getDLTKModel().getScriptProject(NEWPROJNAME).exists();
	}
}
