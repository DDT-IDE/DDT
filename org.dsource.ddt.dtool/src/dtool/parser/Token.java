package dtool.parser;

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.SourceRange;

public class Token {
	
	public final DeeTokens type;
	public final int startPos;
	public final String tokenSource; //TODO, don't store this for certain kinds of nodes
	
	public Token(DeeTokens tokenCode, String source, int startPos) {
		this.type = assertNotNull_(tokenCode);
		this.tokenSource = source;
		this.startPos = startPos;
	}
	
	public final DeeTokens getTokenType() {
		return type;
	}
	
	public final int getStartPos() {
		return startPos;
	}
	
	public int getLength() {
		return tokenSource.length();
	}
	
	public int getEndPos() {
		return startPos + tokenSource.length();
	}
	
	public SourceRange getSourceRange() {
		return new SourceRange(getStartPos(), getLength());
	}
	
	public final String getSourceValue() {
		return tokenSource;
	}
	
	public DeeTokens getEffectiveType() {
		return type;
	}
	
	public static class ErrorToken extends Token {
		
		protected final LexerErrorTypes error;
		protected final DeeTokens originalTokenType;
		
		public ErrorToken(String value, int start, DeeTokens originalType, LexerErrorTypes error) {
			super(DeeTokens.ERROR, value, start);
			this.originalTokenType = originalType;
			this.error = error;
			if(originalType == DeeTokens.ERROR || error == LexerErrorTypes.INVALID_CHARACTERS) {
				assertTrue(originalType == DeeTokens.ERROR);
				assertTrue(error == LexerErrorTypes.INVALID_CHARACTERS);
			}
		}
		
		@Override
		public DeeTokens getEffectiveType() {
			return originalTokenType;
		}
	}
	
}