package dtool.parser;


public enum EDeeParserErrors {
	
	UNKNOWN_TOKEN,
	MALFORMED_TOKEN,
	
	EXPECTED_TOKEN_BEFORE,
	EXPECTED_OTHER_AFTER,
	
	SYNTAX_ERROR // Most generic syntax error (unexpected token out of many)
	
}
