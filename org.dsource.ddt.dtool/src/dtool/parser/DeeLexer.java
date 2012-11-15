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
		
		IDENTIFIER,
		INTEGER,
		;
		
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
		
		Arrays.fill(charRuleCategory, '0', '9'+1, DeeTokens.INTEGER);
		Arrays.fill(charRuleCategory, 'a', 'z'+1, DeeTokens.IDENTIFIER);
		Arrays.fill(charRuleCategory, 'A', 'X'+1, DeeTokens.IDENTIFIER);
		charRuleCategory['_'] = DeeRuleSelection.IDENTIFIER;
		
	}
	
	@Deprecated
	public Token createToken(DeeTokens tokenCode, int endPos) {
		return new Token(tokenCode, source, pos, endPos);
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
		case SLASH: return matchSlashCharacter();
		case INTEGER: return matchInteger();
		case IDENTIFIER: return matchIdentifier_Like();
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
				return DeeRuleSelection.IDENTIFIER;
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
		assertTrue(charRuleCategory[lookAheadAscii()] == DeeRuleSelection.EOL);
		pos++;
		return createToken(DeeTokens.EOL);
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
	
	protected Token matchSlashCharacter() {
		assertTrue(charRuleCategory[lookAheadAscii()] == DeeRuleSelection.SLASH);
		
		pos++;
		
		if(lookAhead() == '*') {
			//BUG here //pos++;
			int result = seekUntil("*/");
			if(result == 0) {
				return createToken(DeeTokens.COMMENT);
			} else {
				return createErrorToken(pos, DeeParserMessages.COMMENT_NOT_TERMINATED);
			}
		} else if(lookAhead() == '+') { //BUG here
			//BUG here //pos++;
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
			return createToken(DeeTokens.COMMENT, pos); // BUG here
			
		} else if(lookAhead() == '/') {
			//BUG here //pos++;
			seekToNewline();
			// Note that EOF is also a valid terminator for this comment
			return createToken(DeeTokens.COMMENT);
		} else {
			return createToken(DeeTokens.DIV_X, pos); // BUG here
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
	
	protected Token matchIdentifier_Like() {
		assertTrue(charRuleCategory[lookAheadAscii()] == DeeRuleSelection.IDENTIFIER);
		
		while(true) {
			pos++;
			int ch = lookAhead();
			
			DeeRuleSelection charCategory = getCharCategory(ch);
			if(charCategory == DeeRuleSelection.IDENTIFIER || charCategory == DeeRuleSelection.INTEGER) {
				continue;
			}
			
			return createToken(DeeTokens.IDENTIFIER);
		}
	}
	
	protected Token matchInteger() {
		assertTrue(charRuleCategory[lookAheadAscii()] == DeeRuleSelection.INTEGER);
		
		while(true) {
			pos++;
			
			int ch = lookAhead();
			
			if(getCharCategory(ch) == DeeRuleSelection.INTEGER) {
				continue;
			}
			
			return createToken(DeeTokens.INTEGER);
		}
	}
	
}