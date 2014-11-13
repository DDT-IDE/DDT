package mmrnmhrm.ui.editor.ref;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import melnorme.lang.tooling.ast_actual.ILangNamedElement;
import melnorme.util.swt.SWTTestUtils;
import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.misc.ReflectionUtils;
import mmrnmhrm.core.engine_client.CompletionEngineSourceTests;
import mmrnmhrm.core.engine_client.DToolClient;
import mmrnmhrm.ui.CommonDeeUITest;
import mmrnmhrm.ui.editor.AbstractLangEditor_DLTK;
import mmrnmhrm.ui.editor.codeassist.DeeCompletionProposal;
import mmrnmhrm.ui.views.DeeElementLabelProvider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import dtool.ddoc.TextUI;
import dtool.sourcegen.AnnotatedSource;
import dtool.tests.MockCompilerInstalls;

public class ContentAssistUISourceTests extends CompletionEngineSourceTests {
	
	static {
		MiscUtil.loadClass(CommonDeeUITest.class);
	}
	
	public ContentAssistUISourceTests(String testUIDescription, File file) {
		super(testUIDescription, file);
	}
	
	@BeforeClass
	public static  void beforeTests() {
		DToolClient.defaultCompilerPathOverride = MockCompilerInstalls.EMPTY_COMPILER_INSTALL; 
	}
	
	@AfterClass
	public static void afterTests() {
		DToolClient.defaultCompilerPathOverride = null;
	}
	
	protected static AbstractLangEditor_DLTK editor;
	
	@Override
	public void cleanupTestCase() {
		super.cleanupTestCase();
		
		SWTTestUtils.________________clearEventQueue________________();
	}
	
	@Override
	public void prepareTestCase_do(String explicitModuleName, String projectFolderName, AnnotatedSource testCase)
		throws CoreException, IOException {
		super.prepareTestCase_do(explicitModuleName, projectFolderName, testCase);
		
		IFile file = (IFile) sourceModule.getResource();
		editor = CommonDeeUITest.openDeeEditorForFile(file);
		sourceModule.discardWorkingCopy(); // XXX: In the future this might not be necessary
		
		ContentAssistant ca = ContentAssistUI_CommonTest.getContentAssistant(editor);
		ca.enableAutoInsert(false);
		ca.enablePrefixCompletion(false);
		SWTTestUtils.________________clearEventQueue________________();
	}
	
	@Override
	public void runRefSearchTest_________(RefSearchOptions options) {
		try {
			testComputeProposalsWithRepLen(options.offset, 0, options.rplLen, options.expectedResults);
		} catch (NoSuchFieldException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	public void testComputeProposalsWithRepLen(int offset, int prefixLen, int repLen,
		String... expectedResults) throws NoSuchFieldException {
		
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
			List<ILangNamedElement> results = proposalResultsToDefUnit(proposals);
			checkResults(results, expectedResults);
		}
		ContentAssistUI_CommonTest.checkProposals(proposals, repOffset, repLen, prefixLen);
	}
	
	@Override
	public void precheckOriginalResults(Collection<ILangNamedElement> resultDefElementsOriginal) {
		for (ILangNamedElement defElement : resultDefElementsOriginal) {
			TextUI.getLabelForHoverSignature(defElement);
			DeeElementLabelProvider.getLabelForContentAssistPopup(defElement);
		}
	}
	
	public List<ILangNamedElement> proposalResultsToDefUnit(ICompletionProposal[] proposals) {
		ArrayList<ILangNamedElement> results = new ArrayList<>();
		for (ICompletionProposal completionProposal : proposals) {
			if(completionProposal instanceof DeeCompletionProposal) {
				DeeCompletionProposal deeProposal = (DeeCompletionProposal) completionProposal;
				results.add(deeProposal.namedElement);
			}
		}
		return results;
	}
	
}