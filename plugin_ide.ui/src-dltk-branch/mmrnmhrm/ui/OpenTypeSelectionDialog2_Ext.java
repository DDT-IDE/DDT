package mmrnmhrm.ui;

import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.internal.ui.dialogs.OpenTypeSelectionDialog2;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.dialogs.TypeSelectionExtension;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.swt.widgets.Shell;

public class OpenTypeSelectionDialog2_Ext extends OpenTypeSelectionDialog2 {

	public OpenTypeSelectionDialog2_Ext(Shell parent, boolean multi, IRunnableContext context, IDLTKSearchScope scope,
			int elementKinds, IDLTKUILanguageToolkit toolkit) {
		super(parent, multi, context, scope, elementKinds, toolkit);
	}

	public OpenTypeSelectionDialog2_Ext(Shell parent, boolean multi, IRunnableContext context, IDLTKSearchScope scope,
			int elementKinds, TypeSelectionExtension extension, IDLTKUILanguageToolkit toolkit) {
		super(parent, multi, context, scope, elementKinds, extension, toolkit);
	}
	
	@Override
	public void setTitle(String title) {
		super.setTitle(title);
	}
	
}
