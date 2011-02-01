package mmrnmhrm.ui.preferences;

import mmrnmhrm.ui.DeePlugin;

import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.dltk.ui.wizards.BuildpathsBlock;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

public class DeeBuildpathsBlock extends BuildpathsBlock {
	public DeeBuildpathsBlock(IRunnableContext runnableContext,
			IStatusChangeListener context, int pageToShow,
			boolean useNewPage, IWorkbenchPreferenceContainer pageContainer) {
		super(runnableContext, context, pageToShow, useNewPage, pageContainer);
	}

	@Override
	protected IPreferenceStore getPreferenceStore() {
		return DeePlugin.getInstance().getPreferenceStore();
	}

	@Override
	protected boolean supportZips() {
		return true;
	}
}