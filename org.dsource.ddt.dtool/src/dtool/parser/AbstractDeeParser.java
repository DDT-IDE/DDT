package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import melnorme.utilbox.misc.ArrayUtil;
import descent.internal.compiler.parser.Comment;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoNode;
import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;
import dtool.ast.definitions.DefUnit.DefUnitTuple;
import dtool.parser.ParserError.EDeeParserErrors;
import dtool.util.ArrayView;

/**
 * Basic parser functionality. 
 */
public class AbstractDeeParser {
	
	protected final DeeLexer deeLexer;
	
	protected Token tokenAhead = null;
	protected Token lastRealToken = null;
	
	protected ArrayList<ParserError> errors = new ArrayList<ParserError>();
	protected ArrayList<ParserError> pendingMissingTokenErrors = new ArrayList<ParserError>();
	
	public AbstractDeeParser(DeeLexer deeLexer) {
		this.deeLexer = deeLexer;
	}
	
	public CharSequence getSource() {
		return deeLexer.source;
	}
	
	public String getSource(SourceRange sourceRange) {
		return getSource().subSequence(sourceRange.getStartPos(), sourceRange.getEndPos()).toString();
	}
	
	protected final Token getLastToken() {
		return lastRealToken;
	}
	
	public final int getLastTokenEndPos() {
		return lastRealToken.getEndPos();
	}
	
	public int getParserPosition() {
		if(tokenAhead != null) {
			return tokenAhead.getStartPos();
		} else {
			return deeLexer.getLexingPosition();
		}
	}
	
	protected ParserError addError(EDeeParserErrors errorType, SourceRange sr, String errorSource, Object msgData) {
		assertEquals(getSource(sr), errorSource);
		ParserError error = new ParserError(errorType, sr, errorSource, msgData);
		errors.add(error);
		return error;
	}
	
	protected ParserError addError(EDeeParserErrors errorType, Token errorToken, Object msgData) {
		return addError(errorType, sr(errorToken), errorToken.tokenSource, msgData);
	}
	
	protected ParserError addError(EDeeParserErrors errorType, SourceRange sourceRange, Object msgData) {
		return addError(errorType, sourceRange, getSource(sourceRange), msgData);
	}
	
	static{ assertTrue(DeeTokens.EOF.isParserIgnored == false); }
	protected final Token lookAheadToken() {
		if(tokenAhead != null) {
			return tokenAhead;
		}
		while(true) {
			Token token = deeLexer.next();
			
			DeeTokens tokenType = token.type;
			
			if(tokenType.isParserIgnored) {
				if(tokenType == DeeTokens.INVALID_TOKEN) {
					addError(EDeeParserErrors.INVALID_TOKEN_CHARACTERS, token, null);
				} else {
					consumeToken(token);
				}
				continue;
			}
			
			tokenAhead = token;
			return tokenAhead;
		}
	}
	
	public void consumeToken(Token token) {
		DeeTokenSemantics.checkTokenErrors(token, this);
	}
	
	public DeeTokens lookAhead() {
		return lookAheadToken().getRawTokenType();
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
		
		consumeToken(tokenAhead);
		lastRealToken = tokenAhead;
		tokenAhead = null;
		return lastRealToken;
	}
	
	protected final Token consumeLookAhead() {
		assertNotNull(tokenAhead);
		return consumeInput();
	}
	
	protected final Token consumeLookAhead(DeeTokens tokenType) {
		assertTrue(lookAhead() == tokenType);
		return consumeLookAhead();
	}
	
	/* -- Source consume helpers -- */
	
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
	 * If it fails, creates an error using the range of last token. */
	protected final Token consumeExpectedToken(DeeTokens expectedTokenType) {
		if(lookAhead() == expectedTokenType) {
			return consumeLookAhead();
		} else {
			reportErrorExpectedToken(expectedTokenType);
			return null;
		}
	}
	
	protected Token tryConsumeIdentifier() {
		Token id = consumeExpectedToken(DeeTokens.IDENTIFIER);
		if(id == null) {
			id = missingIdToken(getParserPosition());
		}
		return id;
	}
	
	public static String MISSING_ID_VALUE = "";
	
	protected Token missingIdToken(int startPos) {
		return missingToken(DeeTokens.IDENTIFIER, startPos);
	}
	
	protected Token missingToken(DeeTokens identifier, int startPos) {
		return new Token(identifier, MISSING_ID_VALUE, startPos) {
			@Override
			public int getLength() {
				return 0;
			}
			
			@Override
			public int getEndPos() {
				return startPos;
			}
		};
	}
	
	public static boolean isMissingId(Token id) {
		return id.tokenSource == MISSING_ID_VALUE;
	}
	
	/* ---- error helpers ---- */
	
	protected void reportErrorExpectedToken(DeeTokens expected) {
		reportError(EDeeParserErrors.EXPECTED_TOKEN, expected, true);
	}
	
	protected void reportErrorExpectedRule(String expectedRule) {
		reportError(EDeeParserErrors.EXPECTED_RULE, expectedRule, false);
	}
	
	protected void reportSyntaxError(String expectedRule) {
		reportError(EDeeParserErrors.SYNTAX_ERROR, expectedRule, false);
	}
	
	protected void reportError(EDeeParserErrors parserError, Object msgData, boolean missingToken) {
		ParserError error = addError(parserError, lastRealToken, msgData);
		if(missingToken) {
			pendingMissingTokenErrors.add(error);
		}
	}
	protected final <T extends ASTNeoNode> T connect(T node) {
		for (ParserError parserError : pendingMissingTokenErrors) {
			if(parserError.msgData != DeeTokens.IDENTIFIER) {
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
	
	protected static final <T extends ASTNeoNode> T konnect(T node) {
		// TODO: remove this after putting test
		/*BUG here*/
		return node;
	}
	
	/* ---- Node creation helpers ---- */
	
	public static SourceRange srStartToEnd(int startPos, int endPos) {
		assertTrue(startPos >= 0 && endPos >= startPos);
		return new SourceRange(startPos, endPos - startPos);
	}
	
	public static SourceRange sr(Token token) {
		return token.getSourceRange();
	}
	
	/** @return SourceRange of given declStart to current parser position. */
	public final SourceRange srToCursor(int declStart) {
		return srStartToEnd(declStart, getParserPosition());
	}
	
	public final SourceRange srToCursor(Token startToken) {
		return srToCursor(startToken.getStartPos());
	}
	
	public final SourceRange srToCursor(ASTNeoNode startNode) {
		return srToCursor(startNode.getStartPos());
	}
	
	public TokenInfo tokenInfo(Token idToken) {
		if(idToken == null) {
			return null;
		}
		assertTrue(idToken.type == DeeTokens.IDENTIFIER);
		return new TokenInfo(idToken.tokenSource, idToken.getStartPos());
	}
	
	public DefUnitTuple defUnitTuple(SourceRange sourceRange, Token id, Comment[] comments) {
		return new DefUnitTuple(sourceRange, tokenInfo(id), comments);
	}
	
	public DefUnitTuple defUnitRaw(SourceRange sourceRange, Token id) {
		return defUnitTuple(sourceRange, id, null);
	}
	
	public static <T extends IASTNeoNode> ArrayView<T> arrayView(Collection<? extends T> list, Class<T> cpType) {
		return ArrayView.create(ArrayUtil.createFrom(list, cpType));
	}
	
	public static ArrayView<ASTNeoNode> arrayView(Collection<? extends ASTNeoNode> list) {
		return ArrayView.create(ArrayUtil.createFrom(list, ASTNeoNode.class));
	}
	
	public static ArrayView<String> arrayViewS(Collection<String> list) {
		return ArrayView.create(ArrayUtil.createFrom(list, String.class));
	}
	
}