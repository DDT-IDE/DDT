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

import java.util.ArrayList;

import dtool.ast.definitions.INamedElement;

public class CompletionSearchResult {
	
	public final ECompletionResultStatus resultCode;
	public final PrefixSearchOptions searchOptions;
	public final ArrayList<INamedElement> results;
	
	public CompletionSearchResult(ECompletionResultStatus resultCode, PrefixSearchOptions searchOptions,
			ArrayList<INamedElement> results) {
		this.resultCode = resultCode;
		this.searchOptions = assertNotNull(searchOptions);
		this.results = results;
	}
	
	public ArrayList<INamedElement> getResults() {
		return results;
	}
	
	public ECompletionResultStatus getResultCode() {
		return resultCode;
	}
	
	public PrefixSearchOptions getSearchOptions() {
		return searchOptions;
	}
	
	public int getReplaceLength() {
		return searchOptions.rplLen;
	}
	
	public static class PrefixSearchOptions {
		
		public String searchPrefix = "";
		public int namePrefixLen = 0;
		public int rplLen = 0;
		public boolean isImportModuleSearch = false;
		
		public PrefixSearchOptions() {
		}
		
	}
	
	public enum ECompletionResultStatus {
		
		RESULT_OK("ok"),
		INVALID_TOKEN_LOCATION("invalid_token"),
		INVALID_REFQUAL_LOCATION("invalid_qualified"),
		;
		
		protected final String id;
		
		ECompletionResultStatus(String id) {
			this.id = assertNotNull(id);
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