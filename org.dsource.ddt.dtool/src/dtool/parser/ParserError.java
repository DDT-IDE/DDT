package dtool.parser;

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import melnorme.utilbox.misc.StringUtil;
import descent.core.compiler.Linkage;
import dtool.ast.ASTNeoNode;
import dtool.ast.SourceRange;

public class ParserError {
	
	public enum EDeeParserErrors {
		
		INVALID_TOKEN_CHARACTERS, // Lexer: invalid characters, cannot form token
		MALFORMED_TOKEN, // Lexer: recovered token has errors
		
		EXPECTED_TOKEN, // Syntax error: expected specific token
		EXPECTED_RULE, // Syntax error: expected valid token for rule
		SYNTAX_ERROR, // Syntax error: unexpected rule in rule start
		
		INVALID_EXTERN_ID, //Syntax error: specific error for extern declaration
		EXP_MUST_HAVE_PARENTHESES, //Syntax error: exp must have parentheses to parse
	}
	
	protected final EDeeParserErrors errorType;
	protected final SourceRange sourceRange;
	protected final String msgErrorSource;
	protected final Object msgData;
	public ASTNeoNode originNode;
	
	public ParserError(EDeeParserErrors errorType, SourceRange sourceRange, String msgErrorSource, Object msgData) {
		this.errorType = assertNotNull_(errorType);
		this.sourceRange = assertNotNull_(sourceRange);
		this.msgErrorSource = msgErrorSource;
		this.msgData = msgData;
	}
	
	public String getUserMessage() {
		switch(errorType) {
		case INVALID_TOKEN_CHARACTERS:
			return "Invalid token characters \"" + msgErrorSource + "\", delete these characters.";
		case MALFORMED_TOKEN:
			return "Error during tokenization: " + msgErrorSource;
		case EXPECTED_TOKEN:
			DeeTokens expToken = (DeeTokens) msgData;
			return "Syntax error on token \"" + msgErrorSource + "\", expected " + expToken + " after.";
		case EXPECTED_RULE:
			return "Unexpected token after \"" + msgErrorSource + "\", while trying to parse " + msgData + ".";
		case SYNTAX_ERROR:
			return "Unexpected token \"" + msgErrorSource + "\", while trying to parse " + msgData + ".";
		case INVALID_EXTERN_ID:
			return "Invalid linkage specifier \"" + msgErrorSource + "\", valid ones are: " +
				StringUtil.collToString(Linkage.values(), ",") + ".";
		case EXP_MUST_HAVE_PARENTHESES:
			return "Error " + msgErrorSource + " must be parenthesized when next to operator: " + msgData + ".";
		}
		throw assertFail();
	}
	
	@Override
	public String toString() {
		return "ERROR:" + errorType + sourceRange.toString() +
			(msgErrorSource == null ? "" : (" :" + msgErrorSource)) + " obj2:" + msgData;
	}
	
}