package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;

import java.util.Arrays;

import dtool.parser.Token.ErrorToken;


public class DeeLexer extends CommonTokenSource {
	
	public DeeLexer(CharSequence source) {
		super(source);
		
		scanBegginning();
	}

	protected void scanBegginning() {
		// TODO: UTF BOM's
// If the source file does not start with a BOM, then the first character must be less than or equal to U0000007F.
		
		pos = tokenStartPos;
		
		if(lookAhead(0) == '#' && lookAhead(1) == '!') {
			seekToNewline();
			currentToken = createToken(DeeTokens.SCRIPT_LINE_INTRO);
		}
		return;
	}
	
	public enum DeeRuleSelection {
		BAD_TOKEN,
		
		EOF,
		EOF_CHARS,
		
		EOL,
		WHITESPACE,
		
		SLASH,
		OPEN_PARENS,
		OPEN_BRACKET,
		OPEN_BRACE,
		CLOSE_BRACE,
		LESS_THAN,
		
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
		
		startRuleDecider['('] = DeeRuleSelection.OPEN_PARENS;
		startRuleDecider['{'] = DeeRuleSelection.OPEN_BRACE;
		startRuleDecider['}'] = DeeRuleSelection.CLOSE_BRACE;
		startRuleDecider['['] = DeeRuleSelection.OPEN_BRACKET;
		startRuleDecider['<'] = DeeRuleSelection.LESS_THAN;
		
		startRuleDecider['/'] = DeeRuleSelection.SLASH;
		
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
	
	@Override
	protected Token parseToken() {
		pos = tokenStartPos;
		
		DeeRuleSelection ruleCategory = getLexingDecision(lookAhead());
		
		switch (ruleCategory) {
		case EOF: return createToken(DeeTokens.EOF);
		
		case EOF_CHARS: return matchEOFCharacter();
		case EOL: return matchEOL();
		case WHITESPACE: return matchWhiteSpace();
		
		case SLASH: return ruleSlashStart();
		
		case STRING_ALTWYSIWYG: return matchWYSIWYGString();
		case ALPHA_R: return ruleRStart();
		case STRING_DOUBLE_QUOTES: return matchString();
		case ALPHA_H: return ruleHStart();
		case ALPHA_Q: return ruleQStart();
		
		case DIGIT: return matchDigitRules();
		case ALPHA: return ruleAlphaStart();
		
		 //TODO
		case OPEN_PARENS: return matchError();
		case OPEN_BRACE: return matchSimpleToken(DeeTokens.OPEN_BRACE);
		case CLOSE_BRACE: return matchSimpleToken(DeeTokens.CLOSE_BRACE);
		case OPEN_BRACKET: return matchError();
		case LESS_THAN: return matchError();
		
		case BAD_TOKEN: return matchError();
		
		}
		throw assertUnreachable();
	}
	
	public static DeeRuleSelection getLexingDecision(int ch) {
		if(ch == EOF) {
			return DeeRuleSelection.EOF;
		}
		if(ch > ASCII_LIMIT) {
			// BM: Hum I'm not sure this Unicode handling is correct according to D.
			if(Character.isLowSurrogate((char) ch) || Character.isHighSurrogate((char) ch)
				|| Character.isUnicodeIdentifierPart(ch)
			) {
				return DeeRuleSelection.ALPHA;
			}
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
	
	protected Token matchSimpleToken(DeeTokens tokenCode) {
		pos++;
		return new Token(tokenCode, source, tokenStartPos, pos);
	}
	
	public ErrorToken createErrorToken(int endPos, String message) {
		return new Token.ErrorToken(source, tokenStartPos, endPos, message);
	}
	
	protected Token matchEOFCharacter() {
		assertTrue(startRuleDecider[lookAheadAscii()] == DeeRuleSelection.EOF_CHARS);
		pos++;
		return createToken(DeeTokens.EOF);
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
	
	protected Token ruleAlphaStart() {
		assertTrue(startRuleDecider[lookAheadAscii()].canBeIdentifierStart);
		pos++;
		
		seekIdentifierPartChars(); 
		return createToken(DeeTokens.IDENTIFIER);
	}
	
	/** Seek position until lookahead is not valid identifier part*/
	public void seekIdentifierPartChars() {
		do {
			int ch = lookAhead();
			
			DeeRuleSelection charCategory = getLexingDecision(ch);
			// TODO consider UTF, etc.
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
			Token token = parseAndConsumeToken();
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