package mmrnmhrm.ui.editor.ref;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.List;

import melnorme.lang.ide.ui.text.completion.LangContentAssistInvocationContext;
import mmrnmhrm.core.engine_client.CompletionEngine_Test;
import mmrnmhrm.tests.SampleMainProject;
import mmrnmhrm.ui.editor.codeassist.DeeContentAssistProposal;
import mmrnmhrm.ui.editor.codeassist.DeeCompletionProposalComputer;

import org.eclipse.dltk.core.ModelException;
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
				
				DeeCompletionProposalComputer caComputer = new DeeCompletionProposalComputer();
				List<ICompletionProposal> proposals = caComputer.computeCompletionProposals(
					new LangContentAssistInvocationContext(editor.getSourceViewer_(), offset, editor));
				
				for (ICompletionProposal completionProposal : proposals) {
					
					assertTrue(completionProposal instanceof DeeContentAssistProposal);
					DeeContentAssistProposal deeProposal = (DeeContentAssistProposal) completionProposal;
					assertTrue(deeProposal.getReplacementOffset() == offset);
					assertTrue(deeProposal.getReplacementLength() == rplLen);
					assertTrue(deeProposal.getCursorPosition() == deeProposal.getReplacementString().length());
				}
			}
		}.testBasic();
	}
	
}