package dtool.parser;

import java.util.List;

import dtool.ast.declarations.DeclarationProtection.Protection;
import dtool.parser.ParserError.ParserErrorTypes;

public class DeeTokenSemantics {
	
	public static void checkTokenErrors(Token token, List<ParserError> lexerErrors) {
		if(token.type == DeeTokens.INVALID_TOKEN) {
			lexerErrors.add(createError(ParserErrorTypes.INVALID_TOKEN_CHARACTERS, token, null));
			return;
		}
		
		if(token.getError() != null) {
			lexerErrors.add(createError(ParserErrorTypes.MALFORMED_TOKEN, token, token.getError()));
			return;
		}
		
		// Check token content validity  TODO: strings, unicode escapes, HTML entities, etc.
		switch (token.type) {
		case CHARACTER:
			if(token.source.length() > 3) {
				lexerErrors.add(createError(ParserErrorTypes.MALFORMED_TOKEN, token, 
					LexerErrorTypes.CHAR_LITERAL_SIZE_GREATER_THAN_ONE));
			}
			break;
		default:
			break;
		}
	}
	
	public static ParserError createError(ParserErrorTypes errorType, Token token, Object msgData) {
		return new ParserError(errorType, token.getSourceRange(), token.getSourceValue(), msgData);
	}
	
	public static Protection getProtectionFromToken(DeeTokens token) {
		switch(token) {
		case KW_PRIVATE: return Protection.PRIVATE;
		case KW_PACKAGE: return Protection.PACKAGE;
		case KW_PROTECTED: return Protection.PROTECTED;
		case KW_PUBLIC: return Protection.PUBLIC;
		case KW_EXPORT: return Protection.EXPORT;
		default: return null;
		}
	}
	
}