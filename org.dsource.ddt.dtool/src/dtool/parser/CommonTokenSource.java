package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

public abstract class CommonTokenSource {
	
	protected static final int EOF = -1;
	
	protected static final short ASCII_LIMIT = 127;

	protected final CharSequence source;
	protected int tokenStartPos = 0;
	protected int pos = -1;
	
	protected Token currentToken = null;
	
	public CommonTokenSource(CharSequence source) {
		// Need to investigate how UTF chars are presented to us.
		this.source = source;
	}
	
	public DeeTokens peek() {
		scanCurrentToken();
		return currentToken.getTokenCode();
	}
	
	public Token next() { 
		scanCurrentToken();
		Token nextToken = currentToken;
		consumeCurrentToken();
		return nextToken;
	}
	
	protected void scanCurrentToken() {
		if(currentToken != null) {
			return;
		}
		currentToken = parseToken();
		assertNotNull(currentToken);
	}
	
	protected void consumeCurrentToken() {
		tokenStartPos = currentToken.getEndPos();
		currentToken = null;
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
	
	/** Advance position until any of given strings is found, or input reaches EOF.
	 * Returns the index in given strings array of the matched string (position is advanced to end of string), 
	 * or -1 if EOF was encountered (position is advanced to EOF). */
	public final int seekUntil(String[] strings) {
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
	
	/** Advance position until given string is found, or input reaches EOF.
	 * Returns 0 if given string was found (position is advanced to end of string), 
	 * or -1 if EOF was encountered (position is advanced to EOF). */
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
	
	// Optimization of previous method
	public final int seekUntil(char endChar) {
		while(true) {
			int ch = lookAhead(0);
			if(ch == endChar) {
				pos++; 
				return 0;
			} else if(ch == -1) {
				return -1;
			}
			pos++;
		}
	}
	
	/** Returns true if the sequence from current position matches given string. */
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
	
}