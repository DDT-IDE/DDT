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
import melnorme.lang.tooling.ast.SourceRange;

import org.junit.Test;

import dtool.ast.definitions.Module;
import dtool.parser.common.AbstractParser.ParseHelper;
import dtool.tests.CommonDToolTest;


public class DeeParserAdditionalTests extends CommonDToolTest {
	
	@Test
	public void testModule() throws Exception { testModule$(); }
	public void testModule$() throws Exception {
		Module module = new DeeParser("").parseModule("defaultModuleName", null).node;
		assertTrue(module.defname.hasSourceRangeInfo());
		
		module = new DeeParser(" int x;").parseModule("defaultModuleName", null).node;
		assertTrue(module.defname.hasSourceRangeInfo());
		assertTrue(module.defname.getSourceRange().equals(SourceRange.srStartToEnd(0, 0)));
	}
	
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