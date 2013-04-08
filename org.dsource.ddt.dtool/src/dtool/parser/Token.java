package dtool.parser;

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ISourceRepresentation;
import dtool.ast.SourceRange;

public class Token implements ISourceRepresentation {
	
	public final DeeTokens type;
	public final int startPos;
	public final String source;
	
	public Token(DeeTokens tokenType, String source, int startPos) {
		this.type = assertNotNull_(tokenType);
		this.source = source;
		this.startPos = startPos;
		if(tokenType.getSourceValue() != null) {
			assertEquals(tokenType.getSourceValue(), source);
		}
	}
	
	public final DeeTokens getRawTokenType() {
		return type;
	}
	
	public final int getStartPos() {
		return startPos;
	}
	
	public int getLength() {
		return source.length();
	}
	
	public int getEndPos() {
		return startPos + source.length();
	}
	
	public SourceRange getSourceRange() {
		return new SourceRange(getStartPos(), getLength());
	}
	
	@Override
	public final String getSourceValue() {
		return source;
	}
	
	public LexerErrorTypes getError() {
		return null;
	}
	
	@Override
	public String toString() {
		return type +"►"+ source;
	}
	
	public static class ErrorToken extends Token {
		
		protected final LexerErrorTypes error;
		
		public ErrorToken(String value, int start, DeeTokens originalType, LexerErrorTypes error) {
			super(originalType, value, start);
			this.error = error;
			if(originalType == DeeTokens.INVALID_TOKEN || error == LexerErrorTypes.INVALID_CHARACTERS) {
				assertTrue(originalType == DeeTokens.INVALID_TOKEN);
				assertTrue(error == LexerErrorTypes.INVALID_CHARACTERS);
			}
		}
		
		@Override
		public LexerErrorTypes getError() {
			return error;
		}
	}
	
}