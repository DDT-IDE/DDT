/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc2.ArrayListDeque;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoNode;
import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.util.ArrayView;

/**
 * Basic parser functionality.
 * Maintains a queue of lookahead elements from the parser.
 * Holds an error list data; 
 */
public class AbstractParser {
	
	public static class LexElement {
		
		public final Token[] ignoredPrecedingTokens;
		public final Token token;
		
		public LexElement(Token[] ignoredPrecedingTokens, Token token) {
			this.ignoredPrecedingTokens = ignoredPrecedingTokens;
			this.token = token;
		}
		
		public final int getStartPos() {
			return token.getStartPos();
		}
		
		public final int getEndPos() {
			return token.getEndPos();
		}
		
		public final DeeTokens getType() {
			return token.type;
		}
		
	}
	
	protected final AbstractLexer lexer;
	
	protected ArrayListDeque<LexElement> lookAheadQueue = new ArrayListDeque<AbstractParser.LexElement>();
	protected LexElement lastLexElement = new LexElement(null, new Token(DeeTokens.WHITESPACE, "", 0));
	
	protected ArrayList<ParserError> errors = new ArrayList<ParserError>();
	protected ArrayList<ParserError> pendingMissingTokenErrors = new ArrayList<ParserError>();
	
	public AbstractParser(AbstractLexer deeLexer) {
		this.lexer = deeLexer;
	}
	
	public String getSource() {
		return lexer.getSource();
	}
	
	public String getSource(SourceRange sourceRange) {
		return getSource().subSequence(sourceRange.getStartPos(), sourceRange.getEndPos()).toString();
	}
	
	protected final Token getLastToken() {
		return lastLexElement.token;
	}
	
	public final int getLastTokenEndPos() {
		return lastLexElement.token.getEndPos();
	}
	
	static{ assertTrue(DeeTokens.EOF.isParserIgnored == false); }
	
	public final LexElement lookAheadElement(int laIndex) {
		assertTrue(laIndex >= 0);
		
		while(lookAheadQueue.size() <= laIndex) {
			LexElement newLexElement = produceLexElement();
			lookAheadQueue.add(newLexElement);
		}
		
		return lookAheadQueue.get(laIndex);
	}
	
	public final LexElement produceLexElement() {
		// TODO minor optimize this?
		ArrayList<Token> ignoredTokens = new ArrayList<Token>(1);
		while(true) {
			Token token = lexer.next();
			
			DeeTokens tokenType = token.type;
			
			if(tokenType.isParserIgnored) {
				consumeToken(token);
				ignoredTokens.add(token);
				continue;
			}
			return new LexElement(ArrayUtil.createFrom(ignoredTokens, Token.class), token);
		}
	}
	
	public void consumeToken(Token token) {
		if(token.type == DeeTokens.INVALID_TOKEN) {
			addError(ParserErrorTypes.INVALID_TOKEN_CHARACTERS, token, null);
		} else {
			DeeTokenSemantics.checkTokenErrors(token, this);
		}
	}
	
	public final LexElement lookAheadElement() {
		return lookAheadElement(0);
	}
	
	public final Token lookAheadToken() {
		return lookAheadElement(0).token;
	}
	
	public final DeeTokens lookAhead() {
		return lookAheadElement(0).token.getRawTokenType();
	}
	
	@Deprecated
	// XXX: need to fix this
	public int getParserPosition() {
		if(lookAheadQueue.size() == 0) {
			return lexer.getLexingPosition();
		}
		return lookAheadElement().token.getStartPos();
	}
	
	protected final Token consumeInput() {
		LexElement laElem = lookAheadElement(); // Ensure there is at least one element in queue
		
		consumeToken(laElem.token);
		lastLexElement = laElem;
		lookAheadQueue.removeFirst();
		return laElem.token;
	}
	
	protected final Token consumeLookAhead() {
		return consumeInput();
	}
	
	protected final Token consumeLookAhead(DeeTokens tokenType) {
		assertTrue(lookAhead() == tokenType);
		return consumeLookAhead();
	}
	
	/* ---- Basic error functionality ---- */
	
	protected ParserError addError(ParserErrorTypes errorType, SourceRange sr, String errorSource, Object msgData) {
		assertEquals(getSource(sr), errorSource);
		ParserError error = new ParserError(errorType, sr, errorSource, msgData);
		errors.add(error);
		return error;
	}
	
	protected ParserError addError(ParserErrorTypes errorType, Token errorToken, Object msgData) {
		return addError(errorType, sr(errorToken), errorToken.tokenSource, msgData);
	}
	
	protected ParserError addError(ParserErrorTypes errorType, SourceRange sourceRange, Object msgData) {
		return addError(errorType, sourceRange, getSource(sourceRange), msgData);
	}
	
	
	/* ---- Input consume helpers ---- */
	
	public boolean lookAheadIsType(DeeTokens... tokens) {
		for (int i = 0; i < tokens.length; i++) {
			if(lookAhead() == tokens[i]) {
				return true;
			}
		}
		return false;
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
		reportError(ParserErrorTypes.EXPECTED_TOKEN, expected, true);
	}
	
	protected void reportErrorExpectedRule(String expectedRule) {
		reportError(ParserErrorTypes.EXPECTED_RULE, expectedRule, false);
	}
	
	protected void reportSyntaxError(String expectedRule) {
		reportError(ParserErrorTypes.SYNTAX_ERROR, expectedRule, false);
	}
	
	protected void reportError(ParserErrorTypes parserError, Object msgData, boolean missingToken) {
		ParserError error = addError(parserError, lastLexElement.token, msgData);
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
	
	public final SourceRange srToCursor(LexElement startElement) {
		return srToCursor(startElement.getStartPos());
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