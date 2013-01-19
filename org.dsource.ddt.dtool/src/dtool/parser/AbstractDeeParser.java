package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.ArrayList;

import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoNode;
import dtool.ast.SourceRange;
import dtool.parser.ParserError.EDeeParserErrors;
import dtool.parser.Token.ErrorToken;

public class AbstractDeeParser {
	
	protected final DeeLexer deeLexer;
	protected Token lastToken = null;
	protected Token tokenAhead = null;
	protected ArrayList<ParserError> errors = new ArrayList<ParserError>();
	protected ArrayList<ParserError> pendingMissingTokenErrors = new ArrayList<ParserError>();
	
	public AbstractDeeParser(DeeLexer deeLexer) {
		this.deeLexer = deeLexer;
	}
	
	protected final Token getLastToken() {
		return lastToken;
	}
	
	protected ParserError addError(EDeeParserErrors errorType, SourceRange sourceRange, String errorSource, 
		Object obj2) {
		ParserError error = new ParserError(errorType, sourceRange, errorSource, obj2);
		errors.add(error);
		return error;
	}
	
	public int getCurrentParserPosition() {
		if(tokenAhead != null) {
			return tokenAhead.getStartPos();
		} else {
			return deeLexer.getLexingPosition();
		}
	}
	
	protected final Token lookAheadToken() {
		if(tokenAhead != null) {
			return tokenAhead;
		}
		while(true) {
			Token token = deeLexer.next();
			
			DeeTokens tokenType = token.type;
			
			if(tokenType == DeeTokens.ERROR) {
				ErrorToken errorToken = (ErrorToken) token;
				if(errorToken.originalToken == DeeTokens.ERROR) {
					addError(EDeeParserErrors.INVALID_TOKEN_CHARACTERS, sr(token), token.tokenSource, null);
					continue; // Fetch another token
				} else {
					// TODO tests
					addError(EDeeParserErrors.MALFORMED_TOKEN, sr(token), token.tokenSource, 
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
	
	public DeeTokens lookAhead() {
		return lookAheadToken().type;
	}
	
	public boolean lookAheadIsType(DeeTokens... tokens) {
		for (int i = 0; i < tokens.length; i++) {
			if(lookAhead() == tokens[i]) {
				return true;
			}
		}
		return false;
	}
	
	protected final Token consumeInput() {
		if(tokenAhead == null) {
			lookAheadToken();
		}
		
		lastToken = tokenAhead;
		tokenAhead = null;
		return lastToken;
	}
	
	protected final Token consumeLookAhead() {
		assertNotNull(tokenAhead);
		return consumeInput();
	}
	
	protected final Token consumeIf(DeeTokens tokenType) {
		if(lookAhead() == tokenType) {
			return consumeLookAhead();
		}
		return null;
	}
	
	protected final boolean tryConsume(DeeTokens tokenType) {
		if(lookAhead() == tokenType) {
			consumeLookAhead();
			return true;
		}
		return false;
	}
	
	/** Attempt to consume a token of given type.
	 * If it fails, creates an error using the range of previous token. */
	protected final Token consumeExpectedToken(DeeTokens expectedTokenType) {
		if(lookAhead() == expectedTokenType) {
			return consumeLookAhead();
		} else {
			reportErrorExpectedToken(expectedTokenType);
			return null;
		}
	}
	
	public void reportErrorExpectedToken(DeeTokens expected) {
		reportMissingTokenError(EDeeParserErrors.EXPECTED_TOKEN, expected);
	}
	
	public void reportErrorExpectedRule(String expectedRule) {
		reportMissingTokenError(EDeeParserErrors.EXPECTED_RULE, expectedRule);
	}
	
	public void reportMissingTokenError(EDeeParserErrors parserError, Object msgObj2) {
		String errorSource = lastToken.tokenSource;
		ParserError error = addError(parserError, sr(lastToken), errorSource, msgObj2);
		pendingMissingTokenErrors.add(error);
	}
	
	public void reportSyntaxError(Token lookAheadToken, String expectedRule) {
		addError(EDeeParserErrors.SYNTAX_ERROR, sr(lookAheadToken), lookAheadToken.tokenSource, expectedRule);
	}
	
	protected final <T extends ASTNeoNode> T connect(T node) {
		for (ParserError parserError : pendingMissingTokenErrors) {
			if(parserError.msgObj2 != DeeTokens.IDENTIFIER) {
				parserError.originNode = node;
			}
		}
		pendingMissingTokenErrors = new ArrayList<ParserError>();
		return node;
	}
	
	protected final <T extends IASTNeoNode> T connect(T node) {
		connect((ASTNeoNode) node);
		return node;
	}
	
	public static SourceRange sr(Token token) {
		return new SourceRange(token.getStartPos(), token.getLength());
	}
	
	public static SourceRange sr(Token tokStart, Token tokEnd) {
		int length = tokEnd.getEndPos() - tokStart.getStartPos();
		return new SourceRange(tokStart.getStartPos(), length);
	}
	
}