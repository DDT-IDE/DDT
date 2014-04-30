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

import dtool.parser.AbstractParser.ParseHelper;
import dtool.tests.DToolBaseTest;


public class DeeParserAdditionalTests extends DToolBaseTest {
	
	@Test
	public void testArgList() throws Exception { testArgList$(); }
	public void testArgList$() throws Exception {
		assertTrue(parseArgumentList("(").ruleBroken);
		assertTrue(parseArgumentList("()").ruleBroken == false);
	}
	
	public ParseHelper parseArgumentList(String source) {
		DeeParser parser = new DeeParser(source);
		ParseHelper parse = parser.new ParseHelper();
		parser.parseParenthesesDelimited_ExpArgumentList(parse);;
		return parse;
	}
	
}