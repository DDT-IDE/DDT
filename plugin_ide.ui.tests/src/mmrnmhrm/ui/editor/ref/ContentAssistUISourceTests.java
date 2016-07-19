package mmrnmhrm.ui.editor.ref;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.listFrom;
import static melnorme.utilbox.misc.ArrayUtil.nullToEmpty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import dtool.ast.definitions.EArcheType;
import dtool.ddoc.TextUI;
import dtool.engine.operations.DeeNamedElementLabelProvider;
import dtool.engine.util.NamedElementUtil;
import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import melnorme.lang.ide.ui.editor.structure.AbstractLangStructureEditor;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.util.swt.SWTTestUtils;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.misc.ReflectionUtils;
import mmrnmhrm.core.engine.CoreResolverSourceTests;
import mmrnmhrm.core.engine.DeeLanguageEngine;
import mmrnmhrm.ui.CommonDeeUITest;
import mmrnmhrm.ui.editor.codeassist.DeeContentAssistProposal;

public class ContentAssistUISourceTests extends CoreResolverSourceTests {
	
	static {
		MiscUtil.loadClass(CommonDeeUITest.class);
	}
	
	public ContentAssistUISourceTests(String testUIDescription, File file) {
		super(testUIDescription, file);
	}
	
	protected static AbstractLangStructureEditor editor;
	
	@Override
	public void cleanupTestCase() {
		super.cleanupTestCase();
		
		SWTTestUtils.________________clearEventQueue________________();
	}
	
	@Override
	public void prepareTestCase_do(String explicitModuleName, String projectFolderName, AnnotatedSource testCase)
		throws CoreException, IOException {
		super.prepareTestCase_do(explicitModuleName, projectFolderName, testCase);
		
		IFile file = (IFile) overlayedFile;
		editor = CommonDeeUITest.openDeeEditorForFile(file);
		
		ContentAssistant ca = ContentAssistUI_CommonTest.getContentAssistant(editor);
		ca.enableAutoInsert(false);
		ca.enablePrefixCompletion(false);
		SWTTestUtils.________________clearEventQueue________________();
	}
	
	@Override
	public void runRefSearchTest_________(RefSearchOptions options) {
		try {
			testComputeProposalsWithRepLen(options.offset, options.expectedResults);
		} catch (NoSuchFieldException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	public void testComputeProposalsWithRepLen(int offset, String... expectedResults) throws NoSuchFieldException {
		
		ContentAssistant ca = ContentAssistUI_CommonTest.getContentAssistant(editor);
		ReflectionUtils.invokeMethod(ca, "hide"); //ca.hide();
		SWTTestUtils.________________clearEventQueue________________();
		
		Location previousOverride = DeeLanguageEngine.compilerPathOverride;
		try {
			DeeLanguageEngine.compilerPathOverride = COMPILER_PATH;
			ContentAssistUI_CommonTest.invokeContentAssist(editor, offset); 
		} finally {
			DeeLanguageEngine.compilerPathOverride = previousOverride;
		}
		
		Object completionProposalPopup = ReflectionUtils.readField(ca, "fProposalPopup");
		ICompletionProposal[] proposals = (ICompletionProposal[]) 
				ReflectionUtils.readField(completionProposalPopup, "fComputedProposals");
		ICompletionProposal[] filtered = (ICompletionProposal[]) 
				ReflectionUtils.readField(completionProposalPopup, "fFilteredProposals");
		
		if(nullToEmpty(proposals).length == 0) {
			assertTrue(filtered.length == 0 || 
					(filtered.length == 1 && filtered[0].getClass().getSimpleName().endsWith("EmptyProposal")));
		} else {
			assertEqualArrays(proposals, filtered);
		}
		
		checkProposals(proposals, expectedResults);
	}
	
	protected void checkProposals(ICompletionProposal[] proposals, String... expectedResults) {
		List<INamedElement> results = proposalResultsToDefUnit(proposals);
		checkResults(results, expectedResults);
	}
	
	@Override
	public void precheckOriginalResults(Iterable<INamedElement> resultDefElementsOriginal) {
		for (INamedElement defElement : resultDefElementsOriginal) {
			TextUI.getLabelForHoverSignature(defElement);
			DeeNamedElementLabelProvider.getLabelForContentAssistPopup(defElement);
		}
	}
	
	public List<INamedElement> proposalResultsToDefUnit(ICompletionProposal[] proposals) {
		ArrayList<INamedElement> results = new ArrayList<>();
		for(ICompletionProposal completionProposal : listFrom(proposals)) {
			if(completionProposal instanceof DeeContentAssistProposal) {
				DeeContentAssistProposal deeProposal = (DeeContentAssistProposal) completionProposal;
				results.add(deeProposal.namedElement);
			}
		}
		return results;
	}
	
	@Override
	public void removeDefUnitsFromExpected(LinkedList<INamedElement> resultDefUnits) {
		for (ListIterator<INamedElement> iterator = resultDefUnits.listIterator(); iterator.hasNext(); ) {
			INamedElement defElement = iterator.next();
			
			if(defElement.getArcheType() == EArcheType.Module) {
				String fqName = NamedElementUtil.getElementTypedLabel(defElement);
				if(fqName.equals("object/") || fqName.equals("std.stdio/")) {
					iterator.remove();
				}
			}
		}
	}
	
	/* ----------------- Find def cases - don't test that ----------------- */
	
	@Override
	protected void runFindTest_________(MetadataEntry mde) {
	}
	
	@Override
	protected void runFindFailTest_________(MetadataEntry mde) {
	}
	
	@Override
	protected void runFindMissingTest_________(MetadataEntry mde) {
	}
	
}