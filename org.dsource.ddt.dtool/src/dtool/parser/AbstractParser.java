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

import java.util.Collection;

import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoNode;
import dtool.ast.NodeUtil;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefUnit.ProtoDefSymbol;
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
	
	protected final LexElement consumeIf(DeeTokens tokenType) {
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
	
	protected ParserError createError(ParserErrorTypes errorType, Token errorToken, Object msgData) {
		return createError(errorType, errorToken.getSourceRange(), msgData);
	}
	
	protected ParserError createErrorOnLastToken(ParserErrorTypes parserError, Object msgData) {
		return createError(parserError, lastLexElement().getSourceRange(), msgData);
	}
	
	protected ParserError createExpectedTokenError(DeeTokens expected) {
		return createErrorOnLastToken(ParserErrorTypes.EXPECTED_TOKEN, expected);
	}
	
	protected ParserError createErrorExpectedRule(ParseRuleDescription expectedRule) {
		return createErrorOnLastToken(ParserErrorTypes.EXPECTED_RULE, expectedRule.name);
	}
	
	protected ParserError createSyntaxError(ParseRuleDescription expectedRule) {
		return createErrorOnLastToken(ParserErrorTypes.SYNTAX_ERROR, expectedRule.name);
	}
	
	/* ---- Result helpers ---- */
	
	// TODO: alternative mechanism for broken rule checking
	public static abstract class CommonRuleResult {
		
		public final boolean ruleBroken; // Indicates if rule was terminated properly
		
		public CommonRuleResult(boolean ruleBroken) {
			this.ruleBroken = ruleBroken;
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
		protected final <SUPER_OF_T extends ASTNeoNode> NodeResult<SUPER_OF_T> upcastTypeParam() {
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
	protected static <T extends ASTNeoNode> NodeResult<T> result(boolean ruleBroken, T node) {
		return new NodeResult<T>(ruleBroken, node);
	}
	
	/* ---- Additional input consume helpers ---- */
	
	protected final BaseLexElement consumeExpectedContentToken(DeeTokens expectedTokenType) {
		if(lookAhead() == expectedTokenType) {
			return consumeInput();
		} else {
			ParserError error = createExpectedTokenError(expectedTokenType);
			MissingLexElement missingToken = consumeSubChannelTokens();
			missingToken.error = error;
			return missingToken;
		}
	}
	
	protected final MissingLexElement createExpectedToken(DeeTokens expectedTokenType) {
		assertTrue(lookAhead() != expectedTokenType);
		return consumeSubChannelTokens();
	}
	
	/* ------------  Node finalization  ------------ */
	
	protected final <T extends ASTNeoNode> NodeResult<T> resultConclude(boolean ruleBroken, T node) {
		return result(ruleBroken, conclude(node));
	}
	
	protected final <T extends ASTNeoNode> T conclude(SourceRange sr, T node) {
		node.setSourceRange(sr);
		return concludeDo(null, null, node);
	}
	
	protected final <T extends ASTNeoNode> T concludeNode(T node) {
		return concludeDo(null, null, node);
	}
	protected final <T extends ASTNeoNode> T conclude(T node) {
		return concludeDo(null, null, node);
	}
	protected final <T extends ASTNeoNode> T conclude(ParserError error, final T node) {
		return concludeDo(error, null, node);
	}
	protected final <T extends ASTNeoNode> T concludeDo(ParserError error1, ParserError error2, final T node) {
		if(error1 == null) {
			assertTrue(error2 == null);
			node.setParsedStatus();
		} else if(error2 == null) {
			node.setParsedStatusWithErrors(error1);
		} else {
			node.setParsedStatusWithErrors(error1, error2);
		}
		nodeConcluded(node);
		return node;
	}
	
	@SuppressWarnings("unused")
	protected void nodeConcluded(final ASTNeoNode node) {
	}
	
	/** Temporary node parsing helper class. Designed to be used once per node about to parsed. 
	 * Also intended to have its allocation elided by means of escape analysis optimization,
	 * therefore (in most circumstances) instances of this class should only be assigned to local variables.
	 */ 
	public class ParseHelper {
		
		protected int nodeStart;
		protected ParserError error1 = null;
		protected ParserError error2 = null;
		protected boolean ruleBroken = false;
		
		public ParseHelper(int nodeStart) {
			this.nodeStart = nodeStart;
		}
		
		public ParseHelper(LexElement token) {
			this(token.getStartPos());
		}
		
		public ParseHelper() {
			this(lastLexElement());
		}
		
		public ParseHelper(ASTNeoNode startNode) {
			this(startNode.getStartPos());
		}
		
		public void setStartPosition(int nodeStart) {
			assertTrue(this.nodeStart == -1);
			this.nodeStart = nodeStart;
		}
		
		public <T extends ASTNeoNode> T initRange(T node) {
			assertTrue(node.hasNoSourceRangeInfo());
			return srToPosition(nodeStart, node);
		}
		
		public final boolean consumeRequired(DeeTokens expectedTokenType) {
			return consume(expectedTokenType, false, true);
		}
		
		public final boolean consumeExpected(DeeTokens expectedTokenType) {
			return consume(expectedTokenType, false, false);
		}
		
		public final boolean consume(DeeTokens expectedTokenType, boolean isOptional, boolean breaksRule) {
			if(lookAhead() == expectedTokenType) {
				consumeInput();
				return true;
			}
			if(isOptional == false) {
				store(createExpectedTokenError(expectedTokenType));
				ruleBroken = breaksRule;
			}
			return false;
		}
		
		public BaseLexElement consumeExpectedIdentifier() {
			BaseLexElement token = consumeExpectedContentToken(DeeTokens.IDENTIFIER);
			if(token.getError() != null) {
				store(token.getError());
			}
			return token;
		}
		
		public ProtoDefSymbol checkResult(ProtoDefSymbol defId) {
			ruleBroken = defId.isMissing();
			return defId;
		}
		
		public <T extends ASTNeoNode> T checkResult(NodeResult<T> nodeResult) {
			ruleBroken = nodeResult.ruleBroken;
			return nodeResult.node;
		}
		
		public <T extends ASTNeoNode> T requiredResult(NodeResult<T> nodeResult, ParseRuleDescription expectedRule) {
			if(nodeResult.node == null) {
				storeBreakError(createErrorExpectedRule(expectedRule));
				return null;
			}
			ruleBroken = nodeResult.ruleBroken;
			return nodeResult.node;
		}
		
		protected final ParserError storeBreakError(ParserError error) {
			ruleBroken = error != null;
			return store(error);
		}
		
		protected final ParserError store(ParserError error) {
			assertTrue(error2 == null);
			if(error1 == null) {
				error1 = error;
			} else {
				error2 = error;
			}
			return error;
		}
		
		public <T extends ASTNeoNode> T conclude(T node) {
			initRange(node);
			nodeStart = -1;
			return concludeDo(error1, error2, node);
		}
		
		public final <T extends ASTNeoNode> NodeResult<T> resultConclude(T node) {
			return result(ruleBroken, conclude(node));
		}

	}
	
	public class SingleTokenParse {
		protected BaseLexElement lexToken;
		protected ParserError error = null;
		
		public SingleTokenParse(DeeTokens expectedToken) {
			lexToken = consumeExpectedContentToken(expectedToken);
			error = lexToken.getError();
		}
		
		public <T extends ASTNeoNode> T conclude(T node) {
			srBounds(lexToken.getStartPos(), lexToken.getEndPos(), node);
			return concludeDo(error, null, node);
		}
	}
	
	
	/* ---- Source range helpers ---- */
	
	public static SourceRange srAt(int offset) {
		return new SourceRange(offset, 0);
	}
	
	public static <T extends ASTNeoNode> T srBounds(int startPos, int endPos, T node) {
		node.setSourceRange(startPos, endPos - startPos);
		return node;
	}
	
	public static <T extends ASTNeoNode> T srBounds(ASTNeoNode left, ASTNeoNode right, T node) {
		int startPos = left.getStartPos();
		int endPos = right.getEndPos();
		return srBounds(startPos, endPos, node);
	}
	
	public static <T extends ASTNeoNode> T srBounds(ASTNeoNode wrappedNode, T node) {
		return srBounds(wrappedNode, wrappedNode, node);
	}
	
	public static <T extends ASTNeoNode> T srOf(BaseLexElement lexElement, T node) {
		node.setSourceRange(lexElement.getStartPos(), lexElement.getEndPos() - lexElement.getStartPos());
		return node;
	}
	
	public static <T extends ASTNeoNode> T srEffective(BaseLexElement lexElement, T node) {
		int startPos = lexElement.isMissingElement() ? lexElement.getFullRangeStartPos() : lexElement.getStartPos();
		node.setSourceRange(startPos, lexElement.getEndPos() - startPos);
		return node;
	}
	
	public final <T extends ASTNeoNode> T srToPosition(int nodeStart, T node) {
		node.setSourceRange(nodeStart, getLexPosition() - nodeStart);
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
	
	/* ---- Collection creation helpers ---- */
	
	// TODO: optimize some of this arrayView creation
	
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