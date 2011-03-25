package mmrnmhrm.ui.editor.ref;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.List;

import melnorme.utilbox.core.Function;
import mmrnmhrm.tests.ui.SWTTestUtils;
import mmrnmhrm.ui.editor.DeeEditor;
import mmrnmhrm.ui.editor.codeassist.DeeCodeContentAssistProcessor;
import mmrnmhrm.ui.editor.text.DeeCompletionProposal;

import org.dsource.ddt.lang.ui.WorkbenchUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import dtool.ast.definitions.DefUnit;
import dtool.contentassist.CompletionSession;
import dtool.tests.CommonTestUtils;
import dtool.tests.ref.cc.CodeCompletion__Common;
import dtool.tests.ref.cc.ICodeCompletionTester;

public class CodeCompletionUITestAdapter extends CommonTestUtils implements ICodeCompletionTester {
	
	protected IFile file;
	protected ISourceModule srcModule;
	protected ScriptEditor editor; // TODO, close editor on Dynamic AfterClass
	
	public CodeCompletionUITestAdapter(IFile file) {
		this.file = file;
		srcModule = DLTKCore.createSourceModuleFrom(file);
		IWorkbenchPage page = WorkbenchUtils.getActivePage();
		try {
			editor = (ScriptEditor) IDE.openEditor(page, file, DeeEditor.EDITOR_ID);
		} catch(PartInitException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
		assertTrue(editor.getScriptSourceViewer() != null);
	}
	
	@Override
	public void runAfters() {
		SWTTestUtils.clearEventQueue();
	}
	
	@Override
	public void testComputeProposalsWithRepLen(int repOffset, int prefixLen, 
			int repLen, boolean removeObjectIntrinsics, String... expectedProposals) throws ModelException {
		
		ICompletionProposal[] proposals = DeeCodeContentAssistProcessor
				.computeProposals(repOffset, srcModule, srcModule.getSource(), new CompletionSession());
		assertNotNull(proposals, "Code Completion Unavailable");
		
		
		Function<ICompletionProposal, DefUnit> proposalToDefunit  = new Function<ICompletionProposal, DefUnit>() {
			@Override
			public DefUnit evaluate(ICompletionProposal obj) {
				return obj == null ? null : ((DeeCompletionProposal) obj).defUnit;
			}
		};
		List<DefUnit> results = mapOut(list(proposals), proposalToDefunit, new ArrayList<DefUnit>());

		CodeCompletion__Common.checkProposals(prefixLen, results, expectedProposals, removeObjectIntrinsics);
		
		checkProposals(repOffset, repLen, prefixLen, proposals);
		invokeContentAssist();
		SWTTestUtils.________________clearEventQueue________________();
	}

	protected void invokeContentAssist() {
		ITextOperationTarget target= (ITextOperationTarget) editor.getAdapter(ITextOperationTarget.class);
		if (target != null && target.canDoOperation(ISourceViewer.CONTENTASSIST_PROPOSALS)) {
			target.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
		}
	}
	
	
	protected static void checkProposals(int repOffset, int repLen, int prefixLen, ICompletionProposal[] proposals) {
		
		for(ICompletionProposal iCompletionProposal : proposals) {
			DeeCompletionProposal proposal = (DeeCompletionProposal) iCompletionProposal;
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