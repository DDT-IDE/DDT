package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

/**
 * Abstract class with common util methods for LexElement sources. 
 */
public abstract class CommonLexElementSource {
	
	public abstract String getSource();
	
	public abstract LexElement lookAheadElement(int laIndex);
	
	public final LexElement lookAheadElement() {
		return lookAheadElement(0);
	}
	
	public final Token lookAheadToken() {
		return lookAheadElement(0).token;
	}
	
	public final DeeTokens lookAhead() {
		return lookAheadElement(0).token.getRawTokenType();
	}
	
	public final DeeTokens lookAhead(int laIndex) {
		return lookAheadElement(laIndex).token.getRawTokenType();
	}
	
	public int getParserPosition() {
		assertTrue(lastLexElement().getEndPos() == lookAheadElement().getFullRangeStartPos());
		return lastLexElement().getEndPos();
	}
	
	protected abstract LexElement lastLexElement();
	
	protected abstract LexElement lastNonMissingLexElement();
	
	protected abstract LexElement consumeInput();
	
	protected final Token consumeLookAhead() {
		return consumeInput().token;
	}
	
	protected final LexElement consumeLookAhead(DeeTokens tokenType) {
		assertTrue(lookAhead() == tokenType);
		return consumeInput();
	}
	
	public abstract LexElement consumeIgnoreTokens(DeeTokens expectedToken);
	
	public final LexElement consumeIgnoreTokens() {
		return consumeIgnoreTokens(null);
	}
	
}