package dtool.parser;

public enum LexerErrorTypes {
	
	INVALID_CHARACTERS("Invalid characters."),
	
	COMMENT_NOT_TERMINATED("Comment not terminated, expected a '*/'."),
	COMMENTNESTED_NOT_TERMINATED("Nested comment not terminated, expected a '+/'."),
	
	STRING_NOT_TERMINATED__REACHED_EOF("String not terminated, found EOF."),
	
	STRING_DELIM_NO_DELIMETER("Found EOF when expecting string delimeter."),
	STRING_DELIM_NOT_PROPERLY_TERMINATED("Expected '\"' after closing delimiter in delimited string."),
	STRING_DELIM_ID_NOT_PROPERLY_FORMED("Expected newline after identifier of delimited string."),
	
	CHAR_LITERAL_NOT_TERMINATED__REACHED_EOF("Character literal not terminated, found EOF."),
	CHAR_LITERAL_NOT_TERMINATED__REACHED_EOL("Character literal not terminated, found new line."),
	CHAR_LITERAL_EMPTY("Character literal is empty, no character specified."),
	
	INT_LITERAL_BINARY__INVALID_DIGITS("Invalid digits in binary literal"),
	INT_LITERAL_OCTAL__INVALID_DIGITS("Invalid digits in octal literal"),
	INT_LITERAL__HAS_NO_DIGITS("Literal has no digits, only underscores"),
	
	FLOAT_LITERAL__EXP_HAS_NO_DIGITS("Exponent of float literal has no digits."),
	FLOAT_LITERAL__HEX_HAS_NO_EXP("Hexadecimal float literal has no exponent."),
	
	SPECIAL_TOKEN_LINE_BAD_FORMAT("Invalid format for #line token, must be #line integer [\"filespec\"]\n."),
	SPECIAL_TOKEN_INVALID("Invalid format for # pragma syntax."),
	;
	public final String message;
	private LexerErrorTypes(String message) {
		this.message = message;
	}
}
