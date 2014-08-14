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
package mmrnmhrm.core.build;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.tests.CommonTest;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

public class DubProjectBuilderTest extends CommonTest {
	
	protected static final String NL = System.getProperty("line.separator");
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		testParseErrorLine("somefile.d(12): Error: blah" + NL, "somefile.d", "12", "blah");
		testParseErrorLine("nana/somefile.d(12): Error: blah" + NL, "nana/somefile.d", "12", "blah");
		testParseErrorLine("nana/somefile.d(12): blah" + NL);
		testParseErrorLine("nana/somefile.d(): Error: blah" + NL, "nana/somefile.d", "", "blah");
		testParseErrorLine("nana/somefile.d: Error: blah" + NL);
		testParseErrorLine("xxx"+NL+"somefile.d(12): Error: blah" + NL, "somefile.d", "12", "blah");
	}
	
	protected int lineCount = 0;
	
	protected void testParseErrorLine(String output) {
		testParseErrorLine(output, null, null, null);
	}
	
	protected void testParseErrorLine(String output, 
			final String expectedFile, final String expectedLineStr, final  String expectedErrorMsg) {
		
		lineCount = 0;
		
		new DubProjectBuilder() {
			 @Override
			protected void processErrorLine(String file, String lineStr, String errorMsg) throws CoreException {
				assertEquals(file, expectedFile);
				assertEquals(lineStr, expectedLineStr);
				assertEquals(errorMsg, expectedErrorMsg);
				lineCount++;
			};
		 }.processCompilerErrors(output);
		 
		 if(expectedFile == null) {
			 assertTrue(lineCount == 0);
		 } else {
			 assertTrue(lineCount == 1);
		 }
	}
	
}