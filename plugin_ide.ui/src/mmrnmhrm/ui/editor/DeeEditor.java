package mmrnmhrm.ui.editor;

import melnorme.lang.ide.ui.EditorSettings_Actual;
import mmrnmhrm.ui.DeeUIPlugin;
import mmrnmhrm.ui.text.DeePartitions;

import org.dsource.ddt.ide.core.DeeLanguageToolkit;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.internal.ui.editor.ScriptOutlinePage;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

public class DeeEditor extends DeeBaseEditor {
	
	private ICharacterPairMatcher bracketMatcher = 
		new DefaultCharacterPairMatcher("{}[]()".toCharArray());
	
	@Override
	public String getEditorId() {
		return EditorSettings_Actual.EDITOR_ID;
	}
	
	@Override
	public IDLTKLanguageToolkit getLanguageToolkit() {
		return DeeLanguageToolkit.getDefault();
	}
	
	@Override
	public IPreferenceStore getScriptPreferenceStore() {
		return super.getScriptPreferenceStore();
	}
	
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
	protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {
		support.setCharacterPairMatcher(bracketMatcher);
		support.setMatchingCharacterPainterPreferenceKeys(MATCHING_BRACKETS, MATCHING_BRACKETS_COLOR);
		
		super.configureSourceViewerDecorationSupport(support);
	}
	
	@SuppressWarnings("restriction") 
	@Override
	protected org.eclipse.dltk.internal.ui.actions.FoldingActionGroup createFoldingActionGroup() {
		return new org.eclipse.dltk.internal.ui.actions.
		FoldingActionGroup(this, getViewer(), DeeUIPlugin.getInstance().getPreferenceStore());
	}
	
	@Override
	public String getCallHierarchyID() {
		return "org.eclipse.dltk.callhierarchy.view";
	}
	
}
