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
import java.util.HashMap;

import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.ASTNode;
import dtool.ast.IASTNode;
import dtool.ast.NodeListView;
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
	
	protected String source;
	protected LexElementSource lexSource;
	protected boolean enabled;
	
	public AbstractParser() {
		this.enabled = true;
	}
	
	/* ---- Core functionality ---- */

	public final String getSource() {
		return source;
	}
	
	public final LexElementSource getEnabledLexSource() {
		assertTrue(enabled);
		return lexSource;
	}
	
	protected final LexElementSource getLexSource() {
		return lexSource;
	}
	
	public final void setEnabled(boolean enabled) {
		assertTrue(this.enabled == !enabled);
		this.enabled = enabled;
	}
	
	// There should be no reason to use this other than for contract checks only
	public final boolean isEnabled() {
		return enabled;
	}
	
	public final int getSourcePosition() {
		return getLexSource().getSourcePosition();
	}
	
	public final LexElement lookAheadElement(int laIndex) {
		return getEnabledLexSource().lookAheadElement(laIndex);
	}
	
	public final LexElement lastLexElement() {
		return getLexSource().lastLexElement();
	}
	
	public final LexElement consumeLookAhead() {
		return getEnabledLexSource().consumeInput();
	}
	
	public final MissingLexElement consumeSubChannelTokens() {
		return getEnabledLexSource().consumeSubChannelTokens();
	}
	
	public ParserState saveParserState() {
		LexElementSource lexSource = getEnabledLexSource().saveState();
		return new ParserState(lexSource, enabled);
	}
	
	public void restoreOriginalState(ParserState savedState) {
		this.lexSource.resetState(savedState.lexSource);
		this.enabled = savedState.enabled;
	}
	
	public class ParserState {
		
		protected final LexElementSource lexSource;
		protected final boolean enabled;
		
		public ParserState(LexElementSource lexSource, boolean enabled) {
			this.lexSource = lexSource;
			this.enabled = enabled;
		}
		
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
	
	public final LexElement consumeLookAhead(DeeTokens tokenType) {
		assertTrue(lookAhead() == tokenType);
		return consumeLookAhead();
	}
	
	protected final LexElement consumeIf(DeeTokens tokenType) {
		return lookAhead() == tokenType ? consumeLookAhead() : null;
	}
	
	protected final boolean tryConsume(DeeTokens tokenType) {
		if(lookAhead() == tokenType) {
			consumeLookAhead();
			return true;
		}
		return false;
	}
	protected final boolean tryConsume(DeeTokens tokenType, DeeTokens tokenType2) {
		if(lookAhead() == tokenType && lookAhead(1) == tokenType2) {
			consumeLookAhead();
			consumeLookAhead();
			return true;
		}
		return false;
	}
	protected final boolean tryConsume(DeeTokens tokenType, DeeTokens tokenType2, DeeTokens tokenType3) {
		if(lookAhead() == tokenType && lookAhead(1) == tokenType2 && lookAhead(2) == tokenType3) {
			consumeLookAhead();
			consumeLookAhead();
			consumeLookAhead();
			return true;
		}
		return false;
	}
	
	/* ----  ---- */
	
	protected static final HashMap<String, ParseRuleDescription> parseRules = new HashMap<>(); 
	
	// TODO: maybe this should be an enum
	public static class ParseRuleDescription {
		public final String id;
		public final String description;
		
		public ParseRuleDescription(String id, String description) {
			this.id = id;
			this.description = description;
			assertTrue(parseRules.get(id) == null);
			parseRules.put(id, this);
		}
	}
	
	public static ParseRuleDescription getRule(String key) {
		return parseRules.get(key);
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
		return createErrorOnLastToken(ParserErrorTypes.EXPECTED_RULE, expectedRule.description);
	}
	
	protected ParserError createSyntaxError(ParseRuleDescription expectedRule) {
		return createErrorOnLastToken(ParserErrorTypes.SYNTAX_ERROR, expectedRule.description);
	}
	
	/* ---- Result helpers ---- */
	
	public static abstract class CommonRuleResult {
		
		public final boolean ruleBroken; // Indicates if rule was terminated properly
		
		public CommonRuleResult(boolean ruleBroken) {
			this.ruleBroken = ruleBroken;
		}
		
	}
	
	public static class NodeResult<T extends ASTNode> extends CommonRuleResult {
		
		protected final T node;
		
		public NodeResult(boolean ruleBroken, T result) {
			super(ruleBroken);
			this.node = result;
			assertTrue(!(ruleBroken && result == null));
		}
		
		@SuppressWarnings("unchecked")
		protected final <SUPER_OF_T extends ASTNode> NodeResult<SUPER_OF_T> upcastTypeParam() {
			return (NodeResult<SUPER_OF_T>) this;
		}
		
	}
	
	public static <T extends ASTNode> NodeResult<T> nullResult() {
		return new NodeResult<T>(false, null);
	}
	public static <T extends ASTNode> NodeResult<T> result(boolean ruleBroken, T node) {
		return new NodeResult<T>(ruleBroken, node);
	}
	
	public static boolean isNull(NodeResult<?> result) {
		return result == null || result.node == null;
	}
	
	/* ---- Additional input consume helpers ---- */
	
	protected final BaseLexElement consumeExpectedContentToken(DeeTokens expectedTokenType) {
		if(lookAhead() == expectedTokenType) {
			return consumeLookAhead();
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
	
	protected final <T extends ASTNode> NodeResult<T> resultConclude(boolean ruleBroken, T node) {
		return result(ruleBroken, conclude(node));
	}
	
	protected final <T extends ASTNode> T conclude(SourceRange sr, T node) {
		node.setSourceRange(sr);
		return concludeDo(null, null, node);
	}
	
	protected final <T extends ASTNode> T concludeNode(T node) {
		return concludeDo(null, null, node);
	}
	protected final <T extends ASTNode> T conclude(T node) {
		return concludeDo(null, null, node);
	}
	protected final <T extends ASTNode> T conclude(ParserError error, final T node) {
		return concludeDo(error, null, node);
	}
	protected final <T extends ASTNode> T concludeDo(ParserError error1, ParserError error2, final T node) {
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
	protected void nodeConcluded(final ASTNode node) {
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
		
		public ParseHelper(ASTNode startNode) {
			this(startNode.getStartPos());
		}
		
		public void setStartPosition(int nodeStart) {
			assertTrue(this.nodeStart == -1);
			this.nodeStart = nodeStart;
		}
		
		public <T extends ASTNode> T initRange(T node) {
			assertTrue(!node.hasSourceRangeInfo());
			return srToPosition(nodeStart, node);
		}
		
		public final ParseHelper consumeRequired(DeeTokens expectedTokenType) {
			consume(expectedTokenType, false, true);
			return this;
		}
		
		public final boolean consumeExpected(DeeTokens expectedTokenType) {
			return consume(expectedTokenType, false, false);
		}
		
		public final boolean consumeOptional(DeeTokens expectedTokenType) {
			return consume(expectedTokenType, true, false);
		}
		
		public final boolean consume(DeeTokens expectedTokenType, boolean isOptional, boolean breaksRule) {
			assertTrue(!ruleBroken);
			if(lookAhead() == expectedTokenType) {
				consumeLookAhead();
				return true;
			}
			if(isOptional == false) {
				storeError(createExpectedTokenError(expectedTokenType));
				setRuleBroken(breaksRule);
			}
			return false;
		}
		
		public BaseLexElement consumeExpectedIdentifier() {
			BaseLexElement token = consumeExpectedContentToken(DeeTokens.IDENTIFIER);
			if(token.getError() != null) {
				storeError(token.getError());
			}
			return token;
		}
		
		public final ProtoDefSymbol checkResult(ProtoDefSymbol defId) {
			setRuleBroken(defId.isMissing());
			return defId;
		}
		
		public final <T extends ASTNode> T checkResult(NodeResult<T> nodeResult) {
			if(nodeResult == null) 
				return null;
			setRuleBroken(nodeResult.ruleBroken);
			return nodeResult.node;
		}
		
		public void setRuleBroken(boolean ruleBroken) {
			this.ruleBroken = ruleBroken;
		}
		
		public final ParseHelper clearRuleBroken() {
			if(ruleBroken) {
				setRuleBroken(false);
			}
			return this;
		}
		
		/** Disable parsing until ruleBroken is explicitly checked. (safety feature) */
		public void requireBrokenCheck() {
			setEnabled(false);
		}
		
		public boolean checkRuleBroken() {
			setEnabled(true);
			return ruleBroken;
		}
		
		public <T extends ASTNode> T requiredResult(NodeResult<T> nodeResult, ParseRuleDescription expectedRule) {
			if(nodeResult.node == null) {
				storeBreakError(createErrorExpectedRule(expectedRule));
				return null;
			}
			setRuleBroken(nodeResult.ruleBroken);
			return nodeResult.node;
		}
		
		protected final ParserError storeBreakError(ParserError error) {
			setRuleBroken(error != null);
			return storeError(error);
		}
		
		protected final ParserError storeError(ParserError error) {
			assertTrue(error2 == null);
			if(error1 == null) {
				error1 = error;
			} else {
				error2 = error;
			}
			return error;
		}
		
		public final <T extends ASTNode> T conclude(T node) {
			initRange(node);
			nodeStart = -nodeStart; // invalidate
			return concludeDo(error1, error2, node);
		}
		
		public final <T extends ASTNode> NodeResult<T> resultConclude(T node) {
			return result(ruleBroken, conclude(node));
		}
		
	}
	
	/* ---- Source range helpers ---- */
	
	public static SourceRange srAt(int offset) {
		return new SourceRange(offset, 0);
	}
	
	public static <T extends ASTNode> T srBounds(int startPos, int endPos, T node) {
		node.setSourceRange(startPos, endPos - startPos);
		return node;
	}
	
	public static <T extends ASTNode> T srBounds(ASTNode left, ASTNode right, T node) {
		int startPos = left.getStartPos();
		int endPos = right.getEndPos();
		return srBounds(startPos, endPos, node);
	}
	
	public static <T extends ASTNode> T srOf(BaseLexElement lexElement, T node) {
		node.setSourceRange(lexElement.getStartPos(), lexElement.getEndPos() - lexElement.getStartPos());
		return node;
	}
	
	public static <T extends ASTNode> T srOf(ASTNode rangeNode, T node) {
		node.setSourceRange(rangeNode.getStartPos(), rangeNode.getLength());
		return node;
	}
	
	public static <T extends ASTNode> T srEffective(BaseLexElement lexElement, T node) {
		int startPos = lexElement.isMissingElement() ? lexElement.getFullRangeStartPos() : lexElement.getStartPos();
		node.setSourceRange(startPos, lexElement.getEndPos() - startPos);
		return node;
	}
	
	public final <T extends ASTNode> T srToPosition(int nodeStart, T node) {
		node.setSourceRange(nodeStart, getSourcePosition() - nodeStart);
		return node;
	}
	
	public final <T extends ASTNode> T srToPosition(BaseLexElement start, T node) {
		int declStart = start.getStartPos();
		node.setSourceRange(declStart, getSourcePosition() - declStart);
		return node;
	}
	
	public final <T extends ASTNode> T srToPosition(ASTNode left, T node) {
		assertTrue(left.hasSourceRangeInfo() && !node.hasSourceRangeInfo());
		assertTrue(!node.isParsedStatus());
		return srToPosition(left.getStartPos(), node);
	}
	
	/* ---- Collection creation helpers ---- */
	
	// TODO: optimize some of this arrayView creation
	
	public static <T extends IASTNode> ArrayView<T> arrayView(Collection<T> list) {
		if(list == null)
			return null;
		T[] array = ArrayUtil.createFrom(list, CoreUtil.<Class<T>>blindCast(ASTNode.class));
		return ArrayView.create(array);
	}
	
	public static <T> ArrayView<T> arrayViewG(Collection<? extends T> list) {
		if(list == null)
			return null;
		return ArrayView.create((T[]) ArrayUtil.createFrom(list, Object.class));
	}
	
	public static <T extends IASTNode> NodeListView<T> nodeListView(ArrayList<T> list) {
		if(list == null)
			return null;
		boolean hasEndingSeparator = false;
		if(list.size() > 0 && list.get(list.size()-1) == null) {
			list.remove(list.size()-1);
			hasEndingSeparator = true;
		}
		return nodeListView(list, hasEndingSeparator);
	}
	
	public static <T extends IASTNode> NodeListView<T> nodeListView(ArrayList<T> list, boolean hasEndingSeparator) {
		T[] array = ArrayUtil.createFrom(list, CoreUtil.<Class<T>>blindCast(ASTNode.class));
		return new NodeListView<>(array, hasEndingSeparator);
	}
	
	public static boolean lazyInitIsEmpty(ArrayView<?> arrayView) {
		if(arrayView != null) {
			assertTrue(!arrayView.isEmpty());
		}
		return arrayView == null;
	}
	
}