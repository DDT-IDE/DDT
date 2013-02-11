package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import dtool.ast.SourceRange;
import dtool.tests.CommonTestUtils;

public class AbstractParser_Test extends CommonTestUtils {
	
	protected final class TestsInstrumentedLexer extends AbstractLexer {
		private TestsInstrumentedLexer(String source) {
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
		AbstractParser parser = new AbstractParser(new TestsInstrumentedLexer("abcdefgh"));
		assertEquals(parser.lastLexElement.token.getSourceRange() , new SourceRange(0, 0));
		
		assertTrue(parser.lookAheadQueue.size() == 0);
	}
	
	@Test
	public void testLookAheadQueue() throws Exception { testLookAheadQueue$(); }
	public void testLookAheadQueue$() throws Exception {
		AbstractParser parser = new AbstractParser(new TestsInstrumentedLexer("abcd  efgh"));
		assertTrue(parser.lookAheadQueue.size() == 0);
		
		assertEquals(parser.lookAheadElement(0).token.source, "ab");
		assertTrue(parser.lookAheadQueue.size() == 1);
		assertEquals(parser.lookAheadElement(1).token.source, "cd");
		assertTrue(parser.lookAheadQueue.size() == 2);
		assertEquals(parser.lookAheadElement(3).token.source, "gh");
		assertEquals(parser.lookAheadElement(4).token.type, DeeTokens.EOF);
		assertTrue(parser.lookAheadQueue.size() == 5);
		parser.consumeLookAhead();
		
		assertEquals(parser.lastLexElement.token.source, "ab");
		assertTrue(parser.lookAheadQueue.size() == 4);
		assertEquals(parser.lookAheadElement(2).token.source, "gh");
		assertEquals(parser.lookAheadElement(0).token.source, "cd");
		assertEquals(parser.lookAheadElement(1).token.source, "ef");
		assertEquals(parser.lookAheadElement(1).ignoredPrecedingTokens.length, 2);
		assertEquals(parser.lookAheadElement(3).token.type, DeeTokens.EOF);

		parser.consumeLookAhead();
		assertTrue(parser.lookAheadQueue.size() == 3);
		assertEquals(parser.lastLexElement.token.source, "cd");
		assertEquals(parser.lookAheadElement(0).token.source, "ef");
		assertEquals(parser.lookAheadElement(1).token.source, "gh");
		assertEquals(parser.lookAheadElement(2).token.type, DeeTokens.EOF);
		assertEquals(parser.lookAheadElement(3).token.type, DeeTokens.EOF);
		assertTrue(parser.lookAheadQueue.size() == 4);
		
		parser.consumeLookAhead();
		parser.consumeLookAhead();
		assertEquals(parser.lastLexElement.token.source, "gh");
		assertTrue(parser.lookAheadQueue.size() == 2);
		assertEquals(parser.lookAheadElement(0).token.type, DeeTokens.EOF);
		assertEquals(parser.lookAheadElement(1).token.type, DeeTokens.EOF);
	}
	
	@Test
	public void testConsumeWhiteSpace() throws Exception { testConsumeWhiteSpace$(); }
	public void testConsumeWhiteSpace$() throws Exception {
		AbstractParser parser = new AbstractParser(new TestsInstrumentedLexer("abcd  efgh"));
		
		assertEquals(parser.lookAheadElement(2).token.source, "ef");
		assertTrue(parser.lastNonMissingLexElement == parser.lastLexElement);
		assertTrue(parser.lastNonMissingLexElement.isMissingElement() == false);
		parser.consumeIgnoredTokens();
		assertTrue(parser.lastNonMissingLexElement == parser.lastLexElement);
		assertTrue(parser.lastNonMissingLexElement.isMissingElement() == false);
		
		parser.consumeInput();
		parser.consumeInput();
		assertEquals(parser.lookAheadElement(0).token.source, "ef");
		parser.consumeIgnoredTokens();
		assertTrue(parser.lastNonMissingLexElement == parser.lastLexElement);
		assertTrue(parser.lastNonMissingLexElement.isMissingElement() == false);
		
		assertTrue(parser.lookAheadElement().getStartPos() == 6);
		
		assertEquals(parser.lookAheadElement(0).token.source, "ef");
	}
	
}