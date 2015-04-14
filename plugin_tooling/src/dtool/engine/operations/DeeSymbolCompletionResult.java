/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine.operations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling._actual.ToolCompletionProposal;
import melnorme.lang.tooling.completion.CompletionLocationInfo;
import melnorme.lang.tooling.completion.LangCompletionResult;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Collection2;

public class DeeSymbolCompletionResult {
	
	public final ECompletionResultStatus resultCode;
	public final CompletionLocationInfo locationInfo;
	public final int replaceLength;
	public final Collection2<INamedElement> results;
	
	public DeeSymbolCompletionResult(ECompletionResultStatus resultCode) {
		assertTrue(resultCode != ECompletionResultStatus.RESULT_OK);
		this.resultCode = resultCode;
		this.replaceLength = 0;
		this.results = null;
		this.locationInfo = null;
	}
	
	public DeeSymbolCompletionResult(CompletionLocationInfo locationInfo, Collection2<INamedElement> results) {
		this.resultCode = ECompletionResultStatus.RESULT_OK;
		this.locationInfo = assertNotNull(locationInfo);
		this.replaceLength = locationInfo.rplLen;
		this.results = results;
	}
	
	public ECompletionResultStatus getResultCode() {
		return resultCode;
	}
	
	public boolean isFailure() {
		return resultCode != ECompletionResultStatus.RESULT_OK;
	}
	
	public int getReplaceLength() {
		return replaceLength;
	}
	
	public Collection2<INamedElement> getElementResults() {
		return results;
	}
	
	public LangCompletionResult convertToCompletionResult() {
		if(isFailure()) {
			return new LangCompletionResult(resultCode.getMessage());
		}
		
		ArrayList2<ToolCompletionProposal> proposals = new ArrayList2<>();
		for (INamedElement result : getElementResults()) {
			ToolCompletionProposal proposal = createProposal(locationInfo, getReplaceLength(), result);
			proposals.add(proposal);
		}
		
		return new LangCompletionResult(proposals);
	}
	
	public static ToolCompletionProposal createProposal(CompletionLocationInfo invocationInfo, int replaceLength, 
			INamedElement namedElem) {
		String rplName = namedElem.getName();
		String rplString = rplName.substring(invocationInfo.namePrefixLen);
		return new ToolCompletionProposal(invocationInfo.offset, rplString, replaceLength, namedElem);
	}
	
	public enum ECompletionResultStatus {
		
		RESULT_OK("ok", null),
		INVALID_TOKEN_LOCATION("invalid_token", "Invalid location (inside unmodifiable token)"),
		INVALID_TOKEN_LOCATION_FLOAT("after_float_point", "Invalid location (after float decimal point)"),
		;
		
		protected final String id;
		protected final String message;
		
		private ECompletionResultStatus(String id, String message) {
			this.id = assertNotNull(id);
			this.message = message;
		}
		
		public String getId() {
			return id;
		}
		
		public String getMessage() {
			return message;
		}
		
		public static ECompletionResultStatus fromId(String statusId) {
			for (ECompletionResultStatus status : values()) {
				if(status.id.equals(statusId)) {
					return status;
				}
			}
			return null;
		}
		
	}
	
}