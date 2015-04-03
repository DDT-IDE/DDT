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
package melnorme.lang.tooling.engine.completion;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.Collection2;

public class CompletionSearchResult {
	
	public final ECompletionResultStatus resultCode;
	public final CompletionLocationInfo locationInfo;
	public final int replaceLength;
	public final Collection2<INamedElement> results;
	
	public CompletionSearchResult(ECompletionResultStatus resultCode) {
		assertTrue(resultCode != ECompletionResultStatus.RESULT_OK);
		this.resultCode = resultCode;
		this.replaceLength = 0;
		this.results = null;
		this.locationInfo = null;
	}
	
	public CompletionSearchResult(CompletionLocationInfo locationInfo, Collection2<INamedElement> results) {
		this.resultCode = ECompletionResultStatus.RESULT_OK;
		this.locationInfo = assertNotNull(locationInfo);
		this.replaceLength = locationInfo.rplLen;
		this.results = results;
	}
	
	public Collection2<INamedElement> getResults() {
		return results;
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
	
	public static class CompletionLocationInfo {
		
		public final int offset; // The location where completion was invoked
		public final String searchPrefix;
		public final int namePrefixLen;
		public final int rplLen;
		
		public CompletionLocationInfo(int offset) {
			this(offset, "", 0);
		}
		
		public CompletionLocationInfo(int offset, String searchPrefix, int rplLen) {
			assertTrue(rplLen >= 0);
			assertTrue(offset >= 0);
			this.offset = offset;
			this.searchPrefix = searchPrefix;
			this.namePrefixLen = searchPrefix.length();
			this.rplLen = rplLen;
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