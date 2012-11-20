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
		
		EOF,
		
		EOL,
		WHITESPACE,
		
		SLASH,
		
		ALPHA(true),
		DIGIT(true), 
		
		STRING_ALTWYSIWYG, 
		ALPHA_R(true),
		STRING_DOUBLE_QUOTES,
		ALPHA_H(true),
		ALPHA_Q(true),
		;
		protected final boolean canBeIdentifierPart;
		
		private DeeRuleSelection() {
			this(false);
		}
		
		private DeeRuleSelection(boolean canBeIdentifierPart) {
			this.canBeIdentifierPart = canBeIdentifierPart;
		}
		
		public final boolean canBeIdentifierPart() {
			return canBeIdentifierPart;
		}
	}
	
	protected static final DeeRuleSelection[] charRuleCategory;
	
	static {
		charRuleCategory = new DeeRuleSelection[ASCII_LIMIT];
		
		charRuleCategory[0x00] = DeeRuleSelection.EOF;
		charRuleCategory[0x1A] = DeeRuleSelection.EOF;
		
		charRuleCategory[0x0D] = DeeRuleSelection.EOL;
		charRuleCategory[0x0A] = DeeRuleSelection.EOL;
		
		charRuleCategory[0x20] = DeeRuleSelection.WHITESPACE;
		charRuleCategory[0x09] = DeeRuleSelection.WHITESPACE;
		charRuleCategory[0x0B] = DeeRuleSelection.WHITESPACE;
		charRuleCategory[0x0C] = DeeRuleSelection.WHITESPACE;
		
		charRuleCategory['/'] = DeeRuleSelection.SLASH;
		
		Arrays.fill(charRuleCategory, '0', '9'+1, DeeRuleSelection.DIGIT);
		Arrays.fill(charRuleCategory, 'a', 'z'+1, DeeRuleSelection.ALPHA);
		Arrays.fill(charRuleCategory, 'A', 'Z'+1, DeeRuleSelection.ALPHA);
		charRuleCategory['_'] = DeeRuleSelection.ALPHA;
		
		charRuleCategory['`'] = DeeRuleSelection.STRING_ALTWYSIWYG;
		charRuleCategory['r'] = DeeRuleSelection.ALPHA_R;
		charRuleCategory['"'] = DeeRuleSelection.STRING_DOUBLE_QUOTES;
		charRuleCategory['x'] = DeeRuleSelection.ALPHA_H;
		charRuleCategory['q'] = DeeRuleSelection.ALPHA_Q;
		
	}
	
	protected Token createToken(DeeTokens tokenCode) {
		return new Token(tokenCode, source, tokenStartPos, pos);
	}
	
	@Override
	protected Token parseToken() {
		pos = tokenStartPos;
		if(pos >= source.length()) {
			return createToken(DeeTokens.EOF);
		}
		
		char ch = source.charAt(pos);
		
		DeeRuleSelection ruleCategory = getCharCategory(ch);
		if(ruleCategory == null) {
			return matchError();
		}
		
		switch (ruleCategory) {
		case EOF: return matchEOFCharacter();
		case EOL: return matchEOL();
		case WHITESPACE: return matchWhiteSpace();
		case SLASH: return matchSlashRules();
		
		case STRING_ALTWYSIWYG: return matchWYSIWYGString();
		case ALPHA_R: return matchRRules();
		case STRING_DOUBLE_QUOTES: return matchString();
		case ALPHA_H: return matchHRules();
		case ALPHA_Q: return matchQRules();
		
		case DIGIT: return matchDigitRules();
		case ALPHA: return matchAlphaStartRules();
		}
		throw assertUnreachable();
	}
	
	public DeeRuleSelection getCharCategory(int ch) {
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
		return charRuleCategory[ch];
	}
	
	protected Token matchError() {
		int endPos = pos + 1; // BUG here
		
		while(true) {
			endPos++;
			int ch = getCharacter(endPos);
			if(getCharCategory(ch) == null) {
				continue;
			} else {
				return createErrorToken(endPos, DeeParserMessages.INVALID_TOKEN);
			}
		}
	}
	
	public ErrorToken createErrorToken(int endPos, String message) {
		return new Token.ErrorToken(source, tokenStartPos, endPos, message);
	}
	
	protected Token matchEOFCharacter() {
		assertTrue(charRuleCategory[lookAheadAscii()] == DeeRuleSelection.EOF);
		pos++;
		return createToken(DeeTokens.EOF);
	}
	
	protected Token matchEOL() {
		assertTrue(charRuleCategory[lookAheadAscii()] == DeeRuleSelection.EOL);
		if(lookAheadAscii() == 0x0D && lookAhead(1) == 0x0A) {
			pos += 2;
		} else {
			pos += 1;
		}
		return createToken(DeeTokens.EOL);
	}
	
	protected Token matchWhiteSpace() {
		assertTrue(charRuleCategory[lookAheadAscii()] == DeeRuleSelection.WHITESPACE);
		
		while(true) {
			pos++;
			int ch = lookAhead();
			if(getCharCategory(ch) == DeeRuleSelection.WHITESPACE) {
				continue;
			} else {
				return createToken(DeeTokens.WHITESPACE);
			}
		}
	}
	
	protected static final String[] SEEKUNTIL_MULTICOMMENTS = { "+/", "/+" };
	protected static final String[] SEEKUNTIL_NLS = { "\r", "\n" };
	
	protected Token matchSlashRules() {
		assertTrue(charRuleCategory[lookAheadAscii()] == DeeRuleSelection.SLASH);
		
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
			seekToNewline();
			// Note that EOF is also a valid terminator for this comment
			return createToken(DeeTokens.COMMENT_LINE);
		} else {
			return createToken(DeeTokens.DIV);
		}
	}
	
	public final void seekToNewline() {
		int result = seekUntil(SEEKUNTIL_NLS);
		if(result == 0) { // "\r"
			if(lookAhead() == '\n') {
				pos++;
			}
		}
	}
	
	protected Token matchAlphaStartRules() {
		assertTrue(charRuleCategory[lookAheadAscii()].canBeIdentifierPart());
		assertTrue(charRuleCategory[lookAheadAscii()] != DeeRuleSelection.DIGIT);
		
		while(true) {
			pos++;
			int ch = lookAhead();
			
			DeeRuleSelection charCategory = getCharCategory(ch);
			if(charCategory != null && charCategory.canBeIdentifierPart()) {
				continue;
			}
			
			return createToken(DeeTokens.IDENTIFIER);
		}
	}
	
	protected Token matchWYSIWYGString() {
		assertTrue(charRuleCategory[lookAheadAscii()] == DeeRuleSelection.STRING_ALTWYSIWYG);
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
	
	protected Token matchRRules() {
		assertTrue(charRuleCategory[lookAheadAscii()] == DeeRuleSelection.ALPHA_R);
		
		if(lookAhead(1) == '"') {
			pos++; 
			return matchVerbatimString('"', DeeTokens.STRING_WYSIWYG);
		}
		return matchAlphaStartRules(); 
	}
	
	
	protected Token matchString() {
		assertTrue(charRuleCategory[lookAheadAscii()] == DeeRuleSelection.STRING_DOUBLE_QUOTES);
		
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
	
	protected Token matchHRules() {
		assertTrue(charRuleCategory[lookAheadAscii()] == DeeRuleSelection.ALPHA_H);
		
		if(lookAhead(1) == '"') {
			pos++; 
			return matchVerbatimString('"', DeeTokens.STRING_HEX);
		} else {
			return matchAlphaStartRules();
		}
	}
	
	protected Token matchQRules() {
		// TODO q string
		if(lookAhead(1) == '"') {
		} else if(lookAhead(1) == '{') {
		} else {
			
		}
		return matchAlphaStartRules(); 
	}
	
	protected Token matchDigitRules() {
		assertTrue(charRuleCategory[lookAheadAscii()] == DeeRuleSelection.DIGIT);
		
		while(true) {
			pos++;
			
			int ch = lookAhead();
			
			if(getCharCategory(ch) == DeeRuleSelection.DIGIT) {
				continue;
			}
			
			return createToken(DeeTokens.INTEGER);
		}
	}
	
}