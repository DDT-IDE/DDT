package mmrnmhrm.ui.preferences.pages;

import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.editor.templates.RubyTemplateAccess;
import mmrnmhrm.ui.text.DeePartitions;
import mmrnmhrm.ui.text.DeeSimpleSourceViewerConfiguration;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.dltk.ui.templates.ScriptTemplatePreferencePage;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.text.IDocument;

// TODO implement Dee Code Templates
public class DeeTemplatePreferencePage extends ScriptTemplatePreferencePage {
	
	public final static String PAGE_ID = DeePlugin.EXTENSIONS_IDPREFIX+"preferences.editor.CodeTemplates";
	
	public DeeTemplatePreferencePage() {
		super();
	}
	
	@Override
	protected void setPreferenceStore() {
		setPreferenceStore(DeePlugin.getDefault().getPreferenceStore());
	}
	
	@Override
	protected ScriptTemplateAccess getTemplateAccess() {
		// TODO: implement DeeTemplateAccess
		return RubyTemplateAccess.getInstance();
	}
	
	@Override
	protected ScriptSourceViewerConfiguration createSourceViewerConfiguration() {
		ScriptTextTools textTools = DeePlugin.getDefault().getTextTools();

		return new DeeSimpleSourceViewerConfiguration(textTools
				.getColorManager(), getPreferenceStore(), null,
				DeePartitions.DEE_PARTITIONING, false);

	}

	@Override
	protected void setDocumentParticioner(IDocument document) {
		ScriptTextTools textTools = DeePlugin.getDefault().getTextTools();
		textTools.setupDocumentPartitioner(document, DeePartitions.DEE_PARTITIONING);
	}

}
