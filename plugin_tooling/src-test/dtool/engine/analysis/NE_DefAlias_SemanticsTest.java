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
package dtool.engine.analysis;

import static dtool.engine.analysis.NE_LanguageIntrinsics_SemanticsTest.INT_PROPERTIES;
import melnorme.lang.tooling.engine.ErrorElement.SyntaxErrorElement;



public class NE_DefAlias_SemanticsTest extends NamedElement_CommonTest {
	
	@Override
	public void test_NamedElement________() throws Exception {
		// Test alias to var
		test_NamedElement_Alias(parseNamedElement("int intVar; alias intVar XXX; "), 
			"int intVar;", "$/int", INT_PROPERTIES);
		test_NamedElement_Alias(parseNamedElement("int intVar; alias XXX = intVar; "), 
			"int intVar;", "$/int", INT_PROPERTIES);

		// broken variant
		String NOT_FOUND__target = expectNotFound("target");
		test_NamedElement_Alias(parseNamedElement("alias target XXX; "), 
			NOT_FOUND__target, NOT_FOUND__target, NO_MEMBERS);
		test_NamedElement_Alias(parseNamedElement("alias XXX = target; "), 
			NOT_FOUND__target, NOT_FOUND__target, NO_MEMBERS);
		
		test_NamedElement_Alias(parseNamedElement("alias XXX = zzz; alias zzz = target"), 
			NOT_FOUND__target, NOT_FOUND__target, NO_MEMBERS);
		
		test_NamedElement_Alias(parseNamedElement("alias XXX = "), 
			SyntaxErrorElement.SYNTAX_ERROR__Name, expectNotAValue(SyntaxErrorElement.SYNTAX_ERROR__Name), NO_MEMBERS);
		test_NamedElement_Alias(parseNamedElement("alias XXX"), 
			SyntaxErrorElement.SYNTAX_ERROR__Name, expectNotAValue(SyntaxErrorElement.SYNTAX_ERROR__Name), NO_MEMBERS);
		
		// Test alias to type
		test_NamedElement_Alias(parseNamedElement("alias int XXX; "), 
			"$/int", expectNotAValue("int"), INT_PROPERTIES);
		test_NamedElement_Alias(parseNamedElement("alias XXX = int; "), 
			"$/int", expectNotAValue("int"), INT_PROPERTIES);
	}
	
}