package mmrnmhrm.core.codeassist;

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
		System.out.println(" " + position + " " + i);
		
		requestor.beginReporting();
		CompletionContext context = new CompletionContext();
		requestor.acceptContext(context);
		try {
			this.actualCompletionPosition = position;
			this.offset = i;
			
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
	
	public void doCompletionSearch(final int offset, ISourceModule moduleUnit, String source, CompletionSession session,
			IDefUnitMatchAccepter defUnitAccepter, final CompletionRequestor collector) {
		
		if(defUnitAccepter != null) {
			PrefixDefUnitSearch.doCompletionSearch(offset, moduleUnit, source, session, defUnitAccepter);
		}
		
		IDefUnitMatchAccepter collectorAdapter = new IDefUnitMatchAccepter() {
			@Override
			public void accept(DefUnit defUnit, PrefixSearchOptions searchOptions) {
				String rplStr = defUnit.getName().substring(searchOptions.prefixLen);
				
				CompletionProposal proposal = createProposal(CompletionProposal.TYPE_REF, offset);
				proposal.setName(defUnit.toStringForCodeCompletion());
				proposal.setCompletion(rplStr);
				proposal.setReplaceRange(offset, offset + rplStr.length());
//				proposal.setModelElement(name);
				proposal.setExtraInfo(defUnit);
				
				collector.accept(proposal);
			}
			
		};
		
		PrefixDefUnitSearch.doCompletionSearch(offset, moduleUnit, source, session, collectorAdapter);
	}
	
}