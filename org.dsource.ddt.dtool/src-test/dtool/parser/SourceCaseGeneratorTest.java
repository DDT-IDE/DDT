/*******************************************************************************
 * Copyright (c) 2012, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/

package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;

import org.junit.Test;

public class SourceCaseGeneratorTest {
	
	@Test
	public void test() throws Exception { test$(); }
	public void test$() throws Exception {
		ArrayList<String> testCasesList = new ArrayList<String>();
		
		TemplatedSourceProcessor.processSource("asdf ## #{,#},#,,#NL,##, ,line}==", 0, "#", testCasesList);
		
		for (int i = 0; i < testCasesList.size(); i++) {
			String testCase = testCasesList.get(i);
			assertTrue(testCase.startsWith(" # ", 4));
		}
		StringUtil.collToString(testCasesList, "\n---\n");
		
		assertTrue(testCasesList.size() == (7+2));

		String[] testCases = ArrayUtil.createFrom(testCasesList, String.class);
		assertEquals(testCases[0], "asdf # ==");
		assertEquals(testCases[1], "asdf # }==");
		assertEquals(testCases[2], "asdf # ,==");
		assertEquals(testCases[3], "asdf # \r==");
		assertEquals(testCases[4], "asdf # \n==");
		assertEquals(testCases[5], "asdf # \r\n==");
		assertEquals(testCases[6], "asdf # #==");
		assertEquals(testCases[7], "asdf #  ==");
		assertEquals(testCases[8], "asdf # line==");
	}
}