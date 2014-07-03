package dtool.resolver.api;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

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