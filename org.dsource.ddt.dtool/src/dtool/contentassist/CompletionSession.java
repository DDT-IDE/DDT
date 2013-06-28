/*******************************************************************************
 * Copyright (c) 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.contentassist;

import static dtool.util.NewUtils.assertNotNull_;


public class CompletionSession {
	
	public enum ECompletionResultStatus {
		RESULT_OK("ok"),
		INVALID_TOKEN_LOCATION("invalid_token"),
		INVALID_REFQUAL_LOCATION("invalid_qualified"),
		OTHER_REFERENCE("other_ref")
		;
		
		protected final String id;
		
		ECompletionResultStatus(String id) {
			this.id = assertNotNull_(id);
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
	
	public ECompletionResultStatus resultCode = null;
	public String errorMsg = null;
	
	public static void assignResult(CompletionSession session, ECompletionResultStatus resultCode, String errorMsg) {
		session.resultCode = resultCode;
		session.errorMsg = errorMsg;
	}
	
}