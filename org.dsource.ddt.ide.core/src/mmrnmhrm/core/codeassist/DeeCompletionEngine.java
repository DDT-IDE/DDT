package mmrnmhrm.core.codeassist;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import org.eclipse.dltk.codeassist.ScriptCompletionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.CompletionContext;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;

import dtool.ast.definitions.DefUnit;
import dtool.contentassist.CompletionSession;
import dtool.refmodel.PrefixDefUnitSearch;
import dtool.refmodel.PrefixDefUnitSearch.IDefUnitMatchAccepter;
import dtool.refmodel.PrefixSearchOptions;

public class DeeCompletionEngine extends ScriptCompletionEngine {
	
	@Override
	public void complete(IModuleSource module, final int position, int i) {
		assertNotNull(requestor);
		requestor.beginReporting();
		try {
			CompletionContext context = new CompletionContext();
			requestor.acceptContext(context);
			
			// Completion for model elements.
			final IModelElement modelElement = module.getModelElement();
			
			if(!(modelElement instanceof ISourceModule)) {
				return;
			}
			
			ISourceModule sourceModule = (ISourceModule) modelElement;
			String sourceContents = module.getSourceContents();
			CompletionSession completionSession = new CompletionSession();
			IDefUnitMatchAccepter collectorAdapter = new IDefUnitMatchAccepter() {
				@Override
				public void accept(DefUnit defUnit, PrefixSearchOptions searchOptions) {
					CompletionProposal proposal = createProposal(defUnit, position, searchOptions);
					IModelElement me = SourceModelUtil.getTypeHandle(defUnit);
					proposal.setModelElement(me);
					requestor.accept(proposal);
				}
			};
			PrefixDefUnitSearch.doCompletionSearch(position, sourceModule, sourceContents, completionSession, 
					collectorAdapter);
		} finally {
			requestor.endReporting();
		}
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