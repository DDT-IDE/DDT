/*******************************************************************************
 * Copyright (c) 2012, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.parser.common;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.parser.DeeLexer;
import dtool.parser.DeeTokens;
import dtool.parser.LexerErrorTypes;
import dtool.parser.Token;
import dtool.tests.CommonDToolTest;

public class AbstractLexerTest extends CommonDToolTest {
	
	public AbstractLexerTest() {
	}
	
	public static DeeLexer testLexerTokenizing(String source, DeeTokens... deeTokens) {
		return testLexerTokenizing(source, tokenCheckers(deeTokens));
	}
	
	public static TokenChecker[] tokenCheckers(DeeTokens... deeTokens) {
		TokenChecker[] tokenCheckers = new TokenChecker[deeTokens.length];
		for (int i = 0; i < deeTokens.length; i++) {
			tokenCheckers[i] = new TokenChecker(deeTokens[i]);
		}
		return tokenCheckers;
	}
	
	public static DeeLexer testLexerTokenizing(String source, TokenChecker... tokenCheckers) {
		DeeLexer deeLexer = new DeeTestsLexer(source);
		testLexerTokenizing(deeLexer, 0, tokenCheckers);
		return deeLexer;
	}
	public static void testLexerTokenizing(DeeLexer deeLexer, int readSourceOffset, TokenChecker... tokenCheckers) {
		String source = deeLexer.source;
		
		StringBuilder constructedSource = new StringBuilder(deeLexer.source.substring(0, readSourceOffset));
		for (int i = 0; i < tokenCheckers.length; i++) {
			TokenChecker tokenChecker = tokenCheckers[i];
			Token token = tokenChecker.checkToken(deeLexer, readSourceOffset);
			readSourceOffset = token.getEndPos();
			String sourceSoFar = source.substring(0, readSourceOffset);
			
			String tokenSourceValue = token.getSourceValue();
			// retest with just the token source to make sure boundaries are correct
			if(tokenCheckers.length != 1 && !tokenSourceValue.startsWith("#!")) {
				testLexerTokenizing(tokenSourceValue, array(tokenChecker));
			}
			
			constructedSource.append(tokenSourceValue);
			assertTrue(sourceSoFar.contentEquals(constructedSource));
		}
		assertTrue(deeLexer.pos == source.length());
		new TokenChecker(DeeTokens.EOF).checkToken(deeLexer, readSourceOffset);
		assertTrue(deeLexer.tokenStartPos == source.length());
		assertEquals(source, constructedSource.toString());
	}
	
	protected static final class DeeTestsLexer extends DeeLexer {
		public DeeTestsLexer(String source) {
			super(source);
		}
		
		@Override
		public String toString() {
			return source.substring(0, pos) + "<---parser--->" + source.substring(pos, source.length());
		}
	}
	
	public static class TokenChecker {
		
		private DeeTokens expectedTokenType;
		private LexerErrorTypes expectedError;
		
		public TokenChecker(DeeTokens deeToken) {
			this(deeToken, null);
		}
		
		public TokenChecker(DeeTokens tokenType, LexerErrorTypes error) {
			this.expectedTokenType = tokenType;
			this.expectedError = error;
			if(tokenType == DeeTokens.INVALID_TOKEN) {
				assertTrue(expectedError == null);
				this.expectedError = LexerErrorTypes.INVALID_CHARACTERS;
			}
		}
		
		public Token checkToken(DeeLexer deeLexer, int readOffset) {
			Token token = deeLexer.next();
			
			if(expectedTokenType != null) {
				assertTrue(token.type == expectedTokenType);
			} else {
				assertTrue(expectedError == null);
			}
			
			assertTrue(token.getError() == expectedError);
			
			assertTrue(token.getStartPos() == readOffset);
			assertEquals(deeLexer.source.subSequence(token.getStartPos(), token.getEndPos()), token.getSourceValue());
			
			DeeTokens tokenCode = token.getType();
			if(tokenCode == DeeTokens.EOF) {
				assertTrue(token.getEndPos() >= token.getStartPos());
			} else {
				assertTrue(token.getEndPos() > token.getStartPos());
			}
			return token;
		}
	}
	
}