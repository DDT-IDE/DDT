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

import dtool.parser.LexElement.MissingLexElement;

/**
 * Concrete D Parser class
 * 
 */
// XXX: BM: this code is a bit convoluted and strange, we use inheritance just for the sake of namespace importing
public class DeeParser extends DeeParser_Decls {
	
	public static DeeParserResult parseSource(String source) {
		DeeParser deeParser = new DeeParser(source);
		return new DeeParserResult(deeParser.parseModule(), deeParser);
	}
	
	public DeeParserResult parseUsingRule(ParseRuleDescription parseRule) {
		return parseUsingRule(parseRule.name);
	}
	public DeeParserResult parseUsingRule(String parseRule) {
		DeeParserResult result;
		if(parseRule == null) {
			result = new DeeParserResult(parseModule(), this);
		} else if(parseRule.equalsIgnoreCase(DeeParser.RULE_EXPRESSION.name)) {
			result = new DeeParserResult(parseExpression(), this);
		} else if(parseRule.equalsIgnoreCase(DeeParser.RULE_REFERENCE.name)) {
			result = new DeeParserResult(parseTypeReference(), this);
		} else if(parseRule.equalsIgnoreCase(DeeParser.RULE_DECLARATION.name)) {
			result = new DeeParserResult(parseDeclaration(), this);
		} else if(parseRule.equals(RULE_TYPE_OR_EXP) || parseRule.equalsIgnoreCase("TypeOrExp") ) {
			result = new DeeParserResult(parseTypeOrExpression(true), this);
		} else if(parseRule.equalsIgnoreCase("ExpOrType") ) {
			result = new DeeParserResult(parseExpressionOrType(), this);
		} else if(parseRule.equalsIgnoreCase(DeeParser.RULE_INITIALIZER.name)) {
			result = new DeeParserResult(parseInitializer(), this);
		} else if(parseRule.equalsIgnoreCase("INIT_STRUCT")) {
			result = new DeeParserResult(parseStructInitializer(), this);
		} else if(parseRule.equals("DeclarationImport")) {
			result = new DeeParserResult(parseImportDeclaration(), this);
		} else {
			throw assertFail();
		}
		assertTrue(enabled);
		return result;
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
	public int getLexPosition() {
		return getLexSource().getLexPosition();
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
	public final LexElement consumeInput() {
		return getEnabledLexSource().consumeInput();
	}
	
	@Override
	public MissingLexElement consumeSubChannelTokens() {
		return getEnabledLexSource().consumeSubChannelTokens();
	}
	
	public DeeParserState enterBacktrackableMode() {
		DeeParserState parserState = new DeeParserState();
		parserState.lexSource = getEnabledLexSource().saveState();
		return parserState;
	}
	
	public void restoreOriginalState(DeeParserState savedState) {
		this.lexSource.resetState(savedState.lexSource);
	}
	
	public class DeeParserState {
		
		protected ArrayList<ParserError> errors;
		protected LexElementSource lexSource;
		
	}
	
}