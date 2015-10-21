/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *     Konstantin Salikhov - test cases for lexing rule
 *******************************************************************************/
package dtool.parser;


import melnorme.lang.tests.NumberRuleTest;
import melnorme.lang.tooling.parser.lexer.ILexingRule;

public class DeeNumberRuleTest extends NumberRuleTest {
	
	@Override
	protected ILexingRule createLexingRule() {
		return new DeeNumberLexingRule();
	}
	
	@Override
	protected void testInteger() {
		super.testInteger();
		
		testRule("12L", 3);
	}
	
	@Override
	protected void testFloats() {
		super.testFloats();
		
		testRule("123.1F", 6);
	}
	
	@Override
	protected void testFractionalPartIfLiteralHasRadixPrefix() {
		testRule("0o0.11", 6);
		testRule("0b0.11", 6);
		testRule("0x0.11", 6);
	}
	
}