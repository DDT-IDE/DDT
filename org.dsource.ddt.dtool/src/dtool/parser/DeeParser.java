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

import java.util.ArrayList;

import dtool.ast.definitions.Module;

/**
 * Concrete D Parser class
 * 
 * XXX: BM: this code is a bit convoluted and strange, we use inheritance just for the sake of namespace importing
 */
public class DeeParser extends DeeParser_Decls {
	
	public static DeeParserResult parseSource(String source) {
		DeeParser deeParser = new DeeParser(source);
		Module module = deeParser.parseModule();
		return new DeeParserResult(module, deeParser.errors);
	}
	
	public DeeParserResult parseUsingRule(String parseRule) {
		DeeParserResult result;
		if(parseRule == null) {
			result = new DeeParserResult(parseModule(), this.errors);
		} else if(parseRule.equalsIgnoreCase(DeeParser.RULE_EXPRESSION.name)) {
			result = new DeeParserResult(parseExpression(), this.errors);
		} else if(parseRule.equalsIgnoreCase(DeeParser.RULE_REFERENCE.name)) {
			result = new DeeParserResult(parseReference(), this.errors);
		} else if(parseRule.equalsIgnoreCase(DeeParser.RULE_DECLARATION.name)) {
			result = new DeeParserResult(parseDeclaration(), this.errors);
		} else if(parseRule.equals("DeclarationImport")) {
			result = new DeeParserResult(parseImportDeclaration(), this.errors);
		} else {
			throw assertFail();
		}
		return result;
	}
	
	
	protected final LexerElementSource lexSource;
	protected final ArrayList<ParserError> errors = new ArrayList<ParserError>();
	
	public DeeParser(String source) {
		this(new DeeLexer(source));
	}
	
	public DeeParser(DeeLexer deeLexer) {
		lexSource = new LexerElementSource(deeLexer);
	}
	
	@Override
	protected void submitError(ParserError error) {
		errors.add(error);
	}
	
	@Override
	public String getSource() {
		return lexSource.getSource();
	}
	
	@Override
	public LexElement lookAheadElement(int laIndex) {
		return lexSource.lookAheadElement(laIndex);
	}
	
	@Override
	protected LexElement lastLexElement() {
		return lexSource.lastLexElement;
	}
	
	@Override
	protected LexElement lastNonMissingLexElement() {
		return lexSource.lastNonMissingLexElement;
	}
	
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
	
}