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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;

import dtool.ast.ASTNode;
import dtool.parser.LexElement.MissingLexElement;

/**
 * Concrete D Parser class
 * 
 */
// XXX: BM: this code is a bit convoluted and strange, we use inheritance just for the sake of namespace importing
public class DeeParser extends DeeParser_Statements {
	
	public static DeeParserResult parseSource(String source, String defaultModuleName) {
		DeeParser deeParser = new DeeParser(source);
		DeeParserResult result = new DeeParserResult(deeParser.parseModule(defaultModuleName), deeParser);
		result.module.doAnalysisOnTree();
		return result;
	}
	
	public DeeParserResult parseUsingRule(ParseRuleDescription parseRule) {
		return parseUsingRule(parseRule.id);
	}
	public DeeParserResult parseUsingRule(String parseRule) {
		NodeResult<? extends ASTNode> nodeResult;
		if(parseRule == null) {
			nodeResult = parseModule("__unnamed_module");
		} else if(parseRule.equalsIgnoreCase(DeeParser.RULE_EXPRESSION.id)) {
			nodeResult = parseExpression();
		} else if(parseRule.equalsIgnoreCase(DeeParser.RULE_REFERENCE.id)) {
			nodeResult = parseTypeReference();
		} else if(parseRule.equalsIgnoreCase(DeeParser.RULE_DECLARATION.id)) {
			nodeResult = parseDeclaration();
		} else if(parseRule.equalsIgnoreCase(RULE_TYPE_OR_EXP.id) || parseRule.equalsIgnoreCase("TypeOrExp")) {
			nodeResult = parseTypeOrExpression(true);
		} else if(parseRule.equalsIgnoreCase(DeeParser.RULE_INITIALIZER.id)) {
			nodeResult = parseInitializer();
		} else if(parseRule.equalsIgnoreCase(DeeParser.RULE_STATEMENT.id)) {
			nodeResult = parseStatement();
		} else if(parseRule.equalsIgnoreCase("INIT_STRUCT")) {
			nodeResult = parseStructInitializer();
		} else {
			throw assertFail();
		}
		assertTrue(enabled);
		return new DeeParserResult(nodeResult, this);
	}
	
	
	protected final String source;
	protected LexElementSource lexSource;
	protected ArrayList<ParserError> lexerErrors = new ArrayList<>();
	protected boolean enabled;
	
	public DeeParser(String source) {
		this(new DeeLexer(source));
	}
	
	public DeeParser(DeeLexer deeLexer) {
		this.source = deeLexer.getSource();
		this.lexSource = new LexElementSource(new DeeLexElementProducer().produceLexTokens(deeLexer));
		this.enabled = true;
	}
	
	@Override
	protected final DeeParser thisParser() {
		return this;
	}
	
	@Override
	public final String getSource() {
		return source;
	}
	
	public final class DeeLexElementProducer extends LexElementProducer {
		
		@Override
		protected void tokenCreated(Token token) {
			DeeTokenSemantics.checkTokenErrors(token, lexerErrors);
		}
		
	}
	
	public LexElementSource getEnabledLexSource() {
		assertTrue(enabled);
		return lexSource;
	}
	
	protected LexElementSource getLexSource() {
		return lexSource;
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		assertTrue(this.enabled == !enabled);
		this.enabled = enabled;
	}
	
	@Override
	public boolean isEnabled() { // There should be no reason to use this other than for contract checks only
		return enabled;
	}
	
	@Override
	public int getSourcePosition() {
		return getLexSource().getSourcePosition();
	}
	
	@Override
	public LexElement lookAheadElement(int laIndex) {
		return getEnabledLexSource().lookAheadElement(laIndex);
	}
	
	@Override
	public LexElement lastLexElement() {
		return getLexSource().lastLexElement();
	}
	
	@Override
	public final LexElement consumeLookAhead() {
		return getEnabledLexSource().consumeInput();
	}
	
	@Override
	public MissingLexElement consumeSubChannelTokens() {
		return getEnabledLexSource().consumeSubChannelTokens();
	}
	
	public DeeParserState saveParserState() {
		LexElementSource lexSource = getEnabledLexSource().saveState();
		return new DeeParserState(lexSource, enabled);
	}
	
	public void restoreOriginalState(DeeParserState savedState) {
		this.lexSource.resetState(savedState.lexSource);
		this.enabled = savedState.enabled;
	}
	
	public class DeeParserState {
		
		protected final LexElementSource lexSource;
		protected final boolean enabled;
		
		public DeeParserState(LexElementSource lexSource, boolean enabled) {
			this.lexSource = lexSource;
			this.enabled = enabled;
		}
		
	}
	
}