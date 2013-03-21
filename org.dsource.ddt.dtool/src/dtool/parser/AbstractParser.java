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
import dtool.ast.ASTChildrenVisitor;
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
public class AbstractParser extends CommonLexElementSource {
	
	protected final LexerElementSource lexSource;
	
	protected final ArrayList<ParserError> errors = new ArrayList<ParserError>();
	protected final ArrayList<ParserError> pendingMissingTokenErrors = new ArrayList<ParserError>();
	
	public AbstractParser(LexerElementSource lexSource) {
		this.lexSource = lexSource;
	}
	
	public String getSource() {
		return lexSource.getSource();
	}
	
	@Override
	public LexElement lookAheadElement(int laIndex) {
		return lexSource.lookAheadElement(laIndex);
	}
	
	protected LexElement lastLexElement() {
		return lexSource.lastLexElement;
	}
	
	protected LexElement lastNonMissingLexElement() {
		return lexSource.lastNonMissingLexElement;
	}
	
	// Consume methods of lexSource should not be called directly because we need to make sure
	// analyzeConsumedElement is called
	
	@Override
	protected final LexElement consumeInput() {
		LexElement consumedElement = lexSource.consumeInput();
		analyzeIgnoredTokens(consumedElement);
		DeeTokenSemantics.checkTokenErrors(consumedElement.token, this);
		return consumedElement;
	}
	
	@Override
	public LexElement consumeIgnoreTokens(DeeTokens expectedToken) {
		LexElement consumedElement = lexSource.consumeIgnoreTokens(expectedToken);
		analyzeIgnoredTokens(consumedElement);
		return consumedElement;
	}
	
	protected void analyzeIgnoredTokens(LexElement lastLexElement) {
		if(lastLexElement.ignoredPrecedingTokens != null) {
			for (Token ignoredToken : lastLexElement.ignoredPrecedingTokens) {
				DeeTokenSemantics.checkTokenErrors(ignoredToken, this);
			}
		}
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
	
	public String getSource(SourceRange sourceRange) {
		return getSource().subSequence(sourceRange.getStartPos(), sourceRange.getEndPos()).toString();
	}
	
	/* ---- Input consume helpers ---- */
	
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
	
	protected final boolean attemptConsume(DeeTokens tokenType, boolean isExpected) {
		boolean consumed = tryConsume(tokenType);
		if(!consumed && isExpected) {
			reportErrorExpectedToken(tokenType);
		}
		return consumed;
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
		return consumeIgnoreTokens(expectedTokenType);
	}
	
	protected final LexElement consumeExpectedToken(DeeTokens expectedTokenType, boolean createMissingToken) {
		if(lookAhead() == expectedTokenType) {
			return consumeInput();
		} else {
			reportErrorExpectedToken(expectedTokenType);
			return createMissingToken ? consumeIgnoreTokens(expectedTokenType) : null;
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
		ParserError error = addError(parserError, lastNonMissingLexElement().token, msgData);
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
		pendingMissingTokenErrors.clear();
		
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