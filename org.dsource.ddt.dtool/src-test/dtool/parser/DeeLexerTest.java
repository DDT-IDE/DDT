package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import dtool.tests.DToolBaseTest;

public class DeeLexerTest extends DToolBaseTest {
	
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
	}
	
	
	public static void testLexerTokenizing(String source, DeeTokens[] deeTokens) {
		TokenChecker[] tokenCheckers = new TokenChecker[deeTokens.length];
		for (int i = 0; i < deeTokens.length; i++) {
			tokenCheckers[i] = new TokenChecker(deeTokens[i]);
		}
		testLexerTokenizing(source, tokenCheckers);
	}
	
	public static void testLexerTokenizing(String source, TokenChecker[] tokenCheckers) {
		DeeLexer deeLexer = new DeeTestsLexer(source);
		int readSourceOffset = 0;
		
		StringBuilder constructedSource = new StringBuilder();
		for (int i = 0; i < tokenCheckers.length; i++) {
			TokenChecker tokenChecker = tokenCheckers[i];
			Token token = tokenChecker.checkToken(deeLexer, readSourceOffset);
			readSourceOffset = token.getEndPos();
			String sourceSoFar = source.substring(0, readSourceOffset);
			
			String tokenSourceValue = token.getSourceValue();
			// retest with just the token source to make sure boundaries are correct
			if(tokenCheckers.length != 1 && !tokenSourceValue.startsWith("#!")) {
				testLexerTokenizing(tokenSourceValue, array(tokenChecker));
			}
			
			constructedSource.append(tokenSourceValue);
			assertTrue(sourceSoFar.contentEquals(constructedSource));
		}
		assertTrue(deeLexer.tokenStartPos == source.length());
		new TokenChecker(DeeTokens.EOF).checkToken(deeLexer, readSourceOffset);
		assertEquals(source, constructedSource.toString());
	}
	
	protected static final class DeeTestsLexer extends DeeLexer {
		public DeeTestsLexer(String source) {
			super(source);
		}
		
		@Override
		public String toString() {
			return source.substring(0, pos) + "<---parser--->" + source.substring(pos, source.length());
		}
	}
	
	public static class TokenChecker {
		
		private DeeTokens expectedTokenType;
		private LexerErrorTypes expectedError;
		
		public TokenChecker(DeeTokens deeToken) {
			this(deeToken, null);
		}
		
		public TokenChecker(DeeTokens tokenType, LexerErrorTypes error) {
			this.expectedTokenType = tokenType;
			this.expectedError = error;
			if(tokenType == DeeTokens.INVALID_TOKEN) {
				assertTrue(expectedError == null);
				this.expectedError = LexerErrorTypes.INVALID_CHARACTERS;
			}
		}
		
		public Token checkToken(DeeLexer deeLexer, int readOffset) {
			Token token = deeLexer.next();
			
			if(expectedTokenType != null) {
				assertTrue(token.type == expectedTokenType);
			} else {
				assertTrue(expectedError == null);
			}
			
			assertTrue(token.getError() == expectedError);
			
			assertTrue(token.getStartPos() == readOffset);
			assertEquals(deeLexer.source.subSequence(token.getStartPos(), token.getEndPos()), token.getSourceValue());
			
			DeeTokens tokenCode = token.getTokenType();
			if(tokenCode == DeeTokens.EOF) {
				assertTrue(token.getEndPos() >= token.getStartPos());
			} else {
				assertTrue(token.getEndPos() > token.getStartPos());
			}
			return token;
		}
	}
	
}