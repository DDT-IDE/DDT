package mmrnmhrm.ui.editor;

import melnorme.lang.ide.ui.EditorSettings_Actual;
import melnorme.lang.ide.ui.editor.AbstractLangEditorActions;
import mmrnmhrm.ui.DeeUIPlugin;
import mmrnmhrm.ui.text.DeePartitions;

import org.eclipse.dltk.internal.ui.editor.ScriptOutlinePage;
import org.eclipse.dltk.ui.text.ScriptTextTools;

public class DeeEditor extends DeeBaseEditor {
	
	@Deprecated
	@Override
	public ScriptTextTools getTextTools() {
		return DeeUIPlugin.getDefault().getTextTools();
	}
	
	@Override
	protected ScriptOutlinePage doCreateOutlinePage() {
		return new DeeOutlinePage(this, DeeUIPlugin.getInstance().getPreferenceStore());
	}
	
	@Override
	protected String getPartitioningToConnect() {
		return DeePartitions.PARTITIONING_ID;
	}
	
	@Override
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] { EditorSettings_Actual.EDITOR_CONTEXT_ID });
	}
	
	@Override
	protected AbstractLangEditorActions createActionsManager() {
		return new AbstractLangEditorActions(this) {
			@Override
			protected void doDispose() {
			}
		};
	}
	
}