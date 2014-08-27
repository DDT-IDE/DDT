/*******************************************************************************
 * Copyright (c) 2012, 2014 Bruno Medeiros and other Contributors.
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

import org.junit.Test;

import dtool.parser.common.AbstractLexerTest;

public class DeeLexerTest extends AbstractLexerTest {
	
	@Test
	public void basicLexerTest() throws Exception { basicLexerTest$(); }
	public void basicLexerTest$() throws Exception {
		testLexerTokenizing("  \t", array(DeeTokens.WHITESPACE));
		testLexerTokenizing("\n", array(DeeTokens.LINE_END));
		testLexerTokenizing("/*asd*/", array(DeeTokens.COMMENT_MULTI));
		testLexerTokenizing("/+as/+ sadf  +/ d+/", array(DeeTokens.COMMENT_NESTED));
		testLexerTokenizing("// asdfs", array(DeeTokens.COMMENT_LINE));
		
		testLexerTokenizing("`asdfsdaf`", array(DeeTokens.STRING_WYSIWYG));
		testLexerTokenizing("r\"asdfsdaf\"", array(DeeTokens.STRING_WYSIWYG));
		testLexerTokenizing("\"asdfsdaf\"d", array(DeeTokens.STRING_DQ));
		testLexerTokenizing("x\"A0 01 FF\"w", array(DeeTokens.STRING_HEX));
		
		testLexerTokenizing("q\"/foo(xxx)/\"", array(DeeTokens.STRING_DELIM));
		testLexerTokenizing("q\"(foo(xxx))\"", array(DeeTokens.STRING_DELIM));
		testLexerTokenizing("q\"foo\n(xxx)\nfoo\"", array(DeeTokens.STRING_DELIM));
		testLexerTokenizing("q{ asdf __TIME__  {nest \n braces} }", array(DeeTokens.STRING_TOKENS));
		
		
		testLexerTokenizing("(){}[]", array(DeeTokens.OPEN_PARENS, DeeTokens.CLOSE_PARENS, 
			DeeTokens.OPEN_BRACE, DeeTokens.CLOSE_BRACE, DeeTokens.OPEN_BRACKET, DeeTokens.CLOSE_BRACKET));

		testLexerTokenizing("'a'", array(DeeTokens.CHARACTER));
		
		testLexerTokenizing("123", array(DeeTokens.INTEGER_DECIMAL));
		testLexerTokenizing("0b101", array(DeeTokens.INTEGER_BINARY));
		testLexerTokenizing("01234567", array(DeeTokens.INTEGER_OCTAL));
		testLexerTokenizing("0x0123456789ABDCEF", array(DeeTokens.INTEGER_HEX));
		
		testLexerTokenizing("1234567890.1234567890E123F", array(DeeTokens.FLOAT_DECIMAL));
		testLexerTokenizing("0x0123456789ABDCEFP123f", array(DeeTokens.FLOAT_HEX));
		
		testLexerTokenizing("asdf", array(DeeTokens.IDENTIFIER));
		testLexerTokenizing("final", array(DeeTokens.KW_FINAL));
		testLexerTokenizing("finally", array(DeeTokens.KW_FINALLY));
		testLexerTokenizing("finallyx", array(DeeTokens.IDENTIFIER));
		
		DeeLexer lexer = testLexerTokenizing("(blah)", 
			array(DeeTokens.OPEN_PARENS, DeeTokens.IDENTIFIER, DeeTokens.CLOSE_PARENS));
		lexer.reset(1);
		testLexerTokenizing(lexer, 1, tokenCheckers(DeeTokens.IDENTIFIER, DeeTokens.CLOSE_PARENS));
		lexer.reset(6);
		testLexerTokenizing(lexer, 6);
	}
	
	@Test
	public void test_isValidDIdentifier() throws Exception { test_isValidDIdentifier$(); }
	public void test_isValidDIdentifier$() throws Exception {
		assertTrue(DeeLexingUtil.isValidDIdentifier("foo"));
		assertTrue(DeeLexingUtil.isValidDIdentifier("bar321"));
		assertTrue(DeeLexingUtil.isValidDIdentifier("_bar"));
		assertTrue(DeeLexingUtil.isValidDIdentifier("_foo_bar"));
		assertTrue(DeeLexingUtil.isValidDIdentifier("foo_bar"));
		assertTrue(DeeLexingUtil.isValidDIdentifier("Açores"));
		assertTrue(DeeLexingUtil.isValidDIdentifier("Солярис"));
		
		assertTrue(!DeeLexingUtil.isValidDIdentifier(""));
		assertTrue(!DeeLexingUtil.isValidDIdentifier("foo.d"));
		assertTrue(!DeeLexingUtil.isValidDIdentifier("123foo"));
		assertTrue(!DeeLexingUtil.isValidDIdentifier("bar.foo"));
		assertTrue(!DeeLexingUtil.isValidDIdentifier("bar foo"));
		assertTrue(!DeeLexingUtil.isValidDIdentifier("bar-foo"));
		
		// Test keywords
		assertTrue(!DeeLexingUtil.isValidDIdentifier("while"));
		assertTrue(!DeeLexingUtil.isValidDIdentifier("package"));
		assertTrue(!DeeLexingUtil.isValidDIdentifier("__FILE__"));
		
		for (DeeTokens token : DeeTokenHelper.keyWords_All) {
			assertTrue(!DeeLexingUtil.isValidDIdentifier(token.getSourceValue()));
		}
		
	}
	
}