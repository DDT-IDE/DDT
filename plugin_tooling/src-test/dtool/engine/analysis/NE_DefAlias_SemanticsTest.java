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
		test_resolveElement(parseNamedElement("int intVar; alias intVar XXX; "), "intVar", "$/int");
		test_resolveElement(parseNamedElement("int intVar; alias XXX = intVar; "), "intVar", "$/int");

		// broken variant
		String NOT_FOUND__target = NOT_FOUND__Name + ":target";
		test_resolveElement(parseNamedElement("alias target XXX; "), NOT_FOUND__Name, NOT_FOUND__target);
		test_resolveElement(parseNamedElement("alias XXX = target; "), NOT_FOUND__Name, NOT_FOUND__target);
		
		test_resolveElement(parseNamedElement("alias XXX = zzz; alias zzz = target"), NOT_FOUND__Name, 
			NOT_FOUND__target);
		
		/* FIXME: do syntax error element */
		test_resolveElement(parseNamedElement("alias XXX = "), NOT_FOUND__Name, NOT_FOUND__Name+":");
		test_resolveElement(parseNamedElement("alias XXX"), NOT_FOUND__Name, NOT_FOUND__Name);
		
		// Test alias to type
		test_resolveElement(parseNamedElement("alias int XXX; "), "int", ERROR_IS_NOT_A_VALUE+":int");
		test_resolveElement(parseNamedElement("alias XXX = int; "), "int", ERROR_IS_NOT_A_VALUE+":int");
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