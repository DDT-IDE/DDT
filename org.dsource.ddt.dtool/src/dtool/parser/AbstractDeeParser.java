package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.ast.SourceRange;

public class AbstractDeeParser {
	
	protected final DeeLexer deeLexer;
	protected Token lastToken = null;
	protected Token tokenAhead = null;

	public AbstractDeeParser(DeeLexer deeLexer) {
		this.deeLexer = deeLexer;
	}
	
	protected final Token getLastToken() {
		return lastToken;
	}
	
	protected final Token lookAhead() {
		if(tokenAhead != null) {
			return tokenAhead;
		}
		while(true) {
			Token token = deeLexer.next();
			if(!token.tokenType.isParserIgnored) { // EOF can not be parser ignored
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
		Token next = lookAhead();
		if(next.tokenType == expectedTokenType) {
			consumeLookAhead();
			return next;
		} else {
			//
			Token lastToken = getLastToken();
			SourceRange sourceRange = srFromToken(lastToken);
			pushError(DeeParserErrors.ASDF, lastToken.value, expectedTokenType.name(), sourceRange);
			return null;
		}
	}
	
	public static SourceRange srFromToken(Token token) {
		return new SourceRange(token.getStartPos(), token.getLength());
	}
	
	public static SourceRange srFromToken(Token tokStart, Token tokEnd) {
		return new SourceRange(tokStart.getStartPos(), tokEnd.getLength());
	}
	
	protected void pushError(DeeParserErrors error, String obj1, String obj2, SourceRange sourceRange) {
		String message = "Syntax Error on token " + obj1 + 
			", expected " + obj2 + " after this token";
		// TODO Auto-generated method stub
	}
	
	protected int consumeInputUntil(DeeTokens token1) {
		while(true) {
			consumeInput();
			if(lastToken.tokenType == token1 || lastToken.tokenType == DeeTokens.EOF) { // BUG here
				return 0;
			}
			if(lastToken.tokenType == DeeTokens.EOF) {
				return -1;
			}
		}
	}
	
	protected int consumeInputUntil(DeeTokens token1, DeeTokens token2) {
		while(true) {
			consumeInput();
			if(lastToken.tokenType == token1 || lastToken.tokenType == DeeTokens.EOF) { // BUG here
				return 0;
			}
			if(lastToken.tokenType == token1 || lastToken.tokenType == DeeTokens.EOF) { // BUG here
				return 1;
			}
			if(lastToken.tokenType == DeeTokens.EOF) {
				return -1;
			}
		}	
	}
	
}