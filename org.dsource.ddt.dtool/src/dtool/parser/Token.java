package dtool.parser;

import static dtool.util.NewUtils.assertNotNull_;

public class Token {
	
	public final DeeTokens type;
	public final int start;
	public final String value; //TODO, don't store this for certain kinds of nodes
	
	public Token(DeeTokens tokenCode, String value, int start) {
		this.type = assertNotNull_(tokenCode);
		this.value = value;
		this.start = start;
	}
	
	public final DeeTokens getTokenType() {
		return type;
	}
	
	public final int getStartPos() {
		return start;
	}
	
	public int getLength() {
		return value.length();
	}
	
	public int getEndPos() {
		return start + value.length();
	}
	
	public final String getSourceValue() {
		return value;
	}
	
	public static class ErrorToken extends Token {
		
		protected final String errorMessage;
		protected final DeeTokens originalToken;
		
		public ErrorToken(String value, int start, DeeTokens originalToken, String errorMessage) {
			super(DeeTokens.ERROR, value, start);
			this.originalToken = originalToken;
			this.errorMessage = errorMessage;
		}
	}
	
}