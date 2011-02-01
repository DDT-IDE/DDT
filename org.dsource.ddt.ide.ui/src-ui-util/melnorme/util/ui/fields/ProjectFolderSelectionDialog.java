package melnorme.util.ui.fields;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

public class ProjectFolderSelectionDialog extends ProjectContainerSelectionDialog {
	public ProjectFolderSelectionDialog(Shell shell, IProject project) {
		super(shell, project);

		Class<?>[] visibleClasses= new Class<?>[] { IProject.class, IFolder.class };
		Class<?>[] acceptedClasses= new Class<?>[] { IFolder.class };

		ViewerFilter filter = new TypedViewerFilter(visibleClasses, null);
		ISelectionStatusValidator validator= new TypedElementSelectionValidator(acceptedClasses, false);

		dialog = getDefaultFolderSelectionDialog();
		dialog.setTitle("Choose a folder"); 
		dialog.setMessage("Choose a folder desc."); 
		dialog.setValidator(validator);
		dialog.addFilter(filter);
	}

	@Override
	public IFolder chooseContainer() {
		if (dialog.open() == Window.OK) {
			return (IFolder) dialog.getFirstResult();
		}
		return null;
	}
}
