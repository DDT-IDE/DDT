package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import dtool.tests.CommonTestUtils;

public class DeeLexerTest extends CommonTestUtils {
	
	@Test
	public void basicLexerTest() throws Exception { basicLexerTest$(); }
	public void basicLexerTest$() throws Exception {
		runLexerTest("  \t", array(DeeTokens.WHITESPACE));
		runLexerTest("\n", array(DeeTokens.EOL));
		runLexerTest("/*asd*/", array(DeeTokens.COMMENT));
		runLexerTest("asdf", array(DeeTokens.IDENTIFIER));
		
		runLexerTest("  \t\n/*dfg*/asdf"
			+"//coment\n123asd", 
			array(DeeTokens.WHITESPACE, DeeTokens.EOL, DeeTokens.COMMENT, DeeTokens.IDENTIFIER, 
				DeeTokens.COMMENT, DeeTokens.INTEGER, DeeTokens.IDENTIFIER));
	}
	
	public static void runLexerTest(String source, DeeTokens[] deeTokens) {
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
		assertEquals(source, constructedSource.toString());
		checkToken(deeLexer, DeeTokens.EOF, readSourceOffset);
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