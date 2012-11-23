package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;

import java.util.Arrays;

import dtool.parser.Token.ErrorToken;


public class DeeLexer extends CommonTokenSource {
	
	public DeeLexer(CharSequence source) {
		super(source);
	}
	
	public enum DeeRuleSelection {
		BAD_TOKEN,
		
		EOF,
		EOF_CHARS,
		
		EOL,
		WHITESPACE,
		
		HASH,
		OPEN_PARENS, CLOSE_PARENS,
		OPEN_BRACE, CLOSE_BRACE,
		OPEN_BRACKET, CLOSE_BRACKET,
		
		QUESTION, COMMA, SEMICOLON, COLON, DOLLAR, AT,
		
		MINUS, PLUS, STAR, SLASH, MOD,
		
		AMPERSAND, VBAR, CARET, EQUAL, TILDE,
		DOT,
		
		LESS_THAN,
		GREATER_THAN,
		EXCLAMATION,
		
		ALPHA(true, true),
		DIGIT(false, true),
		
		STRING_ALTWYSIWYG,
		ALPHA_R(true, true),
		STRING_DOUBLE_QUOTES,
		ALPHA_H(true, true),
		ALPHA_Q(true, true),
		;
		private final boolean canBeIdentifierStart;
		private final boolean canBeIdentifierPart;
		
		private DeeRuleSelection() {
			this(false, false);
		}
		
		private DeeRuleSelection(boolean canBeIdentifierStart, boolean canBeIdentifierPart) {
			this.canBeIdentifierStart = canBeIdentifierStart;
			this.canBeIdentifierPart = canBeIdentifierPart;
		}
		
	}
	
	protected static final DeeRuleSelection[] startRuleDecider;
	
	static {
		startRuleDecider = new DeeRuleSelection[ASCII_LIMIT+1];
		Arrays.fill(startRuleDecider, DeeRuleSelection.BAD_TOKEN);
		
		startRuleDecider[0x00] = DeeRuleSelection.EOF_CHARS;
		startRuleDecider[0x1A] = DeeRuleSelection.EOF_CHARS;
		
		startRuleDecider[0x0D] = DeeRuleSelection.EOL;
		startRuleDecider[0x0A] = DeeRuleSelection.EOL;
		
		startRuleDecider[0x20] = DeeRuleSelection.WHITESPACE;
		startRuleDecider[0x09] = DeeRuleSelection.WHITESPACE;
		startRuleDecider[0x0B] = DeeRuleSelection.WHITESPACE;
		startRuleDecider[0x0C] = DeeRuleSelection.WHITESPACE;
		
		startRuleDecider['#'] = DeeRuleSelection.HASH;

		startRuleDecider['('] = DeeRuleSelection.OPEN_PARENS;
		startRuleDecider[')'] = DeeRuleSelection.CLOSE_PARENS;
		startRuleDecider['{'] = DeeRuleSelection.OPEN_BRACE;
		startRuleDecider['}'] = DeeRuleSelection.CLOSE_BRACE;
		startRuleDecider['['] = DeeRuleSelection.OPEN_BRACKET;
		startRuleDecider[']'] = DeeRuleSelection.CLOSE_BRACKET;
		
		startRuleDecider['?'] = DeeRuleSelection.QUESTION;
		startRuleDecider[','] = DeeRuleSelection.COMMA;
		startRuleDecider[';'] = DeeRuleSelection.SEMICOLON;
		startRuleDecider[':'] = DeeRuleSelection.COLON;
		startRuleDecider['$'] = DeeRuleSelection.DOLLAR;
		startRuleDecider['@'] = DeeRuleSelection.AT;
		
		startRuleDecider['.'] = DeeRuleSelection.DOT;
		
		startRuleDecider['-'] = DeeRuleSelection.MINUS;
		startRuleDecider['+'] = DeeRuleSelection.PLUS;
		startRuleDecider['*'] = DeeRuleSelection.STAR;
		startRuleDecider['/'] = DeeRuleSelection.SLASH;
		startRuleDecider['%'] = DeeRuleSelection.MOD;
		
		startRuleDecider['&'] = DeeRuleSelection.AMPERSAND;
		startRuleDecider['|'] = DeeRuleSelection.VBAR;
		startRuleDecider['^'] = DeeRuleSelection.CARET;
		startRuleDecider['='] = DeeRuleSelection.EQUAL;
		startRuleDecider['~'] = DeeRuleSelection.TILDE;
		
		startRuleDecider['<'] = DeeRuleSelection.LESS_THAN;
		startRuleDecider['>'] = DeeRuleSelection.GREATER_THAN;
		startRuleDecider['!'] = DeeRuleSelection.EXCLAMATION;
		
		
		Arrays.fill(startRuleDecider, '0', '9'+1, DeeRuleSelection.DIGIT);
		Arrays.fill(startRuleDecider, 'a', 'z'+1, DeeRuleSelection.ALPHA);
		Arrays.fill(startRuleDecider, 'A', 'Z'+1, DeeRuleSelection.ALPHA);
		startRuleDecider['_'] = DeeRuleSelection.ALPHA;
		
		startRuleDecider['`'] = DeeRuleSelection.STRING_ALTWYSIWYG;
		startRuleDecider['r'] = DeeRuleSelection.ALPHA_R;
		startRuleDecider['"'] = DeeRuleSelection.STRING_DOUBLE_QUOTES;
		startRuleDecider['x'] = DeeRuleSelection.ALPHA_H;
		startRuleDecider['q'] = DeeRuleSelection.ALPHA_Q;
	}
	
	protected Token createToken(DeeTokens tokenCode) {
		return new Token(tokenCode, source, tokenStartPos, pos);
	}
	protected Token createToken(DeeTokens tokenCode, int length) {
		pos = tokenStartPos + length;
		return new Token(tokenCode, source, tokenStartPos, pos);
	}
	
	@Override
	protected Token parseToken() {
		pos = tokenStartPos;
		
		DeeRuleSelection ruleDecision = getLexingDecision(lookAhead());
		
		switch (ruleDecision) {
		case EOF: return createToken(DeeTokens.EOF);
		
		case EOF_CHARS: return matchEOFCharacter();
		case EOL: return matchEOL();
		case WHITESPACE: return matchWhiteSpace();
		
		case HASH: return ruleHashStart();
		case SLASH: return ruleSlashStart();
		
		case STRING_ALTWYSIWYG: return matchWYSIWYGString();
		case ALPHA_R: return ruleRStart();
		case STRING_DOUBLE_QUOTES: return matchString();
		case ALPHA_H: return ruleHStart();
		case ALPHA_Q: return ruleQStart();
		
		case DIGIT: return matchDigitRules();
		case ALPHA: return ruleAlphaStart();
		
		case OPEN_PARENS: return createToken(DeeTokens.OPEN_PARENS, 1);
		case CLOSE_PARENS: return createToken(DeeTokens.CLOSE_PARENS, 1);
		case OPEN_BRACE: return createToken(DeeTokens.OPEN_BRACE, 1);
		case CLOSE_BRACE: return createToken(DeeTokens.CLOSE_BRACE, 1);
		case OPEN_BRACKET: return createToken(DeeTokens.OPEN_BRACKET, 1);
		case CLOSE_BRACKET: return createToken(DeeTokens.CLOSE_BRACKET, 1);
		
		case QUESTION: return createToken(DeeTokens.QUESTION, 1);
		case COMMA: return createToken(DeeTokens.COMMA, 1);
		case SEMICOLON: return createToken(DeeTokens.SEMICOLON, 1);
		case COLON: return createToken(DeeTokens.COLON, 1);
		case DOLLAR: return createToken(DeeTokens.DOLLAR, 1);
		case AT: return createToken(DeeTokens.AT, 1);
		
		case DOT: return ruleDotStart();
		
		case PLUS: return rule3Choices('=', DeeTokens.PLUS_ASSIGN, '+', DeeTokens.INCREMENT, DeeTokens.PLUS);
		case MINUS: return rule3Choices('=', DeeTokens.MINUS_ASSIGN, '-', DeeTokens.DECREMENT, DeeTokens.MINUS);
		case STAR: return rule2Choices('=', DeeTokens.MULT_ASSIGN, DeeTokens.STAR);
		case MOD: return rule2Choices('=', DeeTokens.MOD_ASSIGN, DeeTokens.MOD);
		
		case AMPERSAND: 
			return rule3Choices('=', DeeTokens.AND_ASSIGN, '&', DeeTokens.LOGICAL_AND, DeeTokens.AND);
		case VBAR: 
			return rule3Choices('=', DeeTokens.OR_ASSIGN, '|', DeeTokens.LOGICAL_OR, DeeTokens.OR);
		case CARET: return rule2Choices('=', DeeTokens.XOR_ASSIGN, DeeTokens.XOR);
		case EQUAL: return rule3Choices('=', DeeTokens.EQUALS, '>', DeeTokens.LAMBDA, DeeTokens.ASSIGN);
		case TILDE: return rule2Choices('=', DeeTokens.CONCAT_ASSIGN, DeeTokens.CONCAT);
		
		case LESS_THAN: return ruleLessStart();
		case GREATER_THAN: return ruleGreaterStart();
		case EXCLAMATION: return ruleExclamation();
		
		case BAD_TOKEN: return matchError();
		
		}
		throw assertUnreachable();
	}
	
	public static DeeRuleSelection getLexingDecision(int ch) {
		if(ch == EOF) {
			return DeeRuleSelection.EOF;
		}
		if(ch > ASCII_LIMIT) {
			return DeeRuleSelection.ALPHA;
		}
		return startRuleDecider[ch];
	}
	
	protected Token matchError() {
		assertTrue(startRuleDecider[lookAheadAscii()] == DeeRuleSelection.BAD_TOKEN);
		while(true) {
			pos++;
			int ch = lookAhead();
			if(getLexingDecision(ch) == DeeRuleSelection.BAD_TOKEN) {
				continue;
			} else {
				return createErrorToken(pos, DeeParserMessages.INVALID_TOKEN);
			}
		}
	}
	
	public ErrorToken createErrorToken(int endPos, String message) {
		return new Token.ErrorToken(source, tokenStartPos, endPos, message);
	}
	
	protected Token matchEOFCharacter() {
		assertTrue(startRuleDecider[lookAheadAscii()] == DeeRuleSelection.EOF_CHARS);
		return createToken(DeeTokens.EOF, 1);
	}
	
	protected Token matchEOL() {
		assertTrue(startRuleDecider[lookAheadAscii()] == DeeRuleSelection.EOL);
		if(lookAhead() == 0x0D && lookAhead(1) == 0x0A) {
			pos += 2;
		} else {
			pos += 1;
		}
		return createToken(DeeTokens.EOL);
	}
	
	protected Token matchWhiteSpace() {
		assertTrue(startRuleDecider[lookAheadAscii()] == DeeRuleSelection.WHITESPACE);
		
		while(true) {
			pos++;
			int ch = lookAhead();
			if(getLexingDecision(ch) == DeeRuleSelection.WHITESPACE) {
				continue;
			} else {
				return createToken(DeeTokens.WHITESPACE);
			}
		}
	}
	
	protected Token ruleHashStart() {
		if(pos == 0 && lookAhead(1) == '!') {
			pos += 2;
			seekToNewline();
			return createToken(DeeTokens.SCRIPT_LINE_INTRO);
		} else {
			pos += 1;
			return createErrorToken(pos, DeeParserMessages.INVALID_TOKEN);
		}
	}
	
	protected Token ruleAlphaStart() {
		assertTrue(getLexingDecision(lookAhead()).canBeIdentifierStart);
		pos++;
		// Note, according to D spec, not all non-ASCII characters are valid as identifier characters
		// but for simplification we ignore that for lexing. 
		// Perhaps this can be analized later in a lexing semantics phase.
		seekIdentifierPartChars();
		return createToken(DeeTokens.IDENTIFIER);
	}
	
	/** Seek position until lookahead is not valid identifier part */
	public void seekIdentifierPartChars() {
		do {
			DeeRuleSelection charCategory = getLexingDecision(lookAhead());
			if(!charCategory.canBeIdentifierPart) {
				break;
			}
			pos++;
		} while(true);
	}
	
	protected static final String[] SEEKUNTIL_MULTICOMMENTS = { "+/", "/+" };
	
	protected Token ruleSlashStart() {
		assertTrue(startRuleDecider[lookAheadAscii()] == DeeRuleSelection.SLASH);
		
		pos++;
		
		if(lookAhead() == '*') {
			pos++;
			int result = seekUntil("*/");
			if(result == 0) {
				return createToken(DeeTokens.COMMENT_MULTI);
			} else {
				return createErrorToken(pos, DeeParserMessages.COMMENT_NOT_TERMINATED);
			}
		} else if(lookAhead() == '+') {
			pos++;
			int nestingLevel = 1;
			do {
				int result = seekUntil(SEEKUNTIL_MULTICOMMENTS);
				
				if(result == 0) { // "+/"
					nestingLevel--;
				} else if(result == 1) { // "/+"
					nestingLevel++;
				} else {
					assertTrue(result == -1);
					return createErrorToken(pos, DeeParserMessages.COMMENTNESTED_NOT_TERMINATED);
				}
			} while (nestingLevel > 0);
			return createToken(DeeTokens.COMMENT_NESTED);
			
		} else if(lookAhead() == '/') {
			pos++;
			seekToNewlineOrEOFRule();
			// Note that EOF is also a valid terminator for this comment
			return createToken(DeeTokens.COMMENT_LINE);
		} else if(lookAhead() == '=') {
			pos++;
			return createToken(DeeTokens.DIV_ASSIGN);
		} else {
			return createToken(DeeTokens.DIV);
		}
	}
	
	public final void seekToNewlineOrEOFRule() {
		while(true) {
			int ch = lookAhead();
			if(ch == EOF) {
				return;
			}
			pos++;
			if(ch == '\r') {
				if(lookAhead() == '\n') {
					pos++;
				}
				return;
			} else if(ch == '\n' || getLexingDecision(ch) == DeeRuleSelection.EOF_CHARS) {
				return;
			}
		}
	}
	
	protected Token matchWYSIWYGString() {
		assertTrue(startRuleDecider[lookAheadAscii()] == DeeRuleSelection.STRING_ALTWYSIWYG);
		return matchVerbatimString('`', DeeTokens.STRING_WYSIWYG);
	}
	
	protected Token matchVerbatimString(char quoteChar, DeeTokens stringToken) {
		pos++;
		
		int result = seekUntil(quoteChar);
		if(result == 0) {
			ruleStringPostFix();
			return createToken(stringToken);
		} else {
			assertTrue(result == -1);
			return createErrorToken(pos, DeeParserMessages.COMMENTNESTED_NOT_TERMINATED);
		}
	}
	
	protected void ruleStringPostFix() {
		int ch = lookAhead();
		switch(ch) {
		case 'c': pos++; break;
		case 'w': pos++; break;
		case 'd': pos++; break;
		}
	}
	
	protected Token ruleRStart() {
		assertTrue(startRuleDecider[lookAheadAscii()] == DeeRuleSelection.ALPHA_R);
		
		if(lookAhead(1) == '"') {
			pos++; 
			return matchVerbatimString('"', DeeTokens.STRING_WYSIWYG);
		}
		return ruleAlphaStart(); 
	}
	
	
	protected Token matchString() {
		assertTrue(startRuleDecider[lookAheadAscii()] == DeeRuleSelection.STRING_DOUBLE_QUOTES);
		
		pos++;
		while(true) {
			int ch = lookAhead();
			
			if(ch == '"') {
				pos++;
				ruleStringPostFix();
				return createToken(DeeTokens.STRING_DQ);
			} else if (ch == -1) {
				// TODO , maybe recover using EOL?
				return createErrorToken(pos, DeeParserMessages.STRING_NOT_TERMINATED);
			} else if (ch == '\\') {
				if (lookAhead(1) == '"') {
					pos += 2; 
					continue;
				}
				// We ignore the other escape sequences rules since they are not important for lexing 
			}
			pos++;
		}
	}
	
	protected Token ruleHStart() {
		assertTrue(startRuleDecider[lookAheadAscii()] == DeeRuleSelection.ALPHA_H);
		
		if(lookAhead(1) == '"') {
			pos++; 
			return matchVerbatimString('"', DeeTokens.STRING_HEX);
		} else {
			return ruleAlphaStart();
		}
	}
	
	protected Token ruleQStart() {
		if(lookAhead(1) == '"') {
			return matchDelimString();
		} else if(lookAhead(1) == '{') {
			return matchTokenString();
		} else {
			return ruleAlphaStart(); 
		}
	}
	
	public Token matchDelimString() {
		pos+=2;
		int ch = lookAhead();
		
		DeeRuleSelection ruleSelection = getLexingDecision(ch); 
		
		switch(ruleSelection) {
		case EOF: return createErrorToken(pos, DeeParserMessages.STRING_DELIM_NO_DELIMETER);
		case OPEN_PARENS: return matchSimpleDelimString('(',')');
		case OPEN_BRACKET: return matchSimpleDelimString('[',']');
		case OPEN_BRACE: return matchSimpleDelimString('{','}'); 
		case LESS_THAN: return matchSimpleDelimString('<','>');
		
		default:
			if(ruleSelection.canBeIdentifierStart) {
				return matchHereDocDelimString();
			} else {
				return matchSimpleDelimString((char)ch, (char)ch);
			}
		}
	}
	
	public Token matchSimpleDelimString(char openDelim, char closeDelim) {
		assertTrue(lookAhead() == openDelim);
		pos++;
		int nestingLevel = 1;
		
		do {
			int result = seekUntil(closeDelim, openDelim);
			// note, closeDelim can be equal to openDelim, in which case result == 1 should never happen 
			
			if(result == 0) { // closeDelim
				nestingLevel--;
			} else if(result == 1) { // openDelim
				nestingLevel++;
			} else {
				assertTrue(result == -1);
				return createErrorToken(pos, DeeParserMessages.STRING_NOT_TERMINATED__REACHED_EOF);
			}
		} while (nestingLevel > 0);
		
		if(lookAhead() == '"') {
			pos++;
			return createToken(DeeTokens.STRING_DELIM);
		} else {
			seekUntil('"');
			return createErrorToken(pos, DeeParserMessages.STRING_DELIM_NOT_PROPERLY_TERMINATED);
		}
	}
	
	public Token matchHereDocDelimString() {
		int idStartPos = pos;
		pos++;
		seekIdentifierPartChars();
		String hereDocId = source.subSequence(idStartPos, pos).toString(); // Hum, optimization hot spot
		
		if(getLexingDecision(lookAhead()) != DeeRuleSelection.EOL) {
			seekHereDocEndDelim(hereDocId);
			return createErrorToken(pos, DeeParserMessages.STRING_DELIM_ID_NOT_PROPERLY_FORMED);
		}
		
		int result = seekHereDocEndDelim(hereDocId);
		if(result == -1) {
			return createErrorToken(pos, DeeParserMessages.STRING_NOT_TERMINATED__REACHED_EOF);
		}
		assertTrue(result == 0);
		return createToken(DeeTokens.STRING_DELIM);
	}
	
	public int seekHereDocEndDelim(String hereDocId) {
		int result;
		while(true) {
			result = seekToNewline();
			if(result == -1) {
				break;
			}
			if(inputMatchesSequence(hereDocId)) {
				pos += hereDocId.length();
				if(lookAhead() == '"') {
					pos++;
					result = 0;
					break;
				}
			}
		}
		return result;
	}
	
	public Token matchTokenString() {
		pos+=2;
		
		int tokenStringStartPos = tokenStartPos;
		tokenStartPos = pos;
		
		int nestingLevel = 1;
		do {
			Token token = next();
			if(token.getTokenCode() == DeeTokens.OPEN_BRACE) {
				nestingLevel++;
			} else if (token.getTokenCode() == DeeTokens.CLOSE_BRACE) {
				nestingLevel--;
			} else if (token.getTokenCode() == DeeTokens.EOF) {
				tokenStartPos = tokenStringStartPos;
				return createErrorToken(pos, DeeParserMessages.STRING_NOT_TERMINATED__REACHED_EOF);
			}
		} while(nestingLevel > 0);
		
		tokenStartPos = tokenStringStartPos;
		return createToken(DeeTokens.STRING_TOKENS);
	}
	
	public final Token ruleDotStart() {
		if(lookAhead(1) == '.') {
			if(lookAhead(2) == '.') {
				return createToken(DeeTokens.TRIPLE_DOT, 3);
			}
			return createToken(DeeTokens.DOUBLE_DOT, 2);
		} else {
			return createToken(DeeTokens.DOT, 1);
		}
	}
	
	protected final Token ruleLessStart() {
		if(lookAhead(1) == '=') {
			return createToken(DeeTokens.LESS_EQUAL, 2);
		} else if(lookAhead(1) == '<') {
			// <<
			if(lookAhead(2) == '=') {
				return createToken(DeeTokens.LEFT_SHIFT_ASSIGN, 3);
			}
			return createToken(DeeTokens.LEFT_SHIFT, 2);
		} else if(lookAhead(1) == '>') {
			// <>
			if(lookAhead(2) == '=') {
				return createToken(DeeTokens.LESS_GREATER_EQUAL, 3);
			}
			return createToken(DeeTokens.LESS_GREATER, 2);
		}
		return createToken(DeeTokens.LESS_THAN, 1);
	}
	
	protected final Token ruleGreaterStart() {
		if(lookAhead(1) == '=') {
			return createToken(DeeTokens.GREATER_EQUAL, 2);
		} else if(lookAhead(1) == '>') {
			// >>
			if(lookAhead(2) == '=') {
				return createToken(DeeTokens.RIGHT_SHIFT_ASSIGN, 3);
			} else if(lookAhead(2) == '>') {
				// >>>
				if(lookAhead(3) == '=') {
					return createToken(DeeTokens.TRIPLE_RSHIFT_ASSIGN, 4);
				} 
				return createToken(DeeTokens.TRIPLE_RSHIFT, 3);
			} 
			return createToken(DeeTokens.RIGHT_SHIFT, 2);
		} 
		return createToken(DeeTokens.GREATER_THAN, 1);
	}
	
	protected final Token ruleExclamation() {
		if(lookAhead(1) == '=') {
			return createToken(DeeTokens.NOT_EQUAL, 2);
		} else if(lookAhead(1) == '<') {
			// !<
			if(lookAhead(2) == '=') {
				return createToken(DeeTokens.UNORDERED_G, 3);
			} else if(lookAhead(2) == '>') {
				// !<>
				if(lookAhead(3) == '=') {
					return createToken(DeeTokens.UNORDERED, 4);
				} 
				return createToken(DeeTokens.UNORDERED_E, 3);
			} 
			return createToken(DeeTokens.UNORDERED_GE, 2);
		} else if(lookAhead(1) == '>') {
			// !>
			if(lookAhead(2) == '=') {
				return createToken(DeeTokens.UNORDERED_L, 3);
			}
			return createToken(DeeTokens.UNORDERED_LE, 2);
		}
		return createToken(DeeTokens.NOT, 1);
	}
	
	protected Token matchDigitRules() {
		assertTrue(startRuleDecider[lookAheadAscii()] == DeeRuleSelection.DIGIT);
		
		while(true) {
			pos++;
			
			int ch = lookAhead();
			
			if(getLexingDecision(ch) == DeeRuleSelection.DIGIT) {
				continue;
			}
			
			return createToken(DeeTokens.INTEGER);
		}
	}
	
}