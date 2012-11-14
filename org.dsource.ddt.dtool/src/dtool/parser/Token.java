package dtool.parser;

public class Token {
	
	public final DeeTokens tokenCode;
	public final int start;
	public final String value; //TODO, don't store this.
	
	public Token(DeeTokens tokenCode, CharSequence source, int start, int end) {
		this.start = start;
		this.value = source.subSequence(start, end).toString();
		this.tokenCode = tokenCode;
	}
	
	public final DeeTokens getTokenCode() {
		return tokenCode;
	}
	
	public final int getStartPos() {
		return start;
	}
	
	public final int getEndPos() {
		return start + value.length();
	}
	
	public String getSourceValue() {
		return value;
	}
	
	public static class ErrorToken extends Token {
		
		protected final String errorMessage;
		
		public ErrorToken(CharSequence source, int start, int end, String errorMessage) {
			super(DeeTokens.ERROR, source, start, end);
			this.errorMessage = errorMessage;
		}
		
	}
	
}