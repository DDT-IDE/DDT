package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import dtool.tests.CommonTestUtils;

public class DeeLexerTest extends CommonTestUtils {
	
	@Test
	public void basicLexerTest() throws Exception { basicLexerTest$(); }
	public void basicLexerTest$() throws Exception {
		testLexerTokenizing("  \t", array(DeeTokens.WHITESPACE));
		testLexerTokenizing("\n", array(DeeTokens.EOL));
		testLexerTokenizing("/*asd*/", array(DeeTokens.COMMENT_MULTI));
		testLexerTokenizing("/+as/+ sadf  +/ d+/", array(DeeTokens.COMMENT_NESTED));
		testLexerTokenizing("// asdfs", array(DeeTokens.COMMENT_LINE));
		
		testLexerTokenizing("`asdfsdaf`", array(DeeTokens.STRING_WYSIWYG));
		testLexerTokenizing("r\"asdfsdaf\"", array(DeeTokens.STRING_WYSIWYG));
		testLexerTokenizing("\"asdfsdaf\"d", array(DeeTokens.STRING_DQ));
		testLexerTokenizing("x\"A0 01 FF\"w", array(DeeTokens.STRING_HEX));
		
		//TODO
		//runLexerTest("q\"(foo(xxx))\"", array(DeeTokens.STRING_DELIM));
		//runLexerTest("q{(foo(x\"asfdsf\"xx))}", array(DeeTokens.STRING_TOKENS));
		
		
		testLexerTokenizing("asdf", array(DeeTokens.IDENTIFIER));
		
	}
	
	public static void testLexerTokenizing(String source, DeeTokens[] deeTokens) {
		DeeLexer deeLexer = new DeeLexer(source);
		int readSourceOffset = 0;
		
		StringBuilder constructedSource = new StringBuilder();
		for (int i = 0; i < deeTokens.length; i++) {
			DeeTokens expectedTokenCode = deeTokens[i];
			Token token = checkToken(deeLexer, expectedTokenCode, readSourceOffset);
			readSourceOffset = token.getEndPos();
			String sourceSoFar = source.substring(0, readSourceOffset);
			
			constructedSource.append(token.getSourceValue());
			assertTrue(sourceSoFar.contentEquals(constructedSource));
		}
		checkToken(deeLexer, DeeTokens.EOF, readSourceOffset);
		assertEquals(source, constructedSource.toString());
	}
	
	public static Token checkToken(DeeLexer deeLexer, DeeTokens expectedTokenCode, int readOffset) {
		DeeTokens tokenCode = deeLexer.peek();
		if(expectedTokenCode != null) {
			assertTrue(tokenCode == expectedTokenCode);
		}
		Token token = deeLexer.next();
		assertTrue(token.getTokenCode() == tokenCode);
		assertTrue(token.getStartPos() == readOffset);
		
		switch (tokenCode) {
		case EOF: assertTrue(token.getEndPos() >= token.getStartPos());
			break;
		default:
			assertTrue(token.getEndPos() > token.getStartPos());
		}
		return token;
	}
	
}