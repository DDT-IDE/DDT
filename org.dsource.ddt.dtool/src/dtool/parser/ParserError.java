package dtool.parser;

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import dtool.ast.ASTNeoNode;
import dtool.ast.SourceRange;

public class ParserError {
	
	protected final EDeeParserErrors errorType;
	protected final SourceRange sourceRange;
	protected final String msgErrorSource;
	protected final Object msgObj2;
	public ASTNeoNode originNode;
	
	public ParserError(EDeeParserErrors errorType, SourceRange sourceRange, String msgErrorSource, Object msgObj2) {
		this.errorType = assertNotNull_(errorType);
		this.sourceRange = assertNotNull_(sourceRange);
		this.msgErrorSource = msgErrorSource;
		this.msgObj2 = msgObj2;
	}
	
	public String getUserMessage() {
		switch (errorType) {
		case UNKNOWN_TOKEN:
			return "Invalid token characters \"" + msgErrorSource + "\", delete these characters.";
		case MALFORMED_TOKEN:
			return "XXX";
		case EXPECTED_TOKEN:
			DeeTokens expToken = (DeeTokens) msgObj2;
			return "Syntax error on token \"" + msgErrorSource + "\", expected " + expToken + " after.";
		case TOKEN_SYNTAX_ERROR:
			return "Unexpected token \"" + msgErrorSource + "\", delete this token.";
		}
		throw assertFail();
	}
	
	@Override
	public String toString() {
		return "ERROR:" + errorType + sourceRange.toString() + 
			(msgErrorSource == null ? "" : (" :" + msgErrorSource)) + " obj2:" + msgObj2;
	}
	
}
