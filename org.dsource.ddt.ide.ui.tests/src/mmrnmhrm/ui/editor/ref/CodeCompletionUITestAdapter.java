package mmrnmhrm.ui.editor.ref;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.List;

import melnorme.swtutil.SWTTestUtils;
import mmrnmhrm.ui.editor.codeassist.DeeCodeCompletionProcessor;
import mmrnmhrm.ui.editor.codeassist.DeeCompletionProposal;
import mmrnmhrm.ui.text.DeePartitions;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.templates.ScriptTemplateProposal;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import dtool.ast.definitions.DefUnit;
import dtool.resolver.CompareDefUnits;
import dtool.tests.ref.cc.ICodeCompletionTester;

// Not that this class is run as a JUnit test, so annotated initializers are not run
public class CodeCompletionUITestAdapter extends ContentAssistUI_CommonTest implements ICodeCompletionTester {
	
	public CodeCompletionUITestAdapter(ISourceModule srcModule) {
		super(srcModule);
		
		ContentAssistant ca = ContentAssistUI_CommonTest.getContentAssistant(editor);
		ca.enableAutoInsert(false);
		ca.enablePrefixCompletion(false);
	}
	
	@Override
	public boolean isJUnitTest() {
		return false;
	}
	
	@Override
	public void runAfters() {
		SWTTestUtils.clearEventQueue();
	}
	
	@Override
	public void testComputeProposalsWithRepLen(int offset, int prefixLen, int repLen,
		boolean removeObjectIntrinsics, String... expectedProposals) {
		
		ContentAssistant ca = getContentAssistant(editor);
		DeeCodeCompletionProcessor caProcessor = (DeeCodeCompletionProcessor) 
			ca.getContentAssistProcessor(DeePartitions.DEE_CODE);
		
		ICompletionProposal[] proposals = caProcessor.computeCompletionProposals(editor.getViewer(), offset);
		checkProposals(offset, prefixLen, repLen, removeObjectIntrinsics, proposals, expectedProposals);
		
		invokeContentAssist(editor, offset); 
		SWTTestUtils.________________clearEventQueue________________();
	}
	
	protected void checkProposals(int repOffset, int prefixLen, int repLen, boolean removeObjectIntrinsics,
			ICompletionProposal[] proposals, String... expectedProposals) {
		assertNotNull(proposals);
		
		List<DefUnit> results = mapOut(list(proposals), proposalToDefunit, new ArrayList<DefUnit>());
		
		CompareDefUnits.checkResults(results, expectedProposals, removeObjectIntrinsics);
		
		checkProposals(proposals, repOffset, repLen, prefixLen);
	}
	
	protected static void checkProposals(ICompletionProposal[] proposals, int repOffset, int repLen, int prefixLen) {
		for(ICompletionProposal completionProposal : proposals) {
			if(completionProposal instanceof ScriptTemplateProposal) {
				continue;
			}
			DeeCompletionProposal proposal = (DeeCompletionProposal) completionProposal;
			String defName = proposal.defUnit.toStringAsElement();
			
			assertTrue(repOffset == proposal.getReplacementOffset());
			assertTrue(repLen == proposal.getReplacementLength());
			if(prefixLen != -666) {
				String repStr = defName.substring(prefixLen);
				if(repStr.indexOf('(') != -1) {
					repStr = repStr.substring(0, repStr.indexOf('('));
				}
				assertTrue(repStr.equals(proposal.getReplacementString()));				
			}
		}
	}
	
}