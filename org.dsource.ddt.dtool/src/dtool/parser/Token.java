package dtool.parser;

public class Token {
	
	public final DeeTokens tokenType;
	public final int start;
	public final String value; //TODO, don't store this for certain kinds of nodes
	
	public Token(DeeTokens tokenCode, String value, int start) {
		this.tokenType = tokenCode;
		this.value = value;
		this.start = start;
	}
	
	public final DeeTokens getTokenType() {
		return tokenType;
	}
	
	public final int getStartPos() {
		return start;
	}
	
	public final int getLength() {
		return value.length();
	}
	
	public final int getEndPos() {
		return start + value.length();
	}
	
	public String getSourceValue() {
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