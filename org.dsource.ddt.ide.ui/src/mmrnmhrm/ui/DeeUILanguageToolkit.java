package mmrnmhrm.ui;

import mmrnmhrm.core.dltk.DeeLanguageToolkit;
import mmrnmhrm.ui.editor.DeeEditor;
import mmrnmhrm.ui.preferences.pages.DeeCompilersPreferencePage;
import mmrnmhrm.ui.preferences.pages.DeeEditorPreferencePage;
import mmrnmhrm.ui.preferences.pages.DeeSourceColoringPreferencePage;
import mmrnmhrm.ui.preferences.pages.DeeTemplatePreferencePage;
import mmrnmhrm.ui.text.DeeSimpleSourceViewerConfiguration;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.AbstractDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.dltk.ui.viewsupport.ScriptUILabelProvider;
import org.eclipse.jface.preference.IPreferenceStore;

public class DeeUILanguageToolkit extends AbstractDLTKUILanguageToolkit implements IDLTKUILanguageToolkit {
	
	private static final DeeUILanguageToolkit instance = new DeeUILanguageToolkit();
	private static final DeeScriptElementLabels elementLabels = new DeeScriptElementLabels(); 
	
	
	public static DeeUILanguageToolkit getDefault() {
		return instance ;
	}
	
	@Override
	public IDLTKLanguageToolkit getCoreToolkit() {
		return DeeLanguageToolkit.getDefault();
	}
	
	@Override
	public String getEditorId(Object inputElement) {
		return DeeEditor.EDITOR_ID;
	}
	
	@Override
	public IPreferenceStore getPreferenceStore() {
		return DeePlugin.getInstance().getPreferenceStore();
	}
	
	//XXX: DLTK: use DLTK partitioning?
	@Override
	public String getPartitioningId() {
		return DeeConstants.PARTITIONING_ID;
	}
	
	@Override
	public ScriptTextTools getTextTools() {
		return DeePlugin.getDefault().getTextTools();
	}
	
	@Override
	public ScriptSourceViewerConfiguration createSourceViewerConfiguration() {
		return new DeeSimpleSourceViewerConfiguration(getTextTools().getColorManager(),
				getPreferenceStore(), null, getPartitioningId(), false);
	}
	
	@Override
	public ScriptUILabelProvider createScriptUILabelProvider() {
		return new DeeScriptUILabelProvider();
	}
	
	@Override
	public ScriptElementLabels getScriptElementLabels() {
		return elementLabels; 
	}
	
	@Override
	public String getInterpreterPreferencePage() {
		return DeeCompilersPreferencePage.PAGE_ID;
	}
	
	@Override
	public String[] getEditorPreferencePages() {
		return new String[]{ 
				DeeEditorPreferencePage.PAGE_ID, 
				DeeTemplatePreferencePage.PAGE_ID,
				DeeSourceColoringPreferencePage.PAGE_ID};
	}
	
	@Override
	public String getDebugPreferencePage() {
		// TODO DLTK getDebugPreferencePage
		return null;
	}
	
	
	@Override
	public String getInterpreterContainerId() {
		return "mmrnmrhm.core.launching.INTERPRETER_CONTAINER";
	}
	
	@Override
	public boolean getProvideMembers(ISourceModule element) {
		return true;
	}
	
}
