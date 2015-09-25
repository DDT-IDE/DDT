/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
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

import org.junit.Test;

import dtool.parser.DeeLexer;
import dtool.parser.DeeTokens;
import dtool.parser.common.LexerResult.TokenAtOffsetResult;
import dtool.tests.CommonDToolTest;

public class LexResult_Test extends CommonDToolTest {
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		testFindTokenAtOffset("", 0, null, token("", 0));
		testFindTokenAtOffset(";", 0, null, token(";", 0));
		testFindTokenAtOffset(" ", 0, null, token(" ", 0));
		
		testFindTokenAtOffset(";", 1, token(";", 0), token("", 1));
		testFindTokenAtOffset(" ", 1, token("", 1), null);
		
		testFindTokenAtOffset(";", 0, null, token(";", 0));
		testFindTokenAtOffset(";", 1, token(";", 0), token("", 1));
		
		testFindTokenAtOffset(";a", 1, token(";", 0), token("a", 1));
		testFindTokenAtOffset("a;", 1, token("a", 0), token(";", 1));
		
		testFindTokenAtOffset("a ", 1, token("a", 0), token(" ", 1)); // This case could change in the future
	}
	
	protected Token token(String source, int startPos) {
		return new Token(DeeTokens.IDENTIFIER, source, startPos);
	}
	
	protected void testFindTokenAtOffset(String source, int offset, Token expected) {
		testFindTokenAtOffset(source, offset, expected, expected);
	}
	
	protected void testFindTokenAtOffset(String source, int offset, Token expectedLeft, Token expectedRight) {
		LexerResult lexerResult = new LexerResult(source, 
			new LexElementProducer().produceLexTokens(new DeeLexer(source)));
		
		TokenAtOffsetResult tokenAtOffsetResult = lexerResult.findTokenAtOffset(offset);
		if(expectedLeft == expectedRight) {
			assertTrue(tokenAtOffsetResult.atLeft == tokenAtOffsetResult.atRight);
		}
		
		checkToken(tokenAtOffsetResult.atLeft, expectedLeft);
		checkToken(tokenAtOffsetResult.atRight, expectedRight);
	}
	
	protected void checkToken(IToken token, Token expectedToken) {
		if(expectedToken == null) {
			assertTrue(token == null);
		} else {
			assertAreEqual(token.getSourceValue(), expectedToken.getSourceValue());
			assertAreEqual(token.getSourceRange(), expectedToken.getSourceRange());
		}
	}
	
}