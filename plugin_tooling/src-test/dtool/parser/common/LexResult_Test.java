/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.parser.common;

import static dtool.ast.SourceRange.srStartToEnd;

import org.junit.Test;

import dtool.ast.SourceRange;
import dtool.parser.DeeLexer;
import dtool.parser.IToken;
import dtool.tests.CommonDToolTest;

public class LexResult_Test extends CommonDToolTest {
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		testFindTokenAtOffset("", 0, "", srStartToEnd(0, 0));
		testFindTokenAtOffset(" ", 0, " ", srStartToEnd(0, 1));
		testFindTokenAtOffset(" ", 1, "", srStartToEnd(1, 1));
		
		testFindTokenAtOffset(";", 0, ";", srStartToEnd(0, 1));
		testFindTokenAtOffset(";", 1, "", srStartToEnd(1, 1));
	}
	
	protected void testFindTokenAtOffset(String source, int offset, String tokenSource, SourceRange sr) {
		LexerResult lexerResult = new LexerResult(source, 
			new LexElementProducer().produceLexTokens(new DeeLexer(source)));
		
		IToken tokenAtZero = lexerResult.findTokenAtOffset(offset);
		assertAreEqual(tokenAtZero.getSourceValue(), tokenSource);
		assertAreEqual(tokenAtZero.getSourceRange(), sr);
	}
	
}