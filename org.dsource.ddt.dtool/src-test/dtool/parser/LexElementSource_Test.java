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
			if(lookAhead() == 'X') {
				createToken(DeeTokens.IDENTIFIER, 1);
			}
			return createToken(DeeTokens.IDENTIFIER, 2);
		}
	}
	
	@Test
	public void testInit() throws Exception { testInit$(); }
	public void testInit$() throws Exception {
		LexElementSource lexSource = LexElementProducer.createFromLexer(new TestsInstrumentedLexer("abcdefgh"));
		
		assertEquals(lexSource.lastLexElement.token.getSourceRange(), new SourceRange(0, 0));
		
		assertTrue(lexSource.lexElementList.size() == 5);
		assertEquals(lexSource.lookAheadElement(4).token.type, DeeTokens.EOF);
		assertEquals(lexSource.lookAheadElement(5).token.type, DeeTokens.EOF); // Test index beyond first EOF
	}
	
	@Test
	public void testElementList() throws Exception { testElementList$(); }
	public void testElementList$() throws Exception {
		
		LexElementSource lexSource = LexElementProducer.createFromLexer(new TestsInstrumentedLexer("abcd  efgh"));
		assertTrue(lexSource.lexElementList.size() == 5);
		
		assertEquals(lexSource.lookAheadElement(0).token.source, "ab");
		assertEquals(lexSource.lookAheadElement(1).token.source, "cd");
		assertEquals(lexSource.lookAheadElement(3).token.source, "gh");
		assertEquals(lexSource.lookAheadElement(4).token.type, DeeTokens.EOF);
		lexSource.consumeInput();
		
		assertEquals(lexSource.lastLexElement().token.source, "ab");
		assertEquals(lexSource.lookAheadElement(0).token.source, "cd");
		assertEquals(lexSource.lookAheadElement(1).token.source, "ef");
		assertEquals(lexSource.lookAheadElement(1).precedingSubChannelTokens.length, 2);
		assertEquals(lexSource.lookAheadElement(2).token.source, "gh");
		assertEquals(lexSource.lookAheadElement(3).token.type, DeeTokens.EOF);

		lexSource.consumeInput();
		assertEquals(lexSource.lastLexElement().token.source, "cd");
		assertEquals(lexSource.getSourceLexPosition(), 4);
		assertEquals(lexSource.lookAheadElement(0).token.source, "ef");
		assertEquals(lexSource.lookAheadElement(1).token.source, "gh");
		assertEquals(lexSource.lookAheadElement(2).token.type, DeeTokens.EOF);
		assertEquals(lexSource.lookAheadElement(3).token.type, DeeTokens.EOF); // Test index beyond first EOF
		
		lexSource.consumeInput();
		lexSource.consumeInput();
		assertEquals(lexSource.lastLexElement().token.source, "gh");
		assertEquals(lexSource.lookAheadElement(0).token.type, DeeTokens.EOF);
		assertEquals(lexSource.lookAheadElement(1).token.type, DeeTokens.EOF);
	}
	
	@Test
	public void testConsumeWhiteSpace() throws Exception { testConsumeWhiteSpace$(); }
	public void testConsumeWhiteSpace$() throws Exception {
		LexElementSource lexSource = LexElementProducer.createFromLexer(new TestsInstrumentedLexer("abcd  efgh")); 
		
		lexSource.consumeSubChannelTokens();
		assertTrue(lexSource.getSourceLexPosition() == 0);
		
		assertEquals(lexSource.lookAheadElement(2).token.source, "ef");
		lexSource.consumeInput();
		lexSource.consumeInput();
		assertEquals(lexSource.lookAheadElement(0).token.source, "ef");
		assertTrue(lexSource.getSourceLexPosition() == 4);
		lexSource.consumeSubChannelTokens();
		assertTrue(lexSource.getSourceLexPosition() == 6);
		
		assertEquals(lexSource.lookAheadElement(0).getStartPos(), 6);
		assertEquals(lexSource.lookAheadElement(0).token.source, "ef");
	}
	
}