/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.dub;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.tests.CommonTest;

import org.junit.Test;

import dtool.dub.DubBuildOutputParser;

public class DubBuilderOutputParserTest extends CommonTest {
	
	protected static final String NL = System.getProperty("line.separator");
	
	@Test
	public void testParseErrorLine() throws Exception { testParseErrorLine$(); }
	public void testParseErrorLine$() throws Exception {
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
		
		new DubBuildOutputParser<Exception>() {
			@Override
			protected void processDubFailure(String dubErrorLine) throws Exception {
				assertFail();
			};
			
			 @Override
			protected void processCompilerError(String file, String lineStr, String errorMsg) {
				assertEquals(file, expectedFile);
				assertEquals(lineStr, expectedLineStr);
				assertEquals(errorMsg, expectedErrorMsg);
				lineCount++;
			}
		 }.processCompilerErrors(output);
		 
		 if(expectedFile == null) {
			 assertTrue(lineCount == 0);
		 } else {
			 assertTrue(lineCount == 1);
		 }
	}
	
}