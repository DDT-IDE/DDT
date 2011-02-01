package melnorme.util.ui.fields;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

public class ProjectContainerSelectionDialog {
	public FolderSelectionDialog dialog;
	private final Shell shell;
	private final IProject project;

	public ProjectContainerSelectionDialog(Shell shell, IProject project) {
		this.shell = shell;
		this.project = project;
		Class<?>[] acceptedClasses= new Class<?>[] { IProject.class, IFolder.class };

		ViewerFilter filter = new TypedViewerFilter(acceptedClasses, null);
		ISelectionStatusValidator validator= new TypedElementSelectionValidator(acceptedClasses, false);

		dialog = getDefaultFolderSelectionDialog();
		dialog.setTitle("Choose a folder"); 
		dialog.setMessage("Choose a folder desc."); 
		dialog.setValidator(validator);
		dialog.addFilter(filter);
	}

	protected FolderSelectionDialog getDefaultFolderSelectionDialog() {
		ILabelProvider lp= new WorkbenchLabelProvider();
		ITreeContentProvider cp= new WorkbenchContentProvider();

		FolderSelectionDialog dialog;
		dialog = new FolderSelectionDialog(shell, lp, cp);
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		dialog.setInput(project);
		return dialog;
	}

	public IContainer chooseContainer() {
		if (dialog.open() == Window.OK) {
			return (IContainer)dialog.getFirstResult();
		}
		return null;
	}
}
