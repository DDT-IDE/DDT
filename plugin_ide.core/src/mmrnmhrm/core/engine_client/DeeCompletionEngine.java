package mmrnmhrm.core.engine_client;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;

import melnorme.lang.tooling.engine.completion.CompletionSearchResult;
import melnorme.lang.tooling.engine.completion.CompletionSearchResult.PrefixSearchOptions;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.Location;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCoreMessages;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.codeassist.ScriptCompletionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.dltk.core.CompletionContext;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.CompletionRequestor;

public class DeeCompletionEngine extends ScriptCompletionEngine {
	
	protected CompletionRequestor getRequestor() {
		return requestor;
	}
	
	@Override
	public void complete(IModuleSource moduleSource, final int position, int i) {
		assertNotNull(requestor);
		requestor.beginReporting();
		try {
			CompletionContext context = new CompletionContext();
			requestor.acceptContext(context);
			
			Location compilerPath = getCompilerPath(moduleSource);
			CompletionSearchResult completionResult = DToolClient.getDefault().runCodeCompletion(
				moduleSource, position, compilerPath);
			if(completionResult.isFailure()) {
				handleCompletionFailure(DeeCoreMessages.ContentAssist_LocationFailure, position);
				return;
			}
			
			for (INamedElement result : completionResult.getResults()) {
				CompletionProposal proposal = createProposal(result, position, completionResult);
				requestor.accept(proposal);
			}
			
		} catch (CoreException e) {
			DeeCore.logStatus(e);
			handleCompletionFailure(e.getMessage(), position);
		} finally {
			requestor.endReporting();
		}
	}
	
	@SuppressWarnings("unused")
	protected Location getCompilerPath(IModuleSource moduleSource) {
		return null; // Use default
	}
	
	protected void handleCompletionFailure(String errorMessage, final int position) {
		requestor.completionFailure(
			new DefaultProblem(errorMessage, null, null, ProblemSeverity.ERROR, position, position, 0));
	}
	
	protected CompletionProposal createProposal(INamedElement namedElem, int ccOffset, 
			CompletionSearchResult completionResult) {
		PrefixSearchOptions searchOptions = completionResult.searchOptions;
		
		String rplName = namedElem.getName();
		
		String rplStr = rplName.substring(searchOptions.namePrefixLen);
		
		CompletionProposal proposal = new RefSearchCompletionProposal(ccOffset);
		proposal.setName(namedElem.getExtendedName());
		proposal.setCompletion(rplStr);
		proposal.setReplaceRange(ccOffset, ccOffset + completionResult.getReplaceLength());
		proposal.setExtraInfo(namedElem);
		
		return proposal;
	}
	
	public static class RefSearchCompletionProposal extends CompletionProposal {
		
		protected RefSearchCompletionProposal(int completionLocation) {
			super(CompletionProposal.TYPE_REF, completionLocation);
		}
		
		@Override
		public void setExtraInfo(Object extraInfo) {
			assertTrue(extraInfo instanceof INamedElement);
			super.setExtraInfo(extraInfo);
		}
		
		@Override
		public INamedElement getExtraInfo() {
			return (INamedElement) super.getExtraInfo();
		}
		
	}
	
}