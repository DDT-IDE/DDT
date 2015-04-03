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
package dtool.engine.operations;

import melnorme.lang.tooling.completion.CompletionSoftFailure;
import melnorme.lang.tooling.completion.LangCompletionProposal;
import melnorme.lang.tooling.engine.completion.CompletionSearchResult;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Collection2;

public class DeeCompletionSearchResult extends CompletionSearchResult {
	
	public DeeCompletionSearchResult(ECompletionResultStatus resultCode) {
		super(resultCode);
	}
	
	public DeeCompletionSearchResult(CompletionLocationInfo searchOptions, Collection2<INamedElement> results) {
		super(searchOptions, results);
	}
	
	public ArrayList2<DeeCompletionProposal> getAdaptedResults() throws CompletionSoftFailure {
		
		if(isFailure()) {
			throw new CompletionSoftFailure(resultCode.getMessage());
		}
		
		int position = locationInfo.offset;
		
		ArrayList2<DeeCompletionProposal> proposals = new ArrayList2<>();
		for (INamedElement result : getResults()) {
			DeeCompletionProposal proposal = createProposal(result, position, this);
			proposals.add(proposal);
		}
		return proposals;
	}
	
	protected static DeeCompletionProposal createProposal(INamedElement namedElem, int ccOffset, 
			CompletionSearchResult completionResult) {
		CompletionLocationInfo searchOptions = completionResult.locationInfo;
		
		String rplName = namedElem.getName();
		
		String rplStr = rplName.substring(searchOptions.namePrefixLen);
		
		return new DeeCompletionProposal(ccOffset, rplStr, completionResult.getReplaceLength(), namedElem);
	}
	
	public static class DeeCompletionProposal extends LangCompletionProposal {
		
		protected final INamedElement namedElement;
		
		protected DeeCompletionProposal(int completionLocation, String replaceString, int replaceLength,
				INamedElement namedElement) {
			super(completionLocation, replaceString, replaceLength, namedElement.getExtendedName());
			this.namedElement = namedElement;
		}
		
		@Override
		public INamedElement getExtraInfo() {
			return namedElement;
		}
		
	}
	
}