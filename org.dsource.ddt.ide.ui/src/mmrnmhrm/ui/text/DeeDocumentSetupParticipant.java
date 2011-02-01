package mmrnmhrm.ui.text;

import mmrnmhrm.ui.DeePlugin;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.text.IDocument;

public class DeeDocumentSetupParticipant implements IDocumentSetupParticipant {
	
	@Override
	public void setup(IDocument document) {
		ScriptTextTools tools= DeePlugin.getDefault().getTextTools();
		tools.setupDocumentPartitioner(document, DeePartitions.DEE_PARTITIONING);
	}

}
