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

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc2.ArrayListDeque;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoNode;
import dtool.ast.SourceRange;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.util.ArrayView;

/**
 * Basic parser functionality.
 * Maintains a queue of lookahead elements from the parser.
 * Holds an error list data; 
 */
public class AbstractParser {
	
	protected final AbstractLexer lexer;
	
	protected ArrayListDeque<LexElement> lookAheadQueue = new ArrayListDeque<AbstractParser.LexElement>();
	protected LexElement lastLexElement = new LexElement(null, new Token(DeeTokens.WHITESPACE, "", 0));
	// This initialization is important for some error reporting:
	protected LexElement lastNonMissingLexElement = lastLexElement; 
	
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
		ArrayList<Token> ignoredTokens = null;
		while(true) {
			Token token = lexer.next();
			
			DeeTokens tokenType = token.type;
			
			if(tokenType.isParserIgnored) {
				analyzeToken(token);
				if(ignoredTokens == null)
					ignoredTokens = new ArrayList<Token>(1);
				ignoredTokens.add(token);
				continue;
			}
			return new LexElement(ArrayUtil.createFrom(ignoredTokens, Token.class), token);
		}
	}
	
	public static class LexElement {
		
		public final Token[] ignoredPrecedingTokens;
		public final Token token;
		
		public LexElement(Token[] ignoredPrecedingTokens, Token token) {
			this.ignoredPrecedingTokens = ignoredPrecedingTokens;
			this.token = assertNotNull_(token);
		}
		
		public LexElement(Token[] ignoredPrecedingTokens, DeeTokens expectedToken, int lookAheadStart) {
			this.ignoredPrecedingTokens = ignoredPrecedingTokens;
			this.token = new MissingToken(expectedToken, lookAheadStart);
		}
		
		public boolean isMissingElement() {
			return token instanceof MissingToken;
		}
		
		public SourceRange getSourceRange() {
			return token.getSourceRange();
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
		
		public int getFullRangeStartPos() {
			assertTrue(isMissingElement() == false);
			if(ignoredPrecedingTokens != null && ignoredPrecedingTokens.length > 0) {
				return ignoredPrecedingTokens[0].getStartPos();
			}
			return token.getStartPos();
		}
		
		protected static class MissingToken extends Token {
			public MissingToken(DeeTokens tokenType, int startPos) {
				super(tokenType, "", startPos);
			}
			
			@Override
			public int getLength() {
				return 0;
			}
			
			@Override
			public int getEndPos() {
				return startPos;
			}
		}

	}
	
	public void analyzeToken(Token token) {
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
	
	public int getParserPosition() {
		return lookAheadElement().getFullRangeStartPos();
	}
	
	protected final LexElement consumeInput() {
		LexElement laElem = lookAheadElement(); // Ensure there is at least one element in queue
		
		analyzeToken(laElem.token);
		lastNonMissingLexElement = lastLexElement = laElem;
		lookAheadQueue.removeFirst();
		return laElem;
	}
	
	protected final Token consumeLookAhead() {
		return consumeInput().token;
	}
	
	protected final Token consumeLookAhead(DeeTokens tokenType) {
		assertTrue(lookAhead() == tokenType);
		return consumeLookAhead();
	}
	
	public LexElement consumeIgnoredTokens() {
		return consumeIgnoredTokens(null, false);
	}
	
	public LexElement consumeIgnoredTokens(DeeTokens expectedToken, boolean createMissingElement) {
		LexElement la = lookAheadElement();
		
		if(createMissingElement) {
			// Missing element will consume whitetokens ahead
			int lookAheadStart = lookAheadElement().getStartPos();
			lastLexElement = new LexElement(la.ignoredPrecedingTokens, expectedToken, lookAheadStart);
		}
		lookAheadQueue.set(0, new LexElement(null, la.token));
		
		return lastLexElement;
	}
	
	/* ---- Basic error functionality ---- */
	
	protected ParserError addError(ParserErrorTypes errorType, SourceRange sr, String errorSource, Object msgData) {
		assertEquals(getSource(sr), errorSource);
		ParserError error = new ParserError(errorType, sr, errorSource, msgData);
		errors.add(error);
		return error;
	}
	
	protected ParserError addError(ParserErrorTypes errorType, Token errorToken, Object msgData) {
		return addError(errorType, sr(errorToken), errorToken.source, msgData);
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
	
	protected final LexElement consumeExpectedToken(DeeTokens expectedTokenType) {
		return consumeExpectedToken(expectedTokenType, false);
	}
	
	protected final LexElement consumeExpectedToken(DeeTokens expectedTokenType, boolean createMissingToken) {
		if(lookAhead() == expectedTokenType) {
			return consumeInput();
		} else {
			reportErrorExpectedToken(expectedTokenType);
			return createMissingToken ? consumeIgnoredTokens(expectedTokenType, true) : null;
		}
	}
	
	protected LexElement tryConsumeIdentifier() {
		return consumeExpectedToken(DeeTokens.IDENTIFIER, true);
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
		ParserError error = addError(parserError, lastNonMissingLexElement.token, msgData);
		if(missingToken) {
			pendingMissingTokenErrors.add(error);
		}
	}
	protected <T extends ASTNeoNode> T connect(T node) {
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