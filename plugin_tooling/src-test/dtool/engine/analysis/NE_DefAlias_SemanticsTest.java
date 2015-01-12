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

import static melnorme.lang.tooling.engine.ErrorElement.NOT_FOUND__Name;
import static melnorme.lang.tooling.engine.resolver.NamedElementSemantics.NotAValueErrorElement.ERROR_IS_NOT_A_VALUE;



public class NE_DefAlias_SemanticsTest extends NamedElement_CommonTest {
	
	@Override
	public void test_resolveElement________() throws Exception {
		// Test alias to var
		test_resolveElement(parseNamedElement("int intVar; alias intVar XXX; "), "intVar", "int", false);
		test_resolveElement(parseNamedElement("int intVar; alias XXX = intVar; "), "intVar", "int", false);
		// broken variant
		test_resolveElement(parseNamedElement("alias intVar XXX; "), NOT_FOUND__Name, NOT_FOUND__Name, true);
		test_resolveElement(parseNamedElement("alias XXX = intVar; "), NOT_FOUND__Name, NOT_FOUND__Name, true);
		
		/* FIXME: do syntax error element */
		test_resolveElement(parseNamedElement("alias XXX = "), NOT_FOUND__Name, NOT_FOUND__Name, true);
		test_resolveElement(parseNamedElement("alias XXX"), NOT_FOUND__Name, NOT_FOUND__Name, true);
		
		// Test alias to type
		test_resolveElement(parseNamedElement("alias int XXX; "), "int", ERROR_IS_NOT_A_VALUE, true);
		test_resolveElement(parseNamedElement("alias XXX = int; "), "int", ERROR_IS_NOT_A_VALUE, true);
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public void test_resolveSearchInMembersScope________() throws Exception {
		
		test_resolveSearchInMembersScope(parseNamedElement("int intVar; alias intVar XXX; "), 
			NE_LanguageIntrinsics_SemanticsTest.INT_PROPERTIES);
		
		// TODO: more tests for this functionality
		test_resolveSearchInMembersScope(parseNamedElement("int intVar; alias XXX = intVar; "), 
			NE_LanguageIntrinsics_SemanticsTest.INT_PROPERTIES);
	}
	
}