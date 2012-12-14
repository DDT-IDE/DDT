package dtool.parser;

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import dtool.ast.SourceRange;

public class ParserError {
	
	protected EDeeParserErrors errorType;
	protected SourceRange sourceRange;
	protected String msgErrorSource;
	protected Object msgObj2;
	
	public ParserError(EDeeParserErrors errorType, SourceRange sourceRange, String msgErrorSource, Object msgObj2) {
		this.errorType = assertNotNull_(errorType);
		this.sourceRange = assertNotNull_(sourceRange);
		this.msgErrorSource = msgErrorSource;
		this.msgObj2 = msgObj2;
	}
	
	public String getUserMessage() {
		switch (errorType) {
		case UNKNOWN_TOKEN:
			return "Invalid token, delete these token characters.";
		case MALFORMED_TOKEN:
			return "XXX";
		case EXPECTED_OTHER_AFTER:
			return "Syntax Error on token \"" + msgErrorSource + "\".";
		case EXPECTED_TOKEN_BEFORE:
			return "Syntax Error on token \"" + msgErrorSource + "\", missing " + msgObj2 + " before.";
			
			default: throw assertFail();
		}
	}
	
	@Override
	public String toString() {
		return "ERROR:" + errorType + sourceRange.toString() + " obj1:" + msgErrorSource + " obj2:" + msgObj2;
	}
	
}
