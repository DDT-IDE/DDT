package mmrnmhrm.ui.editor.text;

import java.util.ArrayList;
import java.util.Iterator;

import mmrnmhrm.lang.ui.EditorUtil;
import mmrnmhrm.ui.views.DeeElementImageProvider;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.ast.definitions.DefUnit;
import dtool.contentassist.CompletionSession;
import dtool.refmodel.PrefixDefUnitSearch;
import dtool.refmodel.PrefixSearchOptions;
import dtool.refmodel.PrefixDefUnitSearch.IDefUnitMatchAccepter;

// TODO: DTLK: Start using ScriptCompletionProposal ?
public class DeeCodeContentAssistProcessor implements IContentAssistProcessor {
	
	private static final ICompletionProposal[] RESULTS_EMPTY_ARRAY = new ICompletionProposal[0];
	
	private ITextEditor textEditor;
	private String errorMsg;
	ContentAssistant assistant;
	CompletionSession session;
	
	public DeeCodeContentAssistProcessor(ContentAssistant assistant, ITextEditor textEditor) {
		this.textEditor = textEditor;
		this.assistant = assistant;
		this.session = new CompletionSession();
		assistant.addCompletionListener(new ICompletionListener() {
			@Override
			public void assistSessionStarted(ContentAssistEvent event) {
			}
			
			@Override
			public void assistSessionEnded(ContentAssistEvent event) {
				session.errorMsg = null;
				session.invokeNode = null;
			}
			
			@Override
			public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {
			}
		});
	}
	
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			final int offset) {
		
		ISourceModule moduleUnit = EditorUtil.getModuleUnit(textEditor);
		
		String str = viewer.getDocument().get();
		ICompletionProposal[] proposals = computeProposals(offset, moduleUnit, str, session);
		
		errorMsg = session.errorMsg;
		return proposals; 
	}
	
	public static ICompletionProposal[] computeProposals(final int offset,
			ISourceModule moduleUnit, String source, CompletionSession session) {
		
		final ArrayList<DefUnit> defUnitResults = new ArrayList<DefUnit>();
		final ArrayList<ICompletionProposal> results = new ArrayList<ICompletionProposal>();
		
		IDefUnitMatchAccepter defUnitAccepter = new IDefUnitMatchAccepter() {
			@Override
			public Iterator<DefUnit> getResultsIterator() {
				return defUnitResults.iterator();
			};
			
			@Override
			public void accept(DefUnit defUnit, PrefixSearchOptions searchOptions) {
				String rplStr = defUnit.getName().substring(searchOptions.prefixLen);
				defUnitResults.add(defUnit);
				results.add(new DeeCompletionProposal(
						rplStr,
						offset,
						searchOptions.rplLen,
						rplStr.length(),
						DeeElementImageProvider.getNodeImage(defUnit),
						defUnit.toStringForCodeCompletion(),
						defUnit,
						null // context information
				));
			}
			
		};
		
		
		PrefixDefUnitSearch.doCompletionSearch(offset, moduleUnit, source, session, defUnitAccepter);
		
		if(session.errorMsg == null)
			return results.toArray(RESULTS_EMPTY_ARRAY);
		else
			return null;
	}
	
	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		IContextInformation[] result= new IContextInformation[5];
		for (int i= 0; i < result.length; i++)
			result[i] = new ContextInformation(
					"CompletionProcessor.ContextInfo.display.pattern",
					"CompletionProcessor.ContextInfo.value.pattern");
		
		return result; 
	}
	
	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { '.'};
	}
	
	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return new char[] { };
	}
	
	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}
	
	@Override
	public String getErrorMessage() {
		return errorMsg;
	}
	
}
