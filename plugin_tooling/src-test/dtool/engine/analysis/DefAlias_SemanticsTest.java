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


public class DefAlias_SemanticsTest extends NamedElement_CommonTest {
	
	protected static final String CLASS_DEF = "struct Xpto { int x; };";
	
	@Override
	public void test_resolveTypeForValueContext________() throws Exception {
		// Test alias to var
		test_resolveTypeForValueContext("int intVar; alias intVar XXX; ", "int");
		test_resolveTypeForValueContext("int intVar; alias XXX = intVar; ", "int");
		// broken variant
		test_resolveTypeForValueContext("alias intVar XXX; ", null, true);
		test_resolveTypeForValueContext("alias XXX = intVar; ", null, true);
		
		// Test alias to type
		test_resolveTypeForValueContext("alias int XXX; ", "int", true);
		test_resolveTypeForValueContext("alias XXX = int; ", "int", true);
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public void test_resolveSearchInMembersScope________() throws Exception {
		
		testResolveSearchInMembersScope(parseNamedElement("int intVar; alias intVar XXX; "), 
			LanguageIntrinsics_SemanticsTest.INT_PROPERTIES);
		
		// TODO: more tests for this functionality
		testResolveSearchInMembersScope(parseNamedElement("int intVar; alias XXX = intVar; "), 
			LanguageIntrinsics_SemanticsTest.INT_PROPERTIES);
	}
	
}