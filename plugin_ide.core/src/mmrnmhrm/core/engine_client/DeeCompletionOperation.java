/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.engine_client;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.tooling.engine.completion.CompletionSearchResult;
import melnorme.lang.tooling.engine.completion.CompletionSearchResult.PrefixSearchOptions;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.misc.Location;
import mmrnmhrm.core.DeeCoreMessages;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.CompletionRequestor;

public class DeeCompletionOperation {
	
	// Tests may modify this variable, but only tests
	public static volatile Location compilerPathOverride = null;
	
	protected final DToolClient dtoolClient;
	
	public DeeCompletionOperation(DToolClient dtoolClient) {
		this.dtoolClient = dtoolClient;
	}
	
	public ArrayList2<RefSearchCompletionProposal> execute(Path filePath, int offset, String source, int timeoutMillis) 
			throws CoreException {
		CompletionSearchResult completionResult = dtoolClient.performCompletionOperation(
			filePath, offset, source, timeoutMillis);
		
		return completionResultAdapt(completionResult, offset);
	}
	
	public ArrayList2<RefSearchCompletionProposal> completionResultAdapt(CompletionSearchResult completionResult,
			final int position)
			throws CoreException {
		if(completionResult.isFailure()) {
			throw LangCore.createCoreException(DeeCoreMessages.ContentAssist_LocationFailure +
				completionResult.resultCode.getMessage(), null);
		}
		
		ArrayList2<RefSearchCompletionProposal> proposals = new ArrayList2<>();
		for (INamedElement result : completionResult.getResults()) {
			RefSearchCompletionProposal proposal = createProposal(result, position, completionResult);
			proposals.add(proposal);
		}
		return proposals;
	}
	
	protected static void handleCompletionFailure(String errorMessage, final int position, CompletionRequestor completionRequestor) {
		completionRequestor.completionFailure(
			new DefaultProblem(errorMessage, null, null, ProblemSeverity.ERROR, position, position, 0));
	}
	
	protected static RefSearchCompletionProposal createProposal(INamedElement namedElem, int ccOffset, 
			CompletionSearchResult completionResult) {
		PrefixSearchOptions searchOptions = completionResult.searchOptions;
		
		String rplName = namedElem.getName();
		
		String rplStr = rplName.substring(searchOptions.namePrefixLen);
		
		RefSearchCompletionProposal proposal = new RefSearchCompletionProposal(ccOffset);
		proposal.setName(namedElem.getExtendedName());
		proposal.setCompletion(rplStr);
		proposal.setReplaceRange(ccOffset, ccOffset + completionResult.getReplaceLength());
		proposal.setExtraInfo(namedElem);
		
		return proposal;
	}
	
	/* FIXME: review this code, remove DLTK deps */
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