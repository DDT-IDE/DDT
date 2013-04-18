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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.ASTDirectChildrenVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTSemantics;
import dtool.ast.IASTNeoNode;
import dtool.ast.NodeUtil;
import dtool.ast.SourceRange;
import dtool.parser.LexElement.MissingLexElement;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.util.ArrayView;

/**
 * Basic parsing functionality.
 */
public abstract class AbstractParser {
	
	protected abstract DeeParser thisParser();
	
	/* ---- Core functionality ---- */
	
	public abstract String getSource();
	
	public abstract LexElement lookAheadElement(int laIndex);
	
	public abstract int getLexPosition();
	
	public abstract LexElement lastLexElement();
	
	public abstract LexElement consumeInput();
	
	public abstract MissingLexElement consumeSubChannelTokens();
	
	public abstract void setEnabled(boolean enabled);
	
	public abstract boolean isEnabled();
	
	/* ---- core error functionality ---- */
	
	protected ArrayList<ParserError> pendingMissingTokenErrors;
	
	protected abstract void submitError(ParserError error);
	
	protected ParserError addError(ParserError error) {
		submitError(error);
		return error;
	}
	
	/* ---- Lookahead and consume helpers ---- */
	
	public final LexElement lookAheadElement() {
		return lookAheadElement(0);
	}
	
	public final Token lookAheadToken() {
		return lookAheadElement(0).token;
	}
	
	public final DeeTokens lookAhead() {
		return lookAheadElement(0).token.getTokenType();
	}
	
	public final DeeTokens lookAhead(int laIndex) {
		return lookAheadElement(laIndex).token.getTokenType();
	}
	
	public final Token consumeLookAhead() {
		return consumeInput().token;
	}
	
	public final LexElement consumeLookAhead(DeeTokens tokenType) {
		assertTrue(lookAhead() == tokenType);
		return consumeInput();
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
	
	protected final boolean attemptConsume(DeeTokens tokenType, boolean isExpected) {
		boolean consumed = tryConsume(tokenType);
		if(!consumed && isExpected) {
			addExpectedTokenError(tokenType);
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
	
	/* ----  ---- */
	
	public static class ParseRuleDescription {
		public final String name;
		
		public ParseRuleDescription(String name) {
			this.name = name;
		}
	}
	
	/* ---- error helpers ---- */
	
	protected ParserError createError(ParserErrorTypes errorType, SourceRange sr, Object msgData) {
		String errorSource = NodeUtil.getSubString(getSource(), sr); 
		return new ParserError(errorType, sr, errorSource, msgData);
	}
	
	protected ParserError createErrorOnLastToken(ParserErrorTypes parserError, Object msgData) {
		return createError(parserError, lastLexElement().getSourceRange(), msgData);
	}
	
	protected ParserError addError(ParserErrorTypes errorType, Token errorToken, Object msgData) {
		return addError(createError(errorType, errorToken.getSourceRange(), msgData));
	}
	
	protected ParserError addError(ParserErrorTypes errorType, SourceRange sourceRange, Object msgData) {
		return addError(createError(errorType, sourceRange, msgData));
	}
	
	protected ParserError addExpectedTokenError(DeeTokens expected) {
		return addErrorWithMissingtoken(ParserErrorTypes.EXPECTED_TOKEN, expected, true);
	}
	
	protected ParserError addErrorWithMissingtoken(ParserErrorTypes errorType, Object msgData, boolean missingToken) {
		ParserError error = addError(createErrorOnLastToken(errorType, msgData));
		if(missingToken) {
			pendingMissingTokenErrors.add(error);
		}
		return error;
	}
	
	protected void reportErrorExpectedRule(ParseRuleDescription expectedRule) {
		addError(createErrorOnLastToken(ParserErrorTypes.EXPECTED_RULE, expectedRule.name));
	}
	
	protected void reportSyntaxError(ParseRuleDescription expectedRule) {
		addError(createErrorOnLastToken(ParserErrorTypes.SYNTAX_ERROR, expectedRule.name));
	}
	
	/* ---- Additional input consume helpers ---- */
	
	protected final LexElement consumeExpectedToken(DeeTokens expectedTokenType) {
		if(lookAhead() == expectedTokenType) {
			return consumeInput(); 
		}
		addExpectedTokenError(expectedTokenType);
		return null;
	}
	
	protected final BaseLexElement consumeExpectedToken(DeeTokens expectedTokenType, boolean createMissingToken) {
		BaseLexElement result = consumeExpectedToken(expectedTokenType);
		if(result == null && createMissingToken) {
			return consumeSubChannelTokens();
		}
		return result;
	}
	
	protected final BaseLexElement consumeExpectedIdentifier() {
		return consumeExpectedToken(DeeTokens.IDENTIFIER, true);
	}
	
	protected final MissingLexElement createExpectedToken(DeeTokens expectedTokenType) {
		assertTrue(lookAhead() != expectedTokenType);
		return consumeSubChannelTokens();
	}
	
	/* ---- Result helpers ---- */
	
	// TODO: alternative mechanism for broken rule checking
	public static abstract class CommonRuleResult {
		
		public final boolean ruleBroken; // Indicates if rule was terminated properly
		
		public CommonRuleResult(boolean ruleBroken) {
			this.ruleBroken = ruleBroken;
		}
		
	}
	
	public static class RuleResult<T> extends CommonRuleResult {
		public final T result;
		
		public RuleResult(boolean parseBroken, T result) {
			super(parseBroken);
			this.result = result;
		}
		
	}
	
	public static class NodeListParseResult<T> extends RuleResult<ArrayList<T>> {
		public NodeListParseResult(boolean properlyTerminated, ArrayList<T> argList) {
			super(!properlyTerminated, argList);
		}
	}
	
	public static class NodeResult<T extends ASTNeoNode> extends CommonRuleResult {
		
		protected final T node;
		
		public NodeResult(boolean ruleBroken, T result) {
			super(ruleBroken);
			this.node = result;
			assertTrue(!(ruleBroken && result == null));
		}
		
		@SuppressWarnings("unchecked")
		protected final <SUPER_OF_T extends ASTNeoNode> NodeResult<SUPER_OF_T> upcastParam() {
			return (NodeResult<SUPER_OF_T>) this;
		}
		
		public final T getNode() {
			return node;
		}
		
		public final T getNode_NoBrokenCheck() {
			return node;
		}
		
	}
	
	protected static <T extends ASTNeoNode> NodeResult<T> nullResult() {
		return new NodeResult<T>(false, null);
	}
	
	protected static <T extends ASTNeoNode> NodeResult<T> nodeResult(T node) {
		return new NodeResult<T>(false, node);
	}
	protected static <T extends ASTNeoNode> NodeResult<T> nodeResult(boolean ruleBroken, T node) {
		return new NodeResult<T>(ruleBroken, node);
	}
	
	protected static <T> RuleResult<T> ruleResult(boolean ruleBroken, T resultElem) {
		return new RuleResult<T>(ruleBroken, resultElem);
	}
	
	protected static <T extends ASTNeoNode> T getResult(NodeResult<T> nodeResult) {
		return nodeResult == null ? null : nodeResult.node;
	}
	
	/* ------------  Node finalization  ------------ */
	
	protected final <T extends ASTNeoNode> NodeResult<T> connectResult(boolean ruleBroken, T node) {
		return nodeResult(ruleBroken, connect(node));
	}
	
	protected <T extends ASTNeoNode> T connect(final T node) {
		if(pendingMissingTokenErrors != null) {
			for (ParserError parserError : pendingMissingTokenErrors) {
				if(parserError.msgData != DeeTokens.IDENTIFIER) {
					parserError.originNode = node;
				}
			}
			pendingMissingTokenErrors.clear();
		}
		
		node.setData(ASTSemantics.PARSED_STATUS);
		node.accept(new ASTDirectChildrenVisitor() {
			@Override
			protected void geneticChildrenVisit(ASTNeoNode child) {
				assertTrue(child.getParent() == node);
				assertTrue(child.isParsedStatus());
			}
		});
		return node;
	}
	
	protected <T extends ASTNeoNode> T connect(SourceRange sourceRange, T node) {
		node.setSourceRange(sourceRange);
		return connect(node);
	}
	
	protected final <T extends IASTNeoNode> T connect(T node) {
		connect((ASTNeoNode) node);
		return node;
	}
	
	/* ---- Source range helpers ---- */
	
	public static SourceRange srAt(int offset) {
		return new SourceRange(offset, 0);
	}
	
	public static SourceRange srNodeStart(ASTNeoNode startNode, int endPos) {
		return SourceRange.srStartToEnd(startNode.getStartPos(), endPos);
	}
	
	public static <T extends ASTNeoNode> T sr(BaseLexElement lexElement, T node) {
		node.setSourceRange(lexElement.getStartPos(), lexElement.getEndPos() - lexElement.getStartPos());
		return node;
	}
	
	public static <T extends ASTNeoNode> T srEffective(BaseLexElement lexElement, T node) {
		int startPos = lexElement.isMissingElement() ? lexElement.getFullRangeStartPos() : lexElement.getStartPos();
		node.setSourceRange(startPos, lexElement.getEndPos() - startPos);
		return node;
	}
	
	public final <T extends ASTNeoNode> T srToPosition(int declStart, T node) {
		node.setSourceRange(declStart, getLexPosition() - declStart);
		return node;
	}
	
	public final <T extends ASTNeoNode> T srToPosition(BaseLexElement start, T node) {
		int declStart = start.getStartPos();
		node.setSourceRange(declStart, getLexPosition() - declStart);
		return node;
	}
	
	public final <T extends ASTNeoNode> T srToPosition(ASTNeoNode left, T node) {
		assertTrue(left.hasSourceRangeInfo() && !node.hasSourceRangeInfo());
		assertTrue(!node.isParsedStatus());
		return srToPosition(left.getStartPos(), node);
	}
	
	public static <T extends ASTNeoNode> T srBounds(ASTNeoNode left, ASTNeoNode right, T node) {
		int startPos = left.getStartPos();
		node.setSourceRange(startPos, right.getEndPos() - startPos);
		return node;
	}
	
	public static <T extends ASTNeoNode> T srBounds(ASTNeoNode wrappedNode, T node) {
		return srBounds(wrappedNode, wrappedNode, node);
	}
	
	/* ---- Collection creation helpers ---- */
	
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