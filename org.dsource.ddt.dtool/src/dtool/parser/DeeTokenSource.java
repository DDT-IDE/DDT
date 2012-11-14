package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;

import java.util.Arrays;

import dtool.parser.Token.ErrorToken;


public class DeeTokenSource extends CommonTokenSource {
	
	public DeeTokenSource(CharSequence source) {
		super(source);
		
		scanBegginning();
	}

	protected void scanBegginning() {
		// TODO: UTF BOM's
		
		int ch = getInput(tokenStartPos);
		
		if(ch == '#' && getLA(1) == '!') {
			// TODO ignore script line
		}
		return;
	}
	
	protected static final DeeTokens[] charRuleCategory;
	
	static {
		charRuleCategory = new DeeTokens[ASCII_LIMIT];
		
		charRuleCategory[0x00] = DeeTokens.EOF;
		charRuleCategory[0x1A] = DeeTokens.EOF;
		
		charRuleCategory[0x0D] = DeeTokens.EOL;
		charRuleCategory[0x0A] = DeeTokens.EOL;
		
		charRuleCategory[0x20] = DeeTokens.WHITESPACE;
		charRuleCategory[0x09] = DeeTokens.WHITESPACE;
		charRuleCategory[0x0B] = DeeTokens.WHITESPACE;
		charRuleCategory[0x0C] = DeeTokens.WHITESPACE;
		
		charRuleCategory['/'] = DeeTokens.DIV_X;
		
		Arrays.fill(charRuleCategory, '0', '9'+1, DeeTokens.INTEGER);
		Arrays.fill(charRuleCategory, 'a', 'z'+1, DeeTokens.IDENTIFIER);
		Arrays.fill(charRuleCategory, 'A', 'X'+1, DeeTokens.IDENTIFIER);
		charRuleCategory['_'] = DeeTokens.IDENTIFIER;
		
	}
	
	protected Token createToken(DeeTokens tokenCode, int endPos) {
		return new Token(tokenCode, source, pos, endPos);
	}
	
	protected Token createToken2(DeeTokens tokenCode) {
		return new Token(tokenCode, source, tokenStartPos, pos);
	}
	
	@Override
	protected Token parseToken() {
		pos = tokenStartPos;
		if(pos >= source.length()) {
			return createToken(DeeTokens.EOF, pos);
		}
		
		char ch = source.charAt(pos);
		
		DeeTokens ruleCategory = getCharCategory(ch);
		if(ruleCategory == null) {
			return matchError();
		}
		
		switch (ruleCategory) {
		case EOF: return matchEOFCharacter();
		case EOL: return matchEOL();
		case WHITESPACE: return matchWhiteSpace();
		case DIV_X: return matchSlashCharacter();
		case INTEGER: return matchInteger();
		case IDENTIFIER: return matchIdentifier_Like();
		case ERROR: assertFail();
		case COMMENT: assertFail();
		default:
			throw assertUnreachable();
		}
	}
	
	public DeeTokens getCharCategory(int ch) {
		if(ch == EOF) {
			return DeeTokens.EOF;
		}
		if(ch > ASCII_LIMIT) {
			// BM: Hum I'm not sure this Unicode handling is correct according to D.
			if(Character.isLowSurrogate((char) ch) || Character.isHighSurrogate((char) ch)
				|| Character.isUnicodeIdentifierPart(ch)
			) {
				return DeeTokens.IDENTIFIER;
			}
		}
		return charRuleCategory[ch];	
	}
	
	protected Token matchError() {
		int endPos = pos + 1; // BUG here
		
		while(true) {
			endPos++;
			int ch = getInput(endPos);
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
		assertTrue(charRuleCategory[getAsciiLA()] == DeeTokens.EOL);
		int endPos = pos + 1;
		return createToken(DeeTokens.EOL, endPos);
	}
	
	protected Token matchEOL() {
		assertTrue(charRuleCategory[getAsciiLA()] == DeeTokens.EOL);
		int endPos = pos + 1;
		if(getAsciiLA() == 0x0D && getLA(1) == 0x0A) {
			endPos++;
		}
		return createToken(DeeTokens.EOL, endPos);
	}
	
	protected Token matchWhiteSpace() {
		assertTrue(charRuleCategory[getAsciiLA()] == DeeTokens.WHITESPACE);
		
		int endPos = pos;
		while(true) {
			endPos++;
			int ch = getInput(endPos);
			if(getCharCategory(ch) == DeeTokens.WHITESPACE) {
				continue;
			} else {
				return createToken(DeeTokens.WHITESPACE, endPos);
			}
		}
	}
	
	protected static final String[] SEEKUNTIL_MULTICOMMENTS = { "+/", "/+" };
	protected static final String[] SEEKUNTIL_NLS = { "\r", "\n" };
	
	protected Token matchSlashCharacter() {
		assertTrue(charRuleCategory[getAsciiLA()] == DeeTokens.DIV_X);
		
		pos++;
		
		if(getLA() == '*') {
			//BUG here //pos++;
			int result = seekUntil("*/");
			if(result == 0) {
				return createToken2(DeeTokens.COMMENT);
			} else {
				return createErrorToken(pos, DeeParserMessages.COMMENT_NOT_TERMINATED);
			}
		} else if(getLA() == '+') { //BUG here
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
			
		} else if(getLA() == '/') {
			//BUG here //pos++;
			int result = seekUntil(SEEKUNTIL_NLS);
			if(result == 0) {
				if(getLA() == '\n') {
					pos++;
				}
			}
			// Note that EOF is also a valid terminator for this comment
			return createToken2(DeeTokens.COMMENT);
		} else {
			return createToken(DeeTokens.DIV_X, pos); // BUG here
		}
	}
	
	protected Token matchIdentifier_Like() {
		assertTrue(charRuleCategory[getAsciiLA()] == DeeTokens.IDENTIFIER);
		
		int endPos = pos;
		while(true) {
			endPos++;
			int ch = getInput(endPos);
			
			DeeTokens charCategory = getCharCategory(ch);
			if(charCategory == DeeTokens.IDENTIFIER || charCategory == DeeTokens.INTEGER) {
				continue;
			}
			
			return createToken(DeeTokens.IDENTIFIER, endPos);
		}
	}
	
	protected Token matchInteger() {
		assertTrue(charRuleCategory[getAsciiLA()] == DeeTokens.INTEGER);
		
		int endPos = pos;
		while(true) {
			endPos++;
			
			int ch = getInput(endPos);
			
			if(getCharCategory(ch) == DeeTokens.INTEGER) {
				continue;
			}
			
			return createToken(DeeTokens.INTEGER, endPos);
		}
	}
	
}