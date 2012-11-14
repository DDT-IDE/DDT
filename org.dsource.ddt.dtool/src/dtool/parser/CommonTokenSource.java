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
	
	protected final int getInput(int index) {
		if(index >= source.length()) {
			return -1;
		}
		
		return source.charAt(index);
	}
	
	protected final int getLA(int offset) {
		return getInput(pos + offset);
	}
	
	protected final int getLA() {
		return getInput(pos);
	}
	
	protected final int getAsciiLA() {
		int input = getInput(pos);
		assertTrue(input >= 0 && input <= ASCII_LIMIT);
		return input;
	}
	
	protected abstract Token parseToken();

	
	/* ------------------------ Helpers ------------------------ */
	
	/** Advance position until given string is found, or position reaches EOF.
	 * Returns 0 if given string was found (position is advanced to end of string), 
	 * or -1 if EOF was encountered (position is advanced to EOF). */
	public final int seekUntil(String string) {
		while(true) {
			boolean matches = posMatchesSequence(string);
			if(matches) {
				pos += string.length();
				return 0;
			} else if(getLA(0) == -1) {
				return -1;
			} else {
				pos++;
			}
		}
	}
	
	/** Advance position until any of given strings is found, or position reaches EOF.
	 * Returns the index in given strings array of the matched string (position is advanced to end of string), 
	 * or -1 if EOF was encountered (position is advanced to EOF). */
	public final int seekUntil(String[] strings) {
		while(true) {
			int i = 0;
			boolean matchesAny = false;
			for (; i < strings.length; i++) {
				matchesAny = posMatchesSequence(strings[i]);
				if(matchesAny) {
					break;
				}
			}
			if(matchesAny) {
				pos += strings[i].length();
				return i;
			} else if(getLA(0) == -1) {
				return -1;
			} else {
				pos++;
			}
		}
	}
	
	/** Returns true if the sequence from current position matches given str. */
	public final boolean posMatchesSequence(String str) {
		int length = str.length();
		for (int i = 0; i < length; i++) {
			int ch = getLA(i);
			if(ch != str.charAt(i)) {
				return false;
			}
		}
		return true;
	}
	
}