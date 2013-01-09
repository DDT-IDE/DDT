package dtool.parser;

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import dtool.ast.ASTNeoNode;
import dtool.ast.SourceRange;

public class ParserError {
	
	public enum EDeeParserErrors {
		
		INVALID_TOKEN_CHARACTERS, // Lexer: invalid characters, cannot form token
		MALFORMED_TOKEN, // Lexer: recovered token has errors // TODO: test
		
		EXPECTED_TOKEN, // Syntax error: expected specific token
		EXPECTED_RULE, // Syntax error: expected valid token for rule
		SYNTAX_ERROR // Syntax error: unexpected rule in rule start
		
	}
	
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
		case INVALID_TOKEN_CHARACTERS:
			return "Invalid token characters \"" + msgErrorSource + "\", delete these characters.";
		case MALFORMED_TOKEN:
			return "Error during tokenization: " + msgErrorSource;
		case EXPECTED_TOKEN:
			DeeTokens expToken = (DeeTokens) msgObj2;
			return "Syntax error on token \"" + msgErrorSource + "\", expected " + expToken + " after.";
		case EXPECTED_RULE:
			return "Unexpected token after \"" + msgErrorSource + "\", while trying to parse " + msgObj2 + ".";
		case SYNTAX_ERROR:
			return "Unexpected token \"" + msgErrorSource + "\", while trying to parse " + msgObj2 + ".";
		}
		throw assertFail();
	}
	
	@Override
	public String toString() {
		return "ERROR:" + errorType + sourceRange.toString() + 
			(msgErrorSource == null ? "" : (" :" + msgErrorSource)) + " obj2:" + msgObj2;
	}
	
}