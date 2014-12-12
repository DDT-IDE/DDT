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
package dtool.engine.operations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;

import org.junit.Test;

public class FindDDocOperation_Test extends CommonDToolOperation_Test {
	
	public static final Path DDOC_TESTER_FilePath = BUNDLE_FOO__SRC_FOLDER.resolve("ddoc_tester.d");
	public static final String DDOC_TESTER_Contents = readStringFromFile(DDOC_TESTER_FilePath);
	
	protected String doOperation(Path filePath, int offset) throws Exception {
		return dtoolEngine.getDDocHTMLView(filePath, offset, testsDubPath());
	}
	
	protected String testFindDefinition(Path modulePath, int offset, String expectedEnd, String... expectedContains) 
			throws Exception {
		String opResult = doOperation(modulePath, offset);
		
		if(expectedEnd == null || opResult == null) {
			assertTrue(opResult == expectedEnd);
		} else {
			assertTrue(opResult.endsWith(expectedEnd));
		}
		
		for (String string : expectedContains) {
			assertTrue(opResult.contains(string), "【" + opResult + "】 does not contain:" + string);
		}
		return opResult;
	}
	
	@Test
	public void testALL() throws Exception { testALL$(); }
	public void testALL$() throws Exception {
		
		testFindDefinition(DDOC_TESTER_FilePath, indexOf(DDOC_TESTER_Contents, "/*fooFunc_ref*/"), 
			"<br/>Some DDOC<p/>"
		);
		
		testFindDefinition(DDOC_TESTER_FilePath, 1, 
			null
		);
		
		
		testFindDefinition(DDOC_TESTER_FilePath, indexOf(DDOC_TESTER_Contents, "auto a1"), 
			"", "string", "(Alias)"
		);
		testFindDefinition(DDOC_TESTER_FilePath, indexOf(DDOC_TESTER_Contents, "auto a2"), 
			"", "int"
		);
		testFindDefinition(DDOC_TESTER_FilePath, indexOf(DDOC_TESTER_Contents, "auto aNotAType"), 
			"", "int"
		);
		testFindDefinition(DDOC_TESTER_FilePath, indexOf(DDOC_TESTER_Contents, "auto aError"), 
			"", "Error: Could not resolve auto initializer"
		);
		
		
		testFindDefinition(DDOC_TESTER_FilePath, indexOf(DDOC_TESTER_Contents, "auto a3"), 
			"Bar DDoc<p/>", "Bar"
		);
		
		
		testFindDefinition(DDOC_TESTER_FilePath, indexOf(DDOC_TESTER_Contents, "auto multiple1"), 
			null 
		);
		
		testFindDefinition(DDOC_TESTER_FilePath, indexOf(DDOC_TESTER_Contents, "enum e1"), 
			"", "string", "(Alias)"
		);
		testFindDefinition(DDOC_TESTER_FilePath, indexOf(DDOC_TESTER_Contents, "enum e2"), 
			"", "int"
		);
		
		testFindDefinition(DDOC_TESTER_FilePath, indexOf(DDOC_TESTER_Contents, "enum em1"), 
			null
		);
		
		
		testFindDefinition(DDOC_TESTER_FilePath, indexOf(DDOC_TESTER_Contents, "auto ifauto1"), 
			"", "string", "(Alias)"
		);
		
	}
	
}