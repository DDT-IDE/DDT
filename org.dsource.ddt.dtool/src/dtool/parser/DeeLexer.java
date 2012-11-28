/*******************************************************************************
 * Copyright (c) 2012, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/

package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
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
		
		ALPHA(true, true),
		DIGIT(false, true),
		
		QUESTION, COMMA, SEMICOLON, COLON, DOLLAR, AT,
		
		MINUS, PLUS, STAR, SLASH, MOD,
		
		AMPERSAND, VBAR, CARET, EQUAL, TILDE,
		DOT,
		
		LESS_THAN,
		GREATER_THAN,
		EXCLAMATION,
		
		SINGLE_QUOTES,
		
		GRAVE_ACCENT,
		ALPHA_R(true, true),
		DOUBLE_QUOTES,
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
		
		Arrays.fill(startRuleDecider, '0', '9'+1, DeeRuleSelection.DIGIT);
		Arrays.fill(startRuleDecider, 'a', 'z'+1, DeeRuleSelection.ALPHA);
		Arrays.fill(startRuleDecider, 'A', 'Z'+1, DeeRuleSelection.ALPHA);
		startRuleDecider['_'] = DeeRuleSelection.ALPHA;
		
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
		
		startRuleDecider['\''] = DeeRuleSelection.SINGLE_QUOTES;
		
		startRuleDecider['`'] = DeeRuleSelection.GRAVE_ACCENT;
		startRuleDecider['r'] = DeeRuleSelection.ALPHA_R;
		startRuleDecider['"'] = DeeRuleSelection.DOUBLE_QUOTES;
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
		
		case GRAVE_ACCENT: return matchWYSIWYGString();
		case ALPHA_R: return ruleRStart();
		case DOUBLE_QUOTES: return matchString();
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
		
		case SINGLE_QUOTES: return matchCharacterLiteral();
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
				return createErrorToken(DeeParserMessages.INVALID_TOKEN);
			}
		}
	}
	
	public ErrorToken createErrorToken(String message) {
		return new Token.ErrorToken(source, tokenStartPos, pos, message);
	}
	
	public boolean consumeRuleCategoryChar(DeeRuleSelection ruleCategory) {
		DeeRuleSelection lexingDecision = getLexingDecision(lookAhead());
		if(lexingDecision == ruleCategory) {
			pos++;
			return true;
		}
		return false;
	}
	
	public int consumeRuleCategoryChars(DeeRuleSelection ruleCategory) {
		int count = 0;
		while(true) {
			DeeRuleSelection lexingDecision = getLexingDecision(lookAhead());
			if(lexingDecision == ruleCategory) {
				pos++;
				count++;
				continue;
			}
			return count;
		}
	}
	
	protected Token matchEOFCharacter() {
		assertTrue(startRuleDecider[lookAheadAscii()] == DeeRuleSelection.EOF_CHARS);
		return createToken(DeeTokens.EOF, 1);
	}
	
	protected Token matchEOL() {
		assertTrue(startRuleDecider[lookAheadAscii()] == DeeRuleSelection.EOL);
		if(lookAhead() == '\r' && lookAhead(1) == '\n') {
			pos += 2;
		} else {
			pos += 1;
		}
		return createToken(DeeTokens.EOL);
	}
	
	public final void readNewline() {
		int result = readNewlineOrEOF();
		assertTrue(result == 0);
	}
	
	public final int readNewlineOrEOF() {
		int ch = lookAhead();
		if(ch == '\r') {
			pos++;
			if(lookAhead() == '\n') {
				pos++;
			}
			return 0;
		} else if(ch == '\n') {
			pos++;
			return 0;
		} else if(ch == EOF){
			return 1;
		} else {
			return -1;
		}
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
			return ruleHashTokens();
		}
	}
	
	protected Token ruleAlphaStart() {
		assertTrue(getLexingDecision(lookAhead()).canBeIdentifierStart);
		
		// Note, according to D spec, not all non-ASCII characters are valid as identifier characters
		// but for simplification we ignore that for lexing. 
		// Perhaps this can be analized later in a lexing semantics phase.
		boolean asciiOnly = seekIdentifierPartChars();
		if(!asciiOnly) {
			return createToken(DeeTokens.IDENTIFIER);
		}
		String idValue = source.subSequence(tokenStartPos, pos).toString();
		DeeTokens keywordToken = DeeLexerKeywordHelper.getKeywordToken(idValue);
		if(keywordToken != null) {
			return createToken(keywordToken);
		}
		return createToken(DeeTokens.IDENTIFIER);
	}
	
	/** Seek position until lookahead is not valid identifier part */
	public boolean seekIdentifierPartChars() {
		boolean asciiOnly = true;
		do {
			int ch = lookAhead();
			DeeRuleSelection charCategory = getLexingDecision(ch);
			if(!charCategory.canBeIdentifierPart) {
				break;
			}
			if(ch > ASCII_LIMIT) {
				asciiOnly = false;
			}
			pos++;
		} while(true);
		return asciiOnly;
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
				return createErrorToken(DeeParserMessages.COMMENT_NOT_TERMINATED);
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
					return createErrorToken(DeeParserMessages.COMMENTNESTED_NOT_TERMINATED);
				}
			} while (nestingLevel > 0);
			return createToken(DeeTokens.COMMENT_NESTED);
			
		} else if(lookAhead() == '/') {
			pos++;
			seekToNewlineOrEOFCharsRule();
			// Note that EOF is also a valid terminator for this comment
			return createToken(DeeTokens.COMMENT_LINE);
		} else if(lookAhead() == '=') {
			pos++;
			return createToken(DeeTokens.DIV_ASSIGN);
		} else {
			return createToken(DeeTokens.DIV);
		}
	}
	
	public final void seekToNewlineOrEOFCharsRule() {
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
		assertTrue(startRuleDecider[lookAheadAscii()] == DeeRuleSelection.GRAVE_ACCENT);
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
			return createErrorToken(DeeParserMessages.COMMENTNESTED_NOT_TERMINATED);
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
		assertTrue(startRuleDecider[lookAheadAscii()] == DeeRuleSelection.DOUBLE_QUOTES);
		
		pos++;
		while(true) {
			int ch = lookAhead();
			
			if(ch == '"') {
				pos++;
				ruleStringPostFix();
				return createToken(DeeTokens.STRING_DQ);
			} else if (ch == EOF) {
				// TODO , maybe recover using EOL?
				return createErrorToken(DeeParserMessages.STRING_NOT_TERMINATED);
			} else if (ch == '\\') {
				if (lookAhead(1) == '"' || lookAhead(1) == '\\') {
					pos += 2;
					continue;
				}
				// We ignore the other escape sequences rules since they are not important for lexing
				// see http://dlang.org/lex.html#EscapeSequence
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
		case EOF: return createErrorToken(DeeParserMessages.STRING_DELIM_NO_DELIMETER);
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
				return createErrorToken(DeeParserMessages.STRING_NOT_TERMINATED__REACHED_EOF);
			}
		} while (nestingLevel > 0);
		
		if(lookAhead() == '"') {
			pos++;
			return createToken(DeeTokens.STRING_DELIM);
		} else {
			seekUntil('"');
			return createErrorToken(DeeParserMessages.STRING_DELIM_NOT_PROPERLY_TERMINATED);
		}
	}
	
	public Token matchHereDocDelimString() {
		int idStartPos = pos;
		pos++;
		seekIdentifierPartChars();
		String hereDocId = source.subSequence(idStartPos, pos).toString(); // Optimization note: allocation here
		
		if(getLexingDecision(lookAhead()) != DeeRuleSelection.EOL) {
			seekHereDocEndDelim(hereDocId);
			return createErrorToken(DeeParserMessages.STRING_DELIM_ID_NOT_PROPERLY_FORMED);
		}
		
		int result = seekHereDocEndDelim(hereDocId);
		if(result == -1) {
			return createErrorToken(DeeParserMessages.STRING_NOT_TERMINATED__REACHED_EOF);
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
				return createErrorToken(DeeParserMessages.STRING_NOT_TERMINATED__REACHED_EOF);
			}
		} while(nestingLevel > 0);
		
		tokenStartPos = tokenStringStartPos;
		return createToken(DeeTokens.STRING_TOKENS);
	}
	
	protected Token matchCharacterLiteral() {
		pos++;
		while(true) {
			int ch = lookAhead();
			DeeRuleSelection charCategory = getLexingDecision(ch);
			
			if(ch == '\'') {
				pos++;
				if(pos == tokenStartPos + 2) {
					return createErrorToken(DeeParserMessages.CHAR_LITERAL_EMPTY);
				}
				
				return createToken(DeeTokens.CHAR_LITERAL);
			} else if (charCategory == DeeRuleSelection.EOF) {
				return createErrorToken(DeeParserMessages.CHAR_LITERAL_NOT_TERMINATED__REACHED_EOF);
			} else if (charCategory == DeeRuleSelection.EOL) {
				seekToNewline();
				return createErrorToken(DeeParserMessages.CHAR_LITERAL_NOT_TERMINATED__REACHED_EOL);
			} else if (ch == '\\') {
				if (lookAhead(1) == '\'' || lookAhead(1) == '\\') {
					pos += 2;
					continue;
				} else {
					// Again, we ignore the other escape sequence rules
				}
			}
			pos++;
		}
	}
	
	protected static enum EInt_Literal_Type  {
		BINARY, OCTAL, DECIMAL, HEX
	}
	
	protected Token matchDigitRules() {
		assertTrue(startRuleDecider[lookAheadAscii()] == DeeRuleSelection.DIGIT);
		
		EInt_Literal_Type literalType = EInt_Literal_Type.DECIMAL;
		boolean invalidDigitFound = false;
		boolean hasAtLeastOneDigit = true;
		int maxDigitChar = '9';

		int firstChar = lookAhead();
		
		
		if(firstChar == '0') {
			if(lookAhead(1) == 'x' || lookAhead(1) == 'X') {
				pos++;
				literalType = EInt_Literal_Type.HEX;
				hasAtLeastOneDigit = false;
			} else if(lookAhead(1) == 'b' || lookAhead(1) == 'B') {
				pos++;
				literalType = EInt_Literal_Type.BINARY;
				maxDigitChar = '1';
				hasAtLeastOneDigit = false;
			} else {
				literalType = EInt_Literal_Type.OCTAL;
				maxDigitChar = '7';
			}
		}
		
		
		while(true) {
			pos++;
			
			int ch = lookAhead();
			
			if(getLexingDecision(ch) == DeeRuleSelection.DIGIT) {
				hasAtLeastOneDigit = true;
				if(ch > maxDigitChar) {
					invalidDigitFound = true;
				}
				continue;
			}
			if(ch == '_') {
				continue;
			}
			if(literalType == EInt_Literal_Type.HEX && isHexDigit(ch)) {
				hasAtLeastOneDigit = true;
				continue;
			}
			
			break;
		}
		
		if(literalType == EInt_Literal_Type.OCTAL && pos == tokenStartPos + 1) {
			literalType = EInt_Literal_Type.DECIMAL; // Zero literal is a decimal literal.
		}
		
		boolean hasIntegerSuffix = readIntegerSuffix();
		
		if(literalType != EInt_Literal_Type.OCTAL && literalType != EInt_Literal_Type.BINARY 
			&& hasIntegerSuffix == false) {
			
			boolean isHex = literalType == EInt_Literal_Type.HEX;
			int ch = lookAhead();
			if(ch == '.') {
				return matchFloatLiteralFromDecimalMark(isHex);
			}
			if(ch == 'f' || ch == 'F' || ch == 'L' || ch == 'i' 
				|| (isHex && (ch == 'P' || ch == 'p'))
				|| (!isHex && (ch == 'E' || ch == 'e'))
				) {
				return matchFloatLiteralAfterFractionalPart(isHex, false);
			}
		}
		
		switch (literalType) {
		case BINARY: return createIntegerToken(DeeTokens.INTEGER_BINARY, invalidDigitFound, hasAtLeastOneDigit);
		case OCTAL: return createIntegerToken(DeeTokens.INTEGER_OCTAL, invalidDigitFound, hasAtLeastOneDigit);
		case DECIMAL: return createToken(DeeTokens.INTEGER);
		case HEX: return createIntegerToken(DeeTokens.INTEGER_HEX, false, hasAtLeastOneDigit);
		}
		throw assertUnreachable();
	}
	
	public Token createIntegerToken(DeeTokens deeToken, boolean invalidDigitFound, boolean hasAtLeastOneDigit) {
		if(!hasAtLeastOneDigit) {
			return createErrorToken(DeeParserMessages.INT_LITERAL__HAS_NO_DIGITS);
		}
		if(invalidDigitFound) {
			return createErrorToken(deeToken == DeeTokens.INTEGER_BINARY ? 
				DeeParserMessages.INT_LITERAL_BINARY__INVALID_DIGITS :
				DeeParserMessages.INT_LITERAL_OCTAL__INVALID_DIGITS
				);
		}
		return createToken(deeToken);
	}
	
	public boolean readIntegerSuffix() {
		int ch = lookAhead();
		if(ch == 'L') {
			pos++;
			if(lookAhead() == 'u' || lookAhead() == 'U') {
				pos++;
			}
			return true;
			
		} else if(ch == 'u' || ch == 'U') {
			pos++;
			if(lookAhead() == 'L') {
				pos++;
			}
			return true;
		}
		return false;
	}
	
	public static boolean isHexDigit(int ch) {
		return (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
	}
	
	public Token matchFloatLiteralFromDecimalMark(boolean isHex) {
		boolean precedingCharIsDot = true;
		while(true) {
			pos++;
			
			int ch = lookAhead();
			
			if(getLexingDecision(ch) == DeeRuleSelection.DIGIT) {
				precedingCharIsDot = false;
				continue;
			}
			if(isHex && isHexDigit(ch)) {
				precedingCharIsDot = false;
				continue;
			}
			if((isHex || !precedingCharIsDot) && ch == '_') {  
				precedingCharIsDot = false;
				continue;
			}
			
			break;
		}
		
		return matchFloatLiteralAfterFractionalPart(isHex, precedingCharIsDot);
	}
	
	public Token matchFloatLiteralAfterFractionalPart(boolean isHex, boolean precedingCharIsDot) {
		boolean exponentHasDigits = true;
		boolean hasExponent = false;
		
		int ch = lookAhead();
		if(	( isHex && (ch == 'P' || ch == 'p')) ||
			(!isHex && (ch == 'E' || ch == 'e') && !precedingCharIsDot)) {
			pos++;
			if(lookAhead() == '+' || lookAhead() == '-') {
				pos++;
			}
			hasExponent = true;
			exponentHasDigits = readDecimalDigitsOrUnderscore();
			precedingCharIsDot = false;
		}
		
		ch = lookAhead();
		if((isHex || !precedingCharIsDot) && (ch == 'f' || ch == 'F' || ch == 'L')) {
			pos++;
		}
		if((isHex || !precedingCharIsDot) && lookAhead() == 'i') {
			pos++;
		}
		
		if(isHex) {
			if(hasExponent == false) {
				return createErrorToken(DeeParserMessages.FLOAT_LITERAL__HEX_HAS_NO_EXP);
			}
			if(!exponentHasDigits) {
				return createErrorToken(DeeParserMessages.FLOAT_LITERAL__EXP_HAS_NO_DIGITS);
			} else {
				return createToken(DeeTokens.FLOAT_HEX);
			}
		} else {
			if(!exponentHasDigits) {
				return createErrorToken(DeeParserMessages.FLOAT_LITERAL__EXP_HAS_NO_DIGITS);
			} else {
				return createToken(DeeTokens.FLOAT);
			}
		}
	}
	
	public final boolean readDecimalDigitsOrUnderscore() {
		boolean hasAtLeastOneDigit = false;
		while(true) {
			int ch = lookAhead();
			
			if(getLexingDecision(ch) == DeeRuleSelection.DIGIT || ch == '_') {
				pos++;
				if(ch != '_') {
					hasAtLeastOneDigit = true;
				}
				continue;
			}
			break;
		}
		return hasAtLeastOneDigit;
	}
	
	
	public final Token ruleDotStart() {
		int lookahead_1 = lookAhead(1);
		if(getLexingDecision(lookahead_1) == DeeRuleSelection.DIGIT) {
			return matchFloatLiteralFromDecimalMark(false);
		}
		
		if(lookahead_1 == '.') {
			if(lookAhead(2) == '.') {
				return createToken(DeeTokens.TRIPLE_DOT, 3);
			}
			return createToken(DeeTokens.DOUBLE_DOT, 2);
		}
		return createToken(DeeTokens.DOT, 1);
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
	
	public Token ruleHashTokens() {
		if(inputMatchesSequence("line") && getLexingDecision(lookAhead(4)) == DeeRuleSelection.WHITESPACE) {
			return matchSpecialTokenLine();
		}
		seekToNewline();
		return createErrorToken(DeeParserMessages.SPECIAL_TOKEN_INVALID);
	}
	
	protected static final String[] SEEKUNTIL_DOUBLEQUOTES_OR_NL = { "\"", "\r\n", "\r", "\n", };
	
	public Token matchSpecialTokenLine() {
		pos+=4;
		
		if(consumeRuleCategoryChars(DeeRuleSelection.WHITESPACE) == 0) {
			assertFail();
		}
		if(consumeRuleCategoryChars(DeeRuleSelection.DIGIT) == 0) {
			seekToNewline();
			return createErrorToken(DeeParserMessages.SPECIAL_TOKEN_LINE_BAD_FORMAT); 
		}
		if(consumeRuleCategoryChars(DeeRuleSelection.WHITESPACE) == 0) {
			// It's ok
		}
		
		if(consumeRuleCategoryChar(DeeRuleSelection.DOUBLE_QUOTES) == false) {
			return matchEndOf_SpecialTokenLine();
		}
		
		if(seekUntil(SEEKUNTIL_DOUBLEQUOTES_OR_NL) != 0) {
			return createErrorToken(DeeParserMessages.SPECIAL_TOKEN_LINE_BAD_FORMAT); 
		}
		
		return matchEndOf_SpecialTokenLine();
	}
	
	public Token matchEndOf_SpecialTokenLine() {
		if(readNewlineOrEOF() == -1) {
			seekToNewline(); // This is not according to DMD I think.
			return createErrorToken(DeeParserMessages.SPECIAL_TOKEN_LINE_BAD_FORMAT); 
		}
		
		return createToken(DeeTokens.SPECIAL_TOKEN_LINE);
	}
	
}