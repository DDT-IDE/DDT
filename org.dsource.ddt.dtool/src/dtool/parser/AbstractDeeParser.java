package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;

import dtool.ast.SourceRange;
import dtool.parser.Token.ErrorToken;

public class AbstractDeeParser {
	
	protected final DeeLexer deeLexer;
	protected Token lastToken = null;
	protected Token tokenAhead = null;
	protected ArrayList<ParserError> errors = new ArrayList<ParserError>();
	
	public AbstractDeeParser(DeeLexer deeLexer) {
		this.deeLexer = deeLexer;
	}
	
	protected final Token getLastToken() {
		return lastToken;
	}
	
	protected void addError(EDeeParserErrors errorType, SourceRange sourceRange, String errorSource, Object obj2) {
		errors.add(new ParserError(errorType, sourceRange, errorSource, obj2));
	}
	
	protected final Token lookAhead() {
		if(tokenAhead != null) {
			return tokenAhead;
		}
		while(true) {
			Token token = deeLexer.next();
			
			DeeTokens tokenType = token.tokenType;
			
			if(tokenType == DeeTokens.ERROR) {
				ErrorToken errorToken = (ErrorToken) token;
				if(errorToken.originalToken == DeeTokens.ERROR) {
					addError(EDeeParserErrors.UNKNOWN_TOKEN, sr(token), token.value, null);
					continue; // Fetch another token
				} else {
					// TODO tests
					addError(EDeeParserErrors.MALFORMED_TOKEN, sr(token), token.value, 
						errorToken.errorMessage);
					tokenType = errorToken.originalToken;
				}
			}
			
			if(!tokenType.isParserIgnored) { // EOF must not be parser ignored
				tokenAhead = token;
				return tokenAhead;
			}
		}
	}
	
	protected final Token consumeInput() {
		if(tokenAhead == null) {
			lookAhead();
		}
		
		lastToken = tokenAhead;
		tokenAhead = null;
		return lastToken;
	}
	
	protected final Token consumeLookAhead() {
		assertNotNull(tokenAhead);
		
		lastToken = tokenAhead;
		tokenAhead = null;
		return lastToken;
	}
	
	protected final boolean tryConsume(DeeTokens tokenType) {
		if(lookAhead().tokenType == tokenType) {
			consumeLookAhead();
			return true;
		}
		return false;
	}
	
	/** Attempt to consume a token of given type.
	 * If it fails, creates an error using the range of previous token. */
	protected final Token consumeExpectedToken(DeeTokens expectedTokenType) {
		Token la = lookAhead();
		if(la.tokenType == expectedTokenType) {
			consumeLookAhead();
			return la;
		} else {
			return null;
		}
	}
	
	public final void recoverStream(DeeTokens expected, DeeTokens terminatingToken) {
		if(lookAhead().tokenType == terminatingToken) {
			consumeLookAhead();
			
			pushSyntaxErrorBefore(expected, terminatingToken);
		} else {
			pushSyntaxErrorAfter();
			
			consumeLookAhead();
		}
	}
	
	public void pushSyntaxErrorBefore(DeeTokens expected, DeeTokens terminatingToken) {
		assertTrue(lastToken.tokenType == terminatingToken);
		addError(EDeeParserErrors.EXPECTED_TOKEN_BEFORE, sr(lastToken), 
			lastToken.value, expected.name());
	}
	
	public void pushSyntaxErrorAfter() {
		addError(EDeeParserErrors.EXPECTED_OTHER_AFTER, sr(lastToken), 
			lastToken.value, null);
	}
	
	public static SourceRange sr(Token token) {
		return new SourceRange(token.getStartPos(), token.getLength());
	}
	
	public static SourceRange srFromToken(Token tokStart, Token tokEnd) {
		return new SourceRange(tokStart.getStartPos(), tokEnd.getLength());
	}
	
}