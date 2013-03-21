package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import dtool.ast.SourceRange;
import dtool.tests.CommonTestUtils;

public class LexElementSource_Test extends CommonTestUtils {
	
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
		LexerElementSource lexSource = new LexerElementSource(new TestsInstrumentedLexer("abcdefgh"));
		
		assertEquals(lexSource.lastLexElement.token.getSourceRange() , new SourceRange(0, 0));
		
		assertTrue(lexSource.lookAheadQueue.size() == 0);
	}
	
	@Test
	public void testLookAheadQueue() throws Exception { testLookAheadQueue$(); }
	public void testLookAheadQueue$() throws Exception {
		LexerElementSource lexSource = new LexerElementSource(new TestsInstrumentedLexer("abcd  efgh"));
		assertTrue(lexSource.lookAheadQueue.size() == 0);
		
		assertEquals(lexSource.lookAheadElement(0).token.source, "ab");
		assertTrue(lexSource.lookAheadQueue.size() == 1);
		assertEquals(lexSource.lookAheadElement(1).token.source, "cd");
		assertTrue(lexSource.lookAheadQueue.size() == 2);
		assertEquals(lexSource.lookAheadElement(3).token.source, "gh");
		assertEquals(lexSource.lookAheadElement(4).token.type, DeeTokens.EOF);
		assertTrue(lexSource.lookAheadQueue.size() == 5);
		lexSource.consumeLookAhead();
		
		assertEquals(lexSource.lastLexElement.token.source, "ab");
		assertTrue(lexSource.lookAheadQueue.size() == 4);
		assertEquals(lexSource.lookAheadElement(2).token.source, "gh");
		assertEquals(lexSource.lookAheadElement(0).token.source, "cd");
		assertEquals(lexSource.lookAheadElement(1).token.source, "ef");
		assertEquals(lexSource.lookAheadElement(1).ignoredPrecedingTokens.length, 2);
		assertEquals(lexSource.lookAheadElement(3).token.type, DeeTokens.EOF);

		lexSource.consumeLookAhead();
		assertTrue(lexSource.lookAheadQueue.size() == 3);
		assertEquals(lexSource.lastLexElement.token.source, "cd");
		assertEquals(lexSource.lookAheadElement(0).token.source, "ef");
		assertEquals(lexSource.lookAheadElement(1).token.source, "gh");
		assertEquals(lexSource.lookAheadElement(2).token.type, DeeTokens.EOF);
		assertEquals(lexSource.lookAheadElement(3).token.type, DeeTokens.EOF);
		assertTrue(lexSource.lookAheadQueue.size() == 4);
		
		lexSource.consumeLookAhead();
		lexSource.consumeLookAhead();
		assertEquals(lexSource.lastLexElement.token.source, "gh");
		assertTrue(lexSource.lookAheadQueue.size() == 2);
		assertEquals(lexSource.lookAheadElement(0).token.type, DeeTokens.EOF);
		assertEquals(lexSource.lookAheadElement(1).token.type, DeeTokens.EOF);
	}
	
	@Test
	public void testConsumeWhiteSpace() throws Exception { testConsumeWhiteSpace$(); }
	public void testConsumeWhiteSpace$() throws Exception {
		LexerElementSource lexSource = new LexerElementSource(new TestsInstrumentedLexer("abcd  efgh"));
		
		assertEquals(lexSource.lookAheadElement(2).token.source, "ef");
		assertTrue(lexSource.lastNonMissingLexElement == lexSource.lastLexElement);
		assertTrue(lexSource.lastNonMissingLexElement.isMissingElement() == false);
		lexSource.consumeIgnoreTokens();
		assertTrue(lexSource.lastLexElement.isMissingElement() == true);
		assertTrue(lexSource.lastNonMissingLexElement.isMissingElement() == false);
		
		lexSource.consumeInput();
		lexSource.consumeInput();
		assertEquals(lexSource.lookAheadElement(0).token.source, "ef");
		lexSource.consumeIgnoreTokens();
		assertTrue(lexSource.lastLexElement.isMissingElement() == true);
		assertTrue(lexSource.lastNonMissingLexElement.isMissingElement() == false);
		
		assertTrue(lexSource.lookAheadElement().getStartPos() == 6);
		
		assertEquals(lexSource.lookAheadElement(0).token.source, "ef");
	}
	
}