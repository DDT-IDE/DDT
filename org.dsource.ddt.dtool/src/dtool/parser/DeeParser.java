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
		} else if(parseRule.equals("DeclarationImport")) {
			result = new DeeParserResult(parseImportDeclaration(), this);
		} else {
			throw assertFail();
		}
		assertTrue(enabled);
		return result;
	}
	
	
	protected final String source;
	protected ArrayList<ParserError> errors;
	protected LexElementSource lexSource;
	protected boolean enabled;
	
	public DeeParser(String source) {
		this(new DeeLexer(source));
	}
	
	public DeeParser(DeeLexer deeLexer) {
		this.source = deeLexer.getSource();
		this.errors = new ArrayList<>();
		this.lexSource = new LexElementSource(new DeeLexElementProducer().produceLexTokens(deeLexer));
		this.enabled = true;
		
		this.pendingMissingTokenErrors = new ArrayList<>(4);
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
			DeeTokenSemantics.checkTokenErrors(token, DeeParser.this);
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
	
	@Override
	protected void submitError(ParserError error) {
		errors.add(error);
	}
}