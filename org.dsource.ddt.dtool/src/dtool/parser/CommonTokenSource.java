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

public abstract class CommonTokenSource {
	
	protected static final int EOF = -1;
	
	protected static final short ASCII_LIMIT = 127;

	protected final CharSequence source;
	protected int tokenStartPos = 0;
	protected int pos = -1;
	
	public CommonTokenSource(CharSequence source) {
		// Need to investigate how UTF chars are presented to us.
		this.source = source;
	}
	
	public Token next() { 
		Token token = parseToken();
		assertNotNull(token);
		
		tokenStartPos = token.getEndPos();
		return token;
	}
	
	/** Gets the character from absolute position index. */
	protected final int getCharacter(int index) {
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
	
	protected final int lookAheadAscii() {
		int input = getCharacter(pos);
		assertTrue(input >= 0 && input <= ASCII_LIMIT);
		return input;
	}
	
	protected abstract Token parseToken();
	
	/* ------------------------ Helpers ------------------------ */
	
	public final Token rule3Choices(char ch1, DeeTokens tk1, char ch2, DeeTokens tk2, DeeTokens tokenElse) {
		if(lookAhead(1) == ch1) {
			return new Token(tk1, source, tokenStartPos, pos+2);
		} else if(lookAhead(1) == ch2) {
			return new Token(tk2, source, tokenStartPos, pos+2);
		} else {
			return new Token(tokenElse, source, tokenStartPos, pos+1);
		}
	}
	
	public final Token rule2Choices(char ch1, DeeTokens tk1, DeeTokens tokenElse) {
		if(lookAhead(1) == ch1) {
			return new Token(tk1, source, tokenStartPos, pos+2);
		} else {
			return new Token(tokenElse, source, tokenStartPos, pos+1);
		}
	}
	
	
	/** Advance position until any of given strings is found, or input reaches EOF.
	 * Returns the index in given strings array of the matched string (position is advanced to end of string),
	 * or -1 if EOF was encountered (position is advanced to EOF).
	 * If input can match more than one string, priority is given to string with lowest index in given strings,
	 * so ordering is important. */
	public final int seekUntil(final String[] strings) {
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
	/** Optimization of {@link #seekUntil(String[])} method for 1 String */
	public final int seekUntil(String string) {
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
	/** Optimization of {@link #seekUntil(String[])} method for 1 char */
	public final int seekUntil(char endChar) {
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
	/** Optimization of {@link #seekUntil(String[])} method for 2 char */
	public final int seekUntil(char endChar1, char endChar2) {
		while(true) {
			int ch = lookAhead();
			if(ch == -1) {
				return -1;
			}
			pos++;
			if(ch == endChar1) {
				return 0;
			} else if(ch == endChar2) {
				return 1;
			} 
		}
	}
	
	static { assertTrue( ((int)-1) != ((char)-1) ); } // inputMatchesSequence relies on this
	
	/** Returns true if the sequence from current position matches given string. */
	public final boolean inputMatchesSequence(CharSequence string) {
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
	public final boolean inputMatchesSequence(String string) {
		int length = string.length();
		for (int i = 0; i < length; i++) {
			int ch = lookAhead(i);
			if(ch != string.charAt(i)) {
				return false;
			}
		}
		return true;
	}
	
	/*---------------------------------------*/
	
	public final int seekToNewline() {
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
	
}