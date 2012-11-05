package mmrnmhrm.tests.ui.accessors;

import melnorme.utilbox.misc.ReflectionUtils;

import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage.NameGroup;


public abstract class ProjectWizardFirstPage__Accessor {

	public static NameGroup access_fNameGroup(ProjectWizardFirstPage firstPage) {
		NameGroup nameGroup = (NameGroup) ReflectionUtils.readField(firstPage, "fNameGroup");
		return nameGroup;
	}
	
}
