package mmrnmhrm.core.codeassist;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import org.eclipse.dltk.codeassist.ScriptCompletionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.CompletionContext;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;

import dtool.ast.definitions.DefUnit;
import dtool.contentassist.CompletionSession;
import dtool.refmodel.PrefixDefUnitSearch;
import dtool.refmodel.PrefixDefUnitSearch.IDefUnitMatchAccepter;
import dtool.refmodel.PrefixSearchOptions;

public class DeeCompletionEngine extends ScriptCompletionEngine {
	
	@Override
	public void complete(IModuleSource module, int position, int i) {
		assertNotNull(requestor);
		requestor.beginReporting();
		try {
			CompletionContext context = new CompletionContext();
			requestor.acceptContext(context);
			
			// Completion for model elements.
			IModelElement modelElement = module.getModelElement();
			
			if(!(modelElement instanceof ISourceModule)) {
				return;
			}
			ISourceModule sourceModule = (ISourceModule) modelElement;
			
			
			CompletionSession completionSession = new CompletionSession();
			doCompletionSearch(position, sourceModule, module.getSourceContents(), completionSession, null, requestor);
		} finally {
			requestor.endReporting();
		}
		
	}
	
	public void doCompletionSearch(final int ccOffset, ISourceModule moduleUnit, String source, CompletionSession session,
			IDefUnitMatchAccepter defUnitAccepter, final CompletionRequestor collector) {
		
		if(defUnitAccepter != null) {
			PrefixDefUnitSearch.doCompletionSearch(ccOffset, moduleUnit, source, session, defUnitAccepter);
		}
		
		IDefUnitMatchAccepter collectorAdapter = new IDefUnitMatchAccepter() {
			@Override
			public void accept(DefUnit defUnit, PrefixSearchOptions searchOptions) {
				CompletionProposal proposal = createProposal(defUnit, ccOffset, searchOptions);
				collector.accept(proposal);
			}

		};
		
		PrefixDefUnitSearch.doCompletionSearch(ccOffset, moduleUnit, source, session, collectorAdapter);
	}
	
	protected CompletionProposal createProposal(DefUnit defUnit, int ccOffset, PrefixSearchOptions searchOptions) {
		String rplStr = defUnit.getName().substring(searchOptions.prefixLen);
		
		CompletionProposal proposal = createProposal(CompletionProposal.TYPE_REF, ccOffset);
		proposal.setName(defUnit.toStringForCodeCompletion());
		proposal.setCompletion(rplStr);
		proposal.setReplaceRange(ccOffset, ccOffset + searchOptions.rplLen);
		proposal.setExtraInfo(defUnit);
		return proposal;
	}
	
}