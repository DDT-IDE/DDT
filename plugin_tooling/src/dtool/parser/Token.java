package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ISourceRepresentation;
import dtool.ast.SourceRange;

public class Token implements ISourceRepresentation, IToken {
	
	public final DeeTokens type;
	public final int startPos;
	public final String source;
	
	public Token(DeeTokens tokenType, String source, int startPos) {
		this.type = assertNotNull(tokenType);
		this.source = assertNotNull(source);
		this.startPos = startPos;
		if(tokenType.hasSourceValue()) {
			assertEquals(tokenType.getSourceValue(), source);
		}
	}
	
	@Override
	public final DeeTokens getType() {
		return type;
	}
	
	@Override
	public final int getStartPos() {
		return startPos;
	}
	
	public final int getLength() {
		return source.length();
	}
	
	@Override
	public final int getEndPos() {
		return startPos + source.length();
	}
	
	@Override
	public final SourceRange getSourceRange() {
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
		
		public ErrorToken(DeeTokens tokenType, String value, int start, LexerErrorTypes error) {
			super(tokenType, value, start);
			this.error = error;
			if(tokenType == DeeTokens.INVALID_TOKEN || error == LexerErrorTypes.INVALID_CHARACTERS) {
				assertTrue(tokenType == DeeTokens.INVALID_TOKEN);
				assertTrue(error == LexerErrorTypes.INVALID_CHARACTERS);
			}
		}
		
		@Override
		public LexerErrorTypes getError() {
			return error;
		}
	}
	
}