package dtool.contentassist;

import dtool.ast.ASTNode;

public class CompletionSession {
	
	public enum ECompletionSessionResults {
		RESULT_OK,
		INVALID_LOCATION_INTOKEN,
		INVALID_LOCATION_INSCOPE,
		WEIRD_LOCATION_REFQUAL,
		INVALID_LOCATION_TPLINST,
		NOTIMPLEMENTED
		;
	}
	
	public ECompletionSessionResults resultCode = null;
	public String errorMsg = null;
	public ASTNode invokeNode;
	
	public static void assignResult(CompletionSession session, ECompletionSessionResults resultCode, String errorMsg) {
		session.resultCode = resultCode;
		session.errorMsg = errorMsg;
	}
	
}