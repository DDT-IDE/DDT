package mmrnmhrm.ui.editor.ref;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import mmrnmhrm.core.engine_client.CompletionEngine_Test;
import mmrnmhrm.tests.SampleMainProject;
import mmrnmhrm.ui.editor.codeassist.DeeCodeCompletionProcessor;
import mmrnmhrm.ui.editor.codeassist.DeeCompletionProposal;
import mmrnmhrm.ui.text.DeePartitions;

import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.templates.ScriptTemplateProposal;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.junit.Test;

public class ContentAssist_ProposalTest extends ContentAssistUI_CommonTest {
	
	public ContentAssist_ProposalTest() {
		super(SampleMainProject.getFile("src-ca/testCodeCompletion.d"));
	}
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		new CompletionEngine_Test() {
			{ this.srcModule = ContentAssist_ProposalTest.this.srcModule; }
			
			@Override
			protected void testCompletionEngine(int offset, int rplLen) throws ModelException {
				ContentAssistant ca = getContentAssistant(editor);
				
				DeeCodeCompletionProcessor caProcessor = new DeeCodeCompletionProcessor(editor, ca, DeePartitions.DEE_CODE);
				ICompletionProposal[] proposals = caProcessor.computeCompletionProposals(editor.getViewer(), offset);
				
				for (ICompletionProposal completionProposal : proposals) {
					if(completionProposal instanceof ScriptTemplateProposal) {
						continue;
					}
					
					assertTrue(completionProposal instanceof DeeCompletionProposal);
					DeeCompletionProposal deeProposal = (DeeCompletionProposal) completionProposal;
					assertTrue(deeProposal.getReplacementOffset() == offset);
					assertTrue(deeProposal.getReplacementLength() == rplLen);
					assertTrue(deeProposal.getCursorPosition() == deeProposal.getReplacementString().length());
				}
			}
		}.testBasic();
	}
	
}