package mmrnmhrm.ui.editor.ref;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import melnorme.swtutil.SWTTestUtils;
import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.misc.ReflectionUtils;
import mmrnmhrm.core.codeassist.CompletionEngineSourceTests;
import mmrnmhrm.tests.ui.BaseDeeUITest;
import mmrnmhrm.ui.editor.codeassist.DeeCompletionProposal;
import mmrnmhrm.ui.views.DeeElementLabelProvider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import dtool.ast.definitions.INamedElement;
import dtool.sourcegen.AnnotatedSource;

public class ContentAssistUISourceTests extends CompletionEngineSourceTests {
	
	static {
		MiscUtil.loadClass(BaseDeeUITest.class);
	}
	
	public ContentAssistUISourceTests(String testUIDescription, File file) {
		super(testUIDescription, file);
	}
	
	protected static ScriptEditor editor;
	
	@Override
	public void doAnnotatedTestCleanup() {
		super.doAnnotatedTestCleanup();
		
		SWTTestUtils.________________clearEventQueue________________();
	}
	
	@Override
	public void setupTestProject_do(String explicitModuleName, String projectFolderName, AnnotatedSource testCase)
		throws CoreException, IOException {
		super.setupTestProject_do(explicitModuleName, projectFolderName, testCase);
		
		IFile file = (IFile) sourceModule.getResource();
		editor = BaseDeeUITest.openDeeEditorForFile(file);
		sourceModule.discardWorkingCopy(); // XXX: In the future this might not be necessary
		
		ContentAssistant ca = ContentAssistUI_CommonTest.getContentAssistant(editor);
		ca.enableAutoInsert(false);
		ca.enablePrefixCompletion(false);
		SWTTestUtils.________________clearEventQueue________________();
	}
	
	@Override
	public void runRefSearchTest_________(RefSearchOptions options) {
		testComputeProposalsWithRepLen(options.offset, 0, options.rplLen, options.expectedResults);
	}
	
	public void testComputeProposalsWithRepLen(int offset, int prefixLen, int repLen,
		String... expectedResults) {
		
		ContentAssistant ca = ContentAssistUI_CommonTest.getContentAssistant(editor);
		ReflectionUtils.invokeMethod(ca, "hide"); //ca.hide();
		SWTTestUtils.________________clearEventQueue________________();
		
		ContentAssistUI_CommonTest.invokeContentAssist(editor, offset); 
		
		
		ICompletionProposal[] proposals;
		Object completionProposalPopup = ReflectionUtils.readField(ca, "fProposalPopup");
		proposals = (ICompletionProposal[]) ReflectionUtils.readField(completionProposalPopup, "fComputedProposals");
		
//		DeeCodeCompletionProcessor caProcessor = (DeeCodeCompletionProcessor) 
//			ca.getContentAssistProcessor(DeePartitions.DEE_CODE);
//		proposals = caProcessor.computeCompletionProposals(editor.getViewer(), offset);
		
		assertEqualArrays(proposals, (ICompletionProposal[]) 
			ReflectionUtils.readField(completionProposalPopup, "fFilteredProposals"));
			
		prefixLen = ContentAssistUI_CommonTest.DONT_CHECK; // Don't check TODO
		checkProposals(offset, prefixLen, repLen, proposals, expectedResults);
	}
	
	protected void checkProposals(int repOffset, int prefixLen, int repLen, ICompletionProposal[] proposals, 
		String... expectedResults) {
		if(proposals == null) {
			assertTrue(expectedResults.length == 0);
			return;
		}
		assertNotNull(proposals);
		
		if(expectedResults != null) {
			List<INamedElement> results = proposalResultsToDefUnit(proposals);
			checkResults(results, expectedResults);
		}
		CodeCompletionUITestAdapter.checkProposals(proposals, repOffset, repLen, prefixLen);
	}
	
	@Override
	public void precheckOriginalResults(Collection<INamedElement> resultDefElementsOriginal) {
		for (INamedElement defElement : resultDefElementsOriginal) {
			DeeElementLabelProvider.getLabelForHoverSignature(defElement);
			DeeElementLabelProvider.getLabelForContentAssistPopup(defElement);
		}
	}
	
	public List<INamedElement> proposalResultsToDefUnit(ICompletionProposal[] proposals) {
		ArrayList<INamedElement> results = new ArrayList<>();
		for (ICompletionProposal completionProposal : proposals) {
			if(completionProposal instanceof DeeCompletionProposal) {
				DeeCompletionProposal deeProposal = (DeeCompletionProposal) completionProposal;
				results.add(deeProposal.namedElement);
			}
		}
		return results;
	}
	
}