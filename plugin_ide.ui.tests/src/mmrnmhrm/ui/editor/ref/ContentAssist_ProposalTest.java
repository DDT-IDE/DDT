/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.editor.ref;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.List;

import melnorme.lang.ide.ui.editor.actions.SourceOperationContext;
import melnorme.utilbox.core.CommonException;
import mmrnmhrm.core.engine.CompletionEngine_Test;
import mmrnmhrm.tests.SampleMainProject;
import mmrnmhrm.ui.editor.codeassist.DeeCompletionProposalComputer;
import mmrnmhrm.ui.editor.codeassist.DeeContentAssistProposal;

import org.eclipse.core.runtime.CoreException;
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
			
			@Override
			protected String getSource() throws CoreException {
				return ContentAssist_ProposalTest.this.editor.getSourceViewer_().getDocument().get();
			};
			
			@Override
			protected void testCompletionEngine(int offset, int rplLen) throws CommonException {
				
				DeeCompletionProposalComputer caComputer = new DeeCompletionProposalComputer();
				List<ICompletionProposal> proposals = caComputer.computeCompletionProposals(
					new SourceOperationContext(editor.getSourceViewer_(), offset, editor));
				
				for (ICompletionProposal completionProposal : proposals) {
					assertTrue(completionProposal instanceof DeeContentAssistProposal);
					DeeContentAssistProposal deeProposal = (DeeContentAssistProposal) completionProposal;
					assertTrue(deeProposal.getReplacementOffset() == offset);
					assertTrue(deeProposal.getReplacementLength() == rplLen);
				}
			}
		}.testBasic();
	}
	
}