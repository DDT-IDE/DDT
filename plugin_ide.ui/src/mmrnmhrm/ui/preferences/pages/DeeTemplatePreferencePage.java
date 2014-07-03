package mmrnmhrm.ui.preferences.pages;

import mmrnmhrm.ui.DeeUIPlugin;
import mmrnmhrm.ui.editor.DeeSimpleSourceViewerConfiguration;
import mmrnmhrm.ui.editor.templates.DeeTemplateAccess;
import mmrnmhrm.ui.text.DeePartitions;
import mmrnmhrm.ui.text.DeeTextTools;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.dltk.ui.templates.ScriptTemplatePreferencePage;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.jface.text.IDocument;

public class DeeTemplatePreferencePage extends ScriptTemplatePreferencePage {
	
	private static final String DEE_TEMPLATE_PREFPAGE_TITLE = "Code Templates";
	
	public final static String PAGE_ID = DeeUIPlugin.PLUGIN_ID + ".preferences.editor.CodeTemplates";
	
	@Override
	public void setTitle(String title) {
		super.setTitle(DEE_TEMPLATE_PREFPAGE_TITLE);
	}
	
	@Override
	protected void setPreferenceStore() {
		setPreferenceStore(DeeUIPlugin.getInstance().getPreferenceStore());
	}
	
	@Override
	protected ScriptTemplateAccess getTemplateAccess() {
		return DeeTemplateAccess.getInstance();
	}
	
	@Override
	protected ScriptSourceViewerConfiguration createSourceViewerConfiguration() {
		return new DeeSimpleSourceViewerConfiguration(getTextTools().getColorManager(), getPreferenceStore(), null,
				DeePartitions.PARTITIONING_ID, false);
	}
	
	@Override
	protected void setDocumentParticioner(IDocument document) {
		getTextTools().setupDocumentPartitioner(document, DeePartitions.PARTITIONING_ID);
	}
	
	protected DeeTextTools getTextTools() {
		return DeeUIPlugin.getDefault().getTextTools();
	}
	
}
