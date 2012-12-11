package dtool.parser;

import dtool.ast.SourceRange;

public class AbstractDeeParser {
	
	protected final DeeLexer deeLexer;
	protected Token lastToken = null;
	protected Token tokenAhead = null;

	public AbstractDeeParser(DeeLexer deeLexer) {
		this.deeLexer = deeLexer;
	}
	
	protected Token getLastToken() {
		return lastToken;
	}
	
	protected Token lookAhead() {
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
	
	protected Token consumeInput() {
		if(tokenAhead == null) {
			lookAhead();
		}
		
		lastToken = tokenAhead;
		tokenAhead = null;
		return lastToken;
	}
	
	protected Token consumeToken(DeeTokens expectedToken) {
		Token next = lookAhead();
		if(next.tokenType == expectedToken) {
			consumeInput();
			return next;
		} else {
			Token lastToken = getLastToken();
			SourceRange sourceRange = srFromToken(lastToken);
			pushError(DeeParserErrors.ASDF, lastToken.value, expectedToken.name(), sourceRange);
			return null;
		}
	}
	
	public static SourceRange srFromToken(Token token) {
		int startPos = token.getStartPos();
		return new SourceRange(startPos, token.getEndPos() - startPos);
	}
	
	protected void pushError(DeeParserErrors error, String obj1, String obj2, SourceRange sourceRange) {
		String message = "Syntax Error on token " + obj1 + 
			", expected " + obj2 + " after this token";
		// TODO Auto-generated method stub
	}
	
	protected int consumeInputUntil(DeeTokens token1) {
		while(true) {
			consumeInput();
			if(lastToken.tokenType == token1 || lastToken.tokenType == DeeTokens.EOF) { //BUG here
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
			if(lastToken.tokenType == token1 || lastToken.tokenType == DeeTokens.EOF) { //BUG here
				return 0;
			}
			if(lastToken.tokenType == token1 || lastToken.tokenType == DeeTokens.EOF) { //BUG here
				return 1;
			}
			if(lastToken.tokenType == DeeTokens.EOF) {
				return -1;
			}
		}	
	}
	
}
