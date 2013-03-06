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
import dtool.ast.ASTChildrenVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoNode;
import dtool.ast.SourceRange;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.util.ArrayView;
import dtool.util.NewUtils;

/**
 * Basic parser functionality.
 * Maintains a queue of lookahead elements from the parser.
 * Holds an error list data; 
 */
public class AbstractParser {
	
	protected final AbstractLexer lexer;
	
	protected final ArrayListDeque<LexElement> lookAheadQueue = new ArrayListDeque<AbstractParser.LexElement>();
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
			return new LexElement(NewUtils.toArray(ignoredTokens, Token.class), token);
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
	
	public final DeeTokens lookAhead(int laIndex) {
		return lookAheadElement(laIndex).token.getRawTokenType();
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
	
	public LexElement consumeIgnoreTokens() {
		return consumeIgnoreTokens(null, false);
	}
	
	public LexElement consumeIgnoreTokens(DeeTokens expectedToken, boolean createMissingElement) {
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
		SourceRange sourceRange = errorToken.getSourceRange();
		assertEquals(errorToken.source, getSource(sourceRange));
		return addError(errorType, sourceRange, msgData);
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
		return lookAhead() == tokenType ? consumeLookAhead() : null;
	}
	
	protected final LexElement consumeElementIf(DeeTokens tokenType) {
		return lookAhead() == tokenType ? consumeInput() : null;
	}
	
	protected final boolean tryConsume(DeeTokens tokenType) {
		if(lookAhead() == tokenType) {
			consumeInput();
			return true;
		}
		return false;
	}
	
	protected final boolean tryConsume(DeeTokens tokenType, DeeTokens tokenType2) {
		if(lookAhead() == tokenType && lookAhead(1) == tokenType2) {
			consumeInput();
			consumeInput();
			return true;
		}
		return false;
	}
	
	protected final LexElement consumeExpectedToken(DeeTokens expectedTokenType) {
		return consumeExpectedToken(expectedTokenType, false);
	}
	
	protected final LexElement createExpectedToken(DeeTokens expectedTokenType) {
		assertTrue(lookAhead() != expectedTokenType);
		return consumeIgnoreTokens(expectedTokenType, true);
	}
	
	protected final LexElement consumeExpectedToken(DeeTokens expectedTokenType, boolean createMissingToken) {
		if(lookAhead() == expectedTokenType) {
			return consumeInput();
		} else {
			reportErrorExpectedToken(expectedTokenType);
			return createMissingToken ? consumeIgnoreTokens(expectedTokenType, true) : null;
		}
	}
	
	protected LexElement consumeExpectedIdentifier() {
		return consumeExpectedToken(DeeTokens.IDENTIFIER, true);
	}
	
	/* ---- error helpers ---- */
	
	protected void reportErrorExpectedToken(DeeTokens expected) {
		reportError(ParserErrorTypes.EXPECTED_TOKEN, expected, true);
	}
	
	public static class ParseRuleDescription {
		public final String name;
		
		public ParseRuleDescription(String name) {
			this.name = name;
		}
	}
	
	protected void reportErrorExpectedRule(ParseRuleDescription expectedRule) {
		reportError(ParserErrorTypes.EXPECTED_RULE, expectedRule.name, false);
	}
	
	protected void reportSyntaxError(ParseRuleDescription expectedRule) {
		reportError(ParserErrorTypes.SYNTAX_ERROR, expectedRule.name, false);
	}
	
	protected void reportError(ParserErrorTypes parserError, Object msgData, boolean missingToken) {
		ParserError error = addError(parserError, lastNonMissingLexElement.token, msgData);
		if(missingToken) {
			pendingMissingTokenErrors.add(error);
		}
	}
	
	public static Object PARSED_STATUS = new String("NODE_STATUS:PARSED");
	
	protected <T extends ASTNeoNode> T connect(final T node) {
		for (ParserError parserError : pendingMissingTokenErrors) {
			if(parserError.msgData != DeeTokens.IDENTIFIER) {
				parserError.originNode = node;
			}
		}
		pendingMissingTokenErrors = new ArrayList<ParserError>();
		
		node.setData(PARSED_STATUS);
		node.accept(new ASTChildrenVisitor() {
			@Override
			protected void geneticChildrenVisit(ASTNeoNode child) {
				assertTrue(child.getParent() == node);
				assertTrue(child.getData() == PARSED_STATUS);
			}
		});
		return node;
	}
	
	protected final <T extends IASTNeoNode> T connect(T node) {
		connect((ASTNeoNode) node);
		return node;
	}
	
	/* ---- Node creation helpers ---- */
	
	public static SourceRange sr(Token token) {
		return token.getSourceRange();
	}
	
	public static SourceRange srNodeStart(ASTNeoNode startNode, int endPos) {
		return SourceRange.srStartToEnd(startNode.getStartPos(), endPos);
	}
	
	public final SourceRange srToCursor(int declStart) {
		return SourceRange.srStartToEnd(declStart, getParserPosition());
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
	
	public static <T extends IASTNeoNode> ArrayView<T> arrayViewI(Collection<? extends T> list) {
		if(list == null)
			return null;
		return ArrayView.create((T[]) ArrayUtil.createFrom(list, IASTNeoNode.class));
	}
	
	public static <T extends ASTNeoNode> ArrayView<T> arrayView(Collection<? extends T> list) {
		if(list == null)
			return null;
		return ArrayView.create((T[]) ArrayUtil.createFrom(list, ASTNeoNode.class));
	}
	
	public static <T> ArrayView<T> arrayViewG(Collection<? extends T> list) {
		if(list == null)
			return null;
		return ArrayView.create((T[]) ArrayUtil.createFrom(list, Object.class));
	}
	
}