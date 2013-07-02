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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.parser.Token.ErrorToken;

public abstract class AbstractLexer {
	
	protected static final int EOF = -1;
	
	protected static final short ASCII_LIMIT = 127;

	protected final String source;
	protected int tokenStartPos = 0;
	// BM: maybe pos should function param instead of global variable
	protected int pos = tokenStartPos; // Temporary variable used internally by scanners 
	
	public AbstractLexer(String source) {
		this.source = assertNotNull(source);
	}
	
	public final String getSource() {
		return source;
	}
	
	public final int getLexingPosition() {
		return tokenStartPos;
	}
	
	public boolean lookAheadIsEOF() {
		assertTrue(pos == tokenStartPos);
		return (tokenStartPos >= source.length());
	}
	
	public final Token next() { 
		Token token = parseToken();
		assertNotNull(token);
		
		tokenStartPos = token.getEndPos();
		return token;
	}
	
	public abstract Token parseToken();
	
	public final void reset(int newTokenStartPosition) {
		assertTrue(newTokenStartPosition >= 0 && newTokenStartPosition <= source.length());
		tokenStartPos = newTokenStartPosition;
	}
	
	/** Gets the character from absolute position index. */
	public final int getCharacter(int index) {
		if(index >= source.length()) {
			return -1;
		}
		
		return source.charAt(index);
	}
	
	protected final int lookAhead(int offset) {
		return getCharacter(pos + offset);
	}
	
	protected final int lookAhead() {
		return getCharacter(pos);
	}
	
	
	protected final Token createToken(DeeTokens tokenCode) {
		String value = source.substring(tokenStartPos, pos);
		return new Token(tokenCode, value, tokenStartPos);
	}
	protected final Token createToken(DeeTokens tokenCode, int length) {
		pos = tokenStartPos + length;
		return createToken(tokenCode);
	}
	
	protected final ErrorToken createErrorToken(DeeTokens originalToken, LexerErrorTypes errorType) {
		String value = source.substring(tokenStartPos, pos);
		return new Token.ErrorToken(value, tokenStartPos, originalToken, errorType);
	}
	
	/* ------------------------ Helpers ------------------------ */
	
	/** Advance position until any of given strings is found, or input reaches EOF.
	 * Returns the index in given strings array of the matched string (position is advanced to end of string),
	 * or -1 if EOF was encountered (position is advanced to EOF).
	 * If input can match more than one string, priority is given to string with lowest index in given strings,
	 * so ordering is important. */
	protected final int seekTo(final String[] strings) {
		while(true) {
			int i = 0;
			boolean matchesAny = false;
			for (; i < strings.length; i++) {
				matchesAny = inputMatchesSequence(strings[i]);
				if(matchesAny) {
					break;
				}
			}
			if(matchesAny) {
				pos += strings[i].length();
				return i;
			} else if(lookAhead(0) == -1) {
				return -1;
			} else {
				pos++;
			}
		}
	}
	/** Optimization of {@link #seekTo(String[])} method for 1 String */
	protected final int seekTo(String string) {
		while(true) {
			boolean matches = inputMatchesSequence(string);
			if(matches) {
				pos += string.length();
				return 0;
			} else if(lookAhead() == -1) {
				return -1;
			} else {
				pos++;
			}
		}
	}
	/** Optimization of {@link #seekTo(String[])} method for 1 char */
	protected final int seekTo(char endChar) {
		while(true) {
			int ch = lookAhead(0);
			if(ch == -1) {
				return -1;
			}
			pos++; 
			if(ch == endChar) {
				return 0;
			}
		}
	}
	/** Optimization of {@link #seekTo(String[])} method for 2 char */
	protected final int seekTo(char endChar1, char endChar2) {
		while(true) {
			int ch = lookAhead();
			if(ch == EOF) {
				return EOF;
			}
			pos++;
			if(ch == endChar1) {
				return 0;
			} else if(ch == endChar2) {
				return 1;
			} 
		}
	}
	
	protected final int seekToNewline() {
		while(true) {
			int ch = lookAhead();
			if(ch == EOF) {
				return EOF;
			}
			pos++;
			if(ch == '\r') {
				if(lookAhead() == '\n') {
					pos++;
				}
				return 0;
			} else if(ch == '\n') {
				return 0;
			} 
		}
	}
	
	/*---------------------------------------*/
	
	protected final void readNewline() {
		int result = readNewlineOrEOF();
		assertTrue(result == 0);
	}
	
	protected final int readNewlineOrEOF() {
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
	
	/*---------------------------------------*/
	
	static { assertTrue( ((int)-1) != ((char)-1) ); } // inputMatchesSequence relies on this
	
	/** Returns true if the sequence from current position matches given string. */
	protected final boolean inputMatchesSequence(CharSequence string) {
		int length = string.length();
		for (int i = 0; i < length; i++) {
			int ch = lookAhead(i);
			if(ch != string.charAt(i)) {
				return false;
			}
		}
		return true;
	}
	/** Optimization of {@link #inputMatchesSequence(CharSequence)} , since String is final and not an interface */
	protected final boolean inputMatchesSequence(String string) {
		int length = string.length();
		for (int i = 0; i < length; i++) {
			int ch = lookAhead(i);
			if(ch != string.charAt(i)) {
				return false;
			}
		}
		return true;
	}
	
	/* ------------------------  ------------------------ */
	
	protected final Token rule3Choices(char ch1, DeeTokens tk1, char ch2, DeeTokens tk2, DeeTokens tokenElse) {
		if(lookAhead(1) == ch1) {
			return createToken(tk1, 2);
		} else if(lookAhead(1) == ch2) {
			return createToken(tk2, 2);
		} else {
			return createToken(tokenElse, 1);
		}
	}
	
	protected final Token rule2Choices(char ch1, DeeTokens tk1, DeeTokens tokenElse) {
		if(lookAhead(1) == ch1) {
			return createToken(tk1, 2);
		} else {
			return createToken(tokenElse, 1);
		}
	}
	
}