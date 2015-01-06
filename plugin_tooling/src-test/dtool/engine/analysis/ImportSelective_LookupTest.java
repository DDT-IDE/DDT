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

import org.junit.Test;


public class ImportSelective_LookupTest extends CommonLookupTest {
	
	@Test
	public void testImportSelective() throws Exception { testImportSelective$(); }
	public void testImportSelective$() throws Exception {
		 
		testLookup(parseModule_WithRef("import pack.foobar : PackFoobar_member;", "PackFoobar_member"),  
			checkSingleResult("int PackFoobar_member;")
		);
		testLookup(parseModule_WithRef("import pack.foobar : PackFoobar_member;", "PackFoobar_member2"),  
			checkSingleResult(null)
		);
		
		testLookup(parseModule_WithRef("import pack.foobar : PackFoobar_member; void PackFoobar_member;", 
			"PackFoobar_member"),  
			checkNameConflict("int PackFoobar_member;", "void PackFoobar_member;")
		);
		
		// We should add more tests here. The other cases are currently tested by ResolverSourceTests
		
	}
	
}