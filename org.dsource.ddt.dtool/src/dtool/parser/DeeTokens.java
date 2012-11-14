package dtool.parser;

public enum DeeTokens {
	
	EOF,
	ERROR,
	
	EOL,
	WHITESPACE,
	
	COMMENT,
	
	DIV_X,
	
	IDENTIFIER,
	INTEGER,
	;
	
	public byte getOrdinal() {
		return (byte) ordinal();
	}
	
}
