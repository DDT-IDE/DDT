package mmrnmhrm.ui.wizards;

import melnorme.utilbox.misc.ReflectionUtils;

import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage.NameGroup;


public abstract class ProjectWizardFirstPage__Accessor {

	public static NameGroup access_fNameGroup(ProjectWizardFirstPage firstPage) throws NoSuchFieldException {
		NameGroup nameGroup = (NameGroup) ReflectionUtils.readField(firstPage, "fNameGroup");
		return nameGroup;
	}
	
}
