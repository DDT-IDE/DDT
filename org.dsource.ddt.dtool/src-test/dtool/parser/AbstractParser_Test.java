package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import dtool.ast.SourceRange;
import dtool.tests.CommonTestUtils;

public class AbstractParser_Test extends CommonTestUtils {
	
	protected final class AbstractLexerExtension extends AbstractLexer {
		private AbstractLexerExtension(String source) {
			super(source);
		}
		
		@Override
		public Token parseToken() {
			if(lookAheadIsEOF()) {
				return createToken(DeeTokens.EOF);
			}
			if(lookAhead() == ' ') {
				return createToken(DeeTokens.WHITESPACE, 1);
			}
			return createToken(DeeTokens.IDENTIFIER, 2);
		}
	}
	
	@Test
	public void testInit() throws Exception { testInit$(); }
	public void testInit$() throws Exception {
		AbstractParser parser = new AbstractParser(new AbstractLexerExtension("abcdefgh"));
		assertEquals(parser.getLastToken().getSourceRange() , new SourceRange(0, 0));
		assertEquals(parser.getLastTokenEndPos(), 0);
		
		assertTrue(parser.lookAheadQueue.size() == 0);
	}
	
	@Test
	public void testLookAheadQueue() throws Exception { testLookAheadQueue$(); }
	public void testLookAheadQueue$() throws Exception {
		AbstractParser parser = new AbstractParser(new AbstractLexerExtension("abcd  efgh"));
		assertTrue(parser.lookAheadQueue.size() == 0);
		
		assertEquals(parser.lookAheadElement(0).token.tokenSource, "ab");
		assertTrue(parser.lookAheadQueue.size() == 1);
		assertEquals(parser.lookAheadElement(1).token.tokenSource, "cd");
		assertTrue(parser.lookAheadQueue.size() == 2);
		assertEquals(parser.lookAheadElement(3).token.tokenSource, "gh");
		assertEquals(parser.lookAheadElement(4).token.type, DeeTokens.EOF);
		assertTrue(parser.lookAheadQueue.size() == 5);
		parser.consumeLookAhead();
		
		assertEquals(parser.lastLexElement.token.tokenSource, "ab");
		assertTrue(parser.lookAheadQueue.size() == 4);
		assertEquals(parser.lookAheadElement(2).token.tokenSource, "gh");
		assertEquals(parser.lookAheadElement(0).token.tokenSource, "cd");
		assertEquals(parser.lookAheadElement(1).token.tokenSource, "ef");
		assertEquals(parser.lookAheadElement(1).ignoredPrecedingTokens.length, 2);
		assertEquals(parser.lookAheadElement(3).token.type, DeeTokens.EOF);

		parser.consumeLookAhead();
		assertTrue(parser.lookAheadQueue.size() == 3);
		assertEquals(parser.lastLexElement.token.tokenSource, "cd");
		assertEquals(parser.lookAheadElement(0).token.tokenSource, "ef");
		assertEquals(parser.lookAheadElement(1).token.tokenSource, "gh");
		assertEquals(parser.lookAheadElement(2).token.type, DeeTokens.EOF);
		assertEquals(parser.lookAheadElement(3).token.type, DeeTokens.EOF);
		assertTrue(parser.lookAheadQueue.size() == 4);
		
		parser.consumeLookAhead();
		parser.consumeLookAhead();
		assertEquals(parser.lastLexElement.token.tokenSource, "gh");
		assertTrue(parser.lookAheadQueue.size() == 2);
		assertEquals(parser.lookAheadElement(0).token.type, DeeTokens.EOF);
		assertEquals(parser.lookAheadElement(1).token.type, DeeTokens.EOF);
	}
	
}