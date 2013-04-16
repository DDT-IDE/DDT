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
	
	
	protected final CommonLexElementSource lexSource;
	protected ArrayList<ParserError> errors = new ArrayList<ParserError>();
	protected boolean enabled = true;
	
	public DeeParser(CommonLexElementSource lexSource) {
		this.lexSource = lexSource;
	}
	
	public DeeParser(String source) {
		this(new DeeLexer(source));
	}
	
	public DeeParser(DeeLexer deeLexer) {
		this(new LexerElementSource(deeLexer));
	}
	
	@Override
	protected void submitError(ParserError error) {
		errors.add(error);
	}
	
	public CommonLexElementSource getEnabledLexSource() {
		assertTrue(enabled);
		return lexSource;
	}
	
	protected CommonLexElementSource getLexSource() {
		return lexSource;
	}
	
	@Override
	public String getSource() {
		return getLexSource().getSource();
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
	protected LexElement lastLexElement() {
		return getLexSource().lastLexElement();
	}
	
	@Override
	protected final LexElement consumeInput() {
		LexElement consumedElement = getEnabledLexSource().consumeInput();
		analyzeIgnoredTokens(consumedElement);
		DeeTokenSemantics.checkTokenErrors(consumedElement.token, this);
		return consumedElement;
	}
	
	@Override
	public MissingLexElement consumeIgnoreTokens(DeeTokens expectedToken) {
		MissingLexElement consumedElement = getEnabledLexSource().consumeIgnoreTokens(expectedToken);
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
	
}