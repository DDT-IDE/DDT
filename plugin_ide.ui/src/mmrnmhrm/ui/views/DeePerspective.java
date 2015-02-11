package mmrnmhrm.ui.views;

import melnorme.lang.ide.ui.LangPerspective;

import org.eclipse.ui.IPageLayout;

public class DeePerspective extends LangPerspective {
	
	public static final String PERSPECTIVE_ID = "org.dsource.ddt.ide.ui.DeePerspective";
	
	@Override
	protected void addActionSets(IPageLayout layout) {
		super.addActionSets(layout);
		layout.addActionSet("org.dsource.ddt.ide.ui.DeeActionSet"); //$NON-NLS-1$
	}
	
}