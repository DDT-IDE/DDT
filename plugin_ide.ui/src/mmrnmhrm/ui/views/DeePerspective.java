package mmrnmhrm.ui.views;

import mmrnmhrm.ui.wizards.DeeProjectWizard;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.progress.IProgressConstants;

public class DeePerspective implements IPerspectiveFactory {
	
	public static final String PERSPECTIVE_ID = "org.dsource.ddt.ide.ui.DeePerspective";
	
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		addFolderViewStructure(layout);
		
		addActionSets(layout);
		addShowViewShortcuts(layout);
		addNewWizardShortcuts(layout);
		addPerspectiveShotcuts(layout);
	}
	
	protected void addFolderViewStructure(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		IFolderLayout leftFolder = layout.createFolder("leftPane", IPageLayout.LEFT, 0.25f, editorArea);
		leftFolder.addView(IPageLayout.ID_PROJECT_EXPLORER);
		
		
		IFolderLayout bottomFolder = layout.createFolder("bottomPane", IPageLayout.BOTTOM, 0.75f, editorArea);
		
		bottomFolder.addView(IPageLayout.ID_TASK_LIST);
		bottomFolder.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottomFolder.addView("org.eclipse.dltk.ui.TypeHierarchy");
		bottomFolder.addPlaceholder(NewSearchUI.SEARCH_VIEW_ID);
		bottomFolder.addView(IPageLayout.ID_PROGRESS_VIEW);
		bottomFolder.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		
		layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT, 0.75f, editorArea);
	}
	
	protected void addActionSets(IPageLayout layout) {
		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		layout.addActionSet("org.dsource.ddt.ide.ui.DeeActionSet"); //$NON-NLS-1$
	}
	
	protected void addShowViewShortcuts(IPageLayout layout) {
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
		layout.addShowViewShortcut(IProgressConstants.PROGRESS_VIEW_ID);
		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		layout.addShowViewShortcut(NewSearchUI.SEARCH_VIEW_ID);
//		layout.addShowViewShortcut("org.eclipse.dltk.testing.ResultView"); //$NON-NLS-1$
//		layout.addShowViewShortcut("org.eclipse.dltk.ui.TypeHierarchy"); //$NON-NLS-1$
//		layout.addShowViewShortcut("org.eclipse.dltk.callhierarchy.view"); //$NON-NLS-1$
	}
	
	protected void addNewWizardShortcuts(IPageLayout layout) {
		// Lang
		layout.addNewWizardShortcut(DeeProjectWizard.WIZARD_ID);
		
		// General
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");//$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");//$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.editors.wizards.UntitledTextFileWizard");//$NON-NLS-1$
	}
	
	protected void addPerspectiveShotcuts(IPageLayout layout) {
		layout.addPerspectiveShortcut("org.eclipse.debug.ui.DebugPerspective"); //$NON-NLS-1$
		layout.addPerspectiveShortcut("org.eclipse.ui.resourcePerspective"); //$NON-NLS-1$
		layout.addPerspectiveShortcut("org.eclipse.team.ui.TeamSynchronizingPerspective"); //$NON-NLS-1$
	}
	
}
