package mmrnmhrm.ui.editor.ref;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.List;

import mmrnmhrm.tests.ui.SWTTestUtils;
import mmrnmhrm.ui.editor.codeassist.DeeCodeCompletionProcessor;
import mmrnmhrm.ui.editor.codeassist.DeeCompletionProposal;
import mmrnmhrm.ui.text.DeePartitions;

import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.templates.ScriptTemplateProposal;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import dtool.ast.definitions.DefUnit;
import dtool.tests.ref.cc.CodeCompletion__Common;
import dtool.tests.ref.cc.ICodeCompletionTester;

// Not that this class is run as a JUnit test, so annotated initializers are not run
public class CodeCompletionUITestAdapter extends ContentAssistUI_CommonTest implements ICodeCompletionTester {
	
	protected ISourceModule srcModule;
	
	public CodeCompletionUITestAdapter(IFile file) {
		super(file);
		this.srcModule = DLTKCore.createSourceModuleFrom(file);
	}
	
	public CodeCompletionUITestAdapter(ISourceModule srcModule) {
		super(assertCast(srcModule.getResource(), IFile.class));
		this.srcModule = DLTKCore.createSourceModuleFrom(file);
	}
	
	@Override
	public void runAfters() {
		SWTTestUtils.clearEventQueue();
	}
	
	@Override
	public void testComputeProposalsWithRepLen(int repOffset, int prefixLen, 
			int repLen, boolean removeObjectIntrinsics, String... expectedProposals) throws ModelException {
		
		// Test with DeeCompletionProcessor as well
		ContentAssistant ca = getContentAssistant(editor);
		DeeCodeCompletionProcessor caProcessor = new DeeCodeCompletionProcessor(editor, ca, DeePartitions.DEE_CODE);
		ICompletionProposal[] proposals = caProcessor.computeCompletionProposals(editor.getViewer(), repOffset);
		checkProposals(repOffset, prefixLen, repLen, removeObjectIntrinsics, proposals, expectedProposals);
		
		invokeContentAssist(); // Just a wild shot test
		SWTTestUtils.________________clearEventQueue________________();
	}

	protected void checkProposals(int repOffset, int prefixLen, int repLen, boolean removeObjectIntrinsics,
			ICompletionProposal[] proposals, String... expectedProposals) {
		assertNotNull(proposals, "Code Completion Unavailable");
		
		List<DefUnit> results = mapOut(list(proposals), proposalToDefunit, new ArrayList<DefUnit>());
		
		CodeCompletion__Common.checkProposals(results, expectedProposals, removeObjectIntrinsics);
		
		checkProposals(repOffset, repLen, prefixLen, proposals);
	}
	
	protected static void checkProposals(int repOffset, int repLen, int prefixLen, ICompletionProposal[] proposals) {
		for(ICompletionProposal completionProposal : proposals) {
			if(completionProposal instanceof ScriptTemplateProposal) {
				continue;
			}
			DeeCompletionProposal proposal = (DeeCompletionProposal) completionProposal;
			String defName = proposal.defUnit.toStringAsElement();
			
			String repStr = defName.substring(prefixLen);
			checkProposal(proposal, repOffset, repLen, repStr);
		}
	}
	
	protected static void checkProposal(DeeCompletionProposal proposal, int repOffset, int repLen, String repStr) {
		if(repStr.indexOf('(') != -1) {
			repStr = repStr.substring(0, repStr.indexOf('('));
		}
		assertTrue(repOffset == proposal.getReplacementOffset());
		assertTrue(repLen == proposal.getReplacementLength());
		assertTrue(repStr.equals(proposal.getReplacementString()));
	}
	
}