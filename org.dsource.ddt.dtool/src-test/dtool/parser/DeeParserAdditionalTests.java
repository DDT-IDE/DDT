/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import dtool.parser.DeeParserTest.DeeTestsParser;
import dtool.parser.DeeParser_RefOrExp.ArgumentListParseResult;
import dtool.tests.CommonTestUtils;


public class DeeParserAdditionalTests extends CommonTestUtils {
	
	@Test
	public void testArgList() throws Exception { testArgList$(); }
	public void testArgList$() throws Exception {
		assertTrue(parseArgumentList("(").parseBroken == true);
		assertTrue(parseArgumentList("()").parseBroken == false);
	}
	
	public ArgumentListParseResult<?> parseArgumentList(String source) {
		DeeTestsParser parser = new DeeTestsParser(source);
		parser.consumeLookAhead(DeeTokens.OPEN_PARENS);
		return parser.parseExpArgumentList(DeeTokens.CLOSE_PARENS);
	}
	
}