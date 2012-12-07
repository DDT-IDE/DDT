package dtool.parser;

import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;
import dtool.ast.definitions.Module;

public class DeeParser extends AbstractDeeParser {
	
	protected final DeeLexer deeLexer;
	
	protected Token lastToken = null;
	protected Token tokenAhead = null;

	public static DeeParserResult parse(String source) {
		DeeParser deeParser = new DeeParser(new DeeLexer(source));
		return deeParser.parseInput();
	}
	
	public DeeParser(DeeLexer deeLexer) {
		this.deeLexer = deeLexer;
	}
	
	protected DeeParserResult parseInput() {
		Module module = parseModule();
		return new DeeParserResult(module);
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
			if(token.tokenCode != DeeTokens.WHITESPACE) {
				tokenAhead = token;
				return token;
			}
		}
	}
	
	
	protected void consumeInput() {
		lastToken = tokenAhead;
		tokenAhead = null;
	}
	
	protected Token consumeToken(DeeTokens expectedToken) {
		Token next = lookAhead();
		if(next.tokenCode == expectedToken) {
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
	
	private void pushError(DeeParserErrors error, String obj1, String obj2, SourceRange sourceRange) {
		String message = "Syntax Error on token " + obj1 + 
			", expected " + obj2 + " after this token";
		// TODO Auto-generated method stub
	}
	
	private void consumeInputUntil(DeeTokens token1) {
		// TODO Auto-generated method stub
	}
	
	private void consumeInputUntil(DeeTokens token1, DeeTokens token2) {
		// TODO Auto-generated method stub
	}
	
	/* ----------------------------------------------------------------- */
	
	public Module parseModule() {
		Token la = lookAhead();
		
		String[] packages = new String[0];
		
		
		Token id = null;
		if(la.tokenCode == DeeTokens.KW_MODULE) {
			consumeInput();
			
			id = consumeToken(DeeTokens.IDENTIFIER);
			if(id == null) {
				//ERROR RECOVERY
				consumeInputUntil(DeeTokens.SEMICOLON);
			}
			
			consumeToken(DeeTokens.SEMICOLON);
			
		} else {
			pushError(DeeParserErrors.ASDF, null, null, null);
		}
		
		//getSourcePosition();
		SourceRange sourceRange = new SourceRange(0, 1);
		return Module.createModule(sourceRange, null, packages, new TokenInfo(id.value), null, null);
	}

}