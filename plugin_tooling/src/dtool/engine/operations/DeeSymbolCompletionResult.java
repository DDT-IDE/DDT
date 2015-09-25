/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
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
import melnorme.lang.tooling.CompletionProposalKind;
import melnorme.lang.tooling.ElementAttributes;
import melnorme.lang.tooling.ElementLabelInfo;
import melnorme.lang.tooling.ToolCompletionProposal;
import melnorme.lang.tooling.ast.SourceRange;
import melnorme.lang.tooling.completion.CompletionLocationInfo;
import melnorme.lang.tooling.completion.LangCompletionResult;
import melnorme.lang.tooling.engine.OverloadedNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Collection2;
import dtool.ast.definitions.ICallableElement;
import dtool.ast.definitions.IFunctionParameter;
import dtool.parser.structure.DeeLabelInfoProvider;

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
			if(result instanceof OverloadedNamedElement) {
				OverloadedNamedElement overload = (OverloadedNamedElement) result;
				
				for (INamedElement namedElement : overload.getOverloadedElements()) {
					proposals.add(createProposal(locationInfo, getReplaceLength(), namedElement));	
				}
				
			} else {
				proposals.add(createProposal(locationInfo, getReplaceLength(), result));
			}
		}
		
		return new LangCompletionResult(proposals);
	}
	
	public static ToolCompletionProposal createProposal(CompletionLocationInfo invocationInfo, int replaceLength, 
			INamedElement namedElem) {
		int rplOffset = invocationInfo.offset - invocationInfo.namePrefixLen;
		replaceLength = replaceLength + invocationInfo.namePrefixLen;
		
		String rplName = namedElem.getName();
		String rplString = rplName;
		String moduleName = namedElem.getModuleFullName();
		String baseLabel = DeeNamedElementLabelProvider.getLabelForContentAssistPopup(namedElem);
		
		String fullReplaceString = rplString;
		ArrayList2<SourceRange> subElements = null;
		if(namedElem instanceof ICallableElement) {
			ICallableElement callableElement = (ICallableElement) namedElem;
			subElements = new ArrayList2<SourceRange>();
			fullReplaceString = getFullReplaceString(rplString, callableElement, subElements);
		}
		
		ElementLabelInfo elementLabelInfo = new DeeLabelInfoProvider().getLabelInfo(namedElem);
		
		CompletionProposalKind kind = elementLabelInfo.proposalKind;
		ElementAttributes attributes = elementLabelInfo.elementAttribs;
		
		return new ToolCompletionProposal(rplOffset, replaceLength, rplString, baseLabel, kind, attributes, 
			moduleName, null, fullReplaceString, subElements, namedElem);
	}
	
	public static String getFullReplaceString(String rplString, ICallableElement callableElement, 
			ArrayList2<SourceRange> subElements) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(rplString);
		
		sb.append("(");
		boolean first = true;
		for (IFunctionParameter param : callableElement.getParameters()) {
			if(!first) {
				sb.append(", ");
			}
			
			String paramName = getParamNameSuggestion(param);
			
			subElements.add(new SourceRange(sb.length(), paramName.length()));
			sb.append(paramName);
			
			first = false;
		}
		sb.append(")");
		
		return sb.toString();
	}
	
	protected static String getParamNameSuggestion(IFunctionParameter param) {
		if(param instanceof INamedElement) {
			INamedElement namedParam = (INamedElement) param;
			return namedParam.getName();
		} else {
			return "__";
		}
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