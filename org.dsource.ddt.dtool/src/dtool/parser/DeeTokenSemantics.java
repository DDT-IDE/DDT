package dtool.parser;

import dtool.parser.ParserError.EDeeParserErrors;

public class DeeTokenSemantics {
	
	public static void checkTokenErrors(Token token, AbstractDeeParser parser) {
		if(token.getError() != null) {
			parser.addError(EDeeParserErrors.MALFORMED_TOKEN, token, token.getError());
			return;
		} //else
		
		// Check token content validity  TODO: strings, unicode escapes, HTML entities, etc.
		switch (token.type) {
		case CHAR_LITERAL:
			if(token.tokenSource.length() > 3) {
				parser.addError(EDeeParserErrors.MALFORMED_TOKEN, token, 
					LexerErrorTypes.CHAR_LITERAL_SIZE_GREATER_THAN_ONE);
			}
			break;
		default:
			break;
		}
	}
	
}