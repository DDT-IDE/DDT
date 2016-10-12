/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ddoc;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class DDocMacrosTest {
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		 runBasicTest("sample", "sample");
		 
		 runBasicTest("$(B sampleB)", boldResult("sampleB"));
		 runBasicTest("$(NON_EXISTANT my)", "$(NON_EXISTANT my)");
		 
		 runBadSyntaxTests("$(B sampleB", "$(B sampleB");
		 runBadSyntaxTests("$(B ", "$(B ");
		 runBadSyntaxTests("$(", "$(");
	}
	
	public void runBasicTest(String SAMPLE_SOURCE, String SAMPLE_EXPECTED) {
		runMacroTest(SAMPLE_SOURCE, SAMPLE_EXPECTED);
		runMacroTest(SAMPLE_SOURCE + " abc $(B mybold) def", 
			SAMPLE_EXPECTED + " abc " + boldResult("mybold") + " def");
		 
		//Nested
		runMacroTest(SAMPLE_SOURCE + "abc $(B $(I my) bold) def", 
			SAMPLE_EXPECTED + "abc " + boldResult(italicResult("my") + " bold") + " def");
		runMacroTest("abc $(B $(I " + SAMPLE_SOURCE + ") bold) def", 
			"abc " + boldResult(italicResult(SAMPLE_EXPECTED) + " bold") + " def");
		 
		//Nested + Recursive
		Map<String, String> macroDefsA = new HashMap<>(DdocMacros.getDefaultMacros());
		macroDefsA.put("X", "<u>" +SAMPLE_SOURCE+ " $(I $0) </u>");
		runMacroTest("abc $(B $(X my) bold) xyz", macroDefsA, 
			 "abc " + boldResult("<u>" +SAMPLE_EXPECTED+ " "+italicResult("my")+" </u>"  + " bold") + " xyz");
		 
		// Recursive - cycle
		macroDefsA.put("CYCLE", "<u>xxx $(CYCLE blah) </u>");
		runMacroTest(SAMPLE_SOURCE + "abc $(CYCLE my) xyz", macroDefsA, 
			SAMPLE_EXPECTED + "abc " + "<u>xxx "+DdocMacros.cycleErrorString("CYCLE")+" </u>" + " xyz");
		 
		// Not found:
		runMacroTest(SAMPLE_SOURCE + "abc $(NON_EXISTANT my) xyz", macroDefsA, 
			SAMPLE_EXPECTED + "abc " + "$(NON_EXISTANT my)" + " xyz");
		 
		// macro odd syntax:
		runMacroTest(SAMPLE_SOURCE + "abc) (my) ) ", SAMPLE_EXPECTED + "abc) (my) ) ");
		runBadSyntaxTests(SAMPLE_SOURCE, SAMPLE_EXPECTED);
		
		// TODO: we need test for arguments, parens nesting, string nesting, etc. 
		// It's likely there are bugs with that functionality
	}
	public void runBadSyntaxTests(String SAMPLE_PREFIX_SOURCE, String SAMPLE_EXPECTED) {
		runMacroTest(SAMPLE_PREFIX_SOURCE + "$(", SAMPLE_EXPECTED + "$(");
		runMacroTest(SAMPLE_PREFIX_SOURCE + "$(X", SAMPLE_EXPECTED + "$(X");
		runMacroTest(SAMPLE_PREFIX_SOURCE + "$(X ", SAMPLE_EXPECTED + "$(X ");
		runMacroTest(SAMPLE_PREFIX_SOURCE + "$(X def $(", SAMPLE_EXPECTED + "$(X def $(");
	}
	
	public static String boldResult(String string) {
		return "<b>" + string + "</b>";
	}
	public static String italicResult(String string) {
		return "<i>" + string + "</i>";
	}
	
	public void runMacroTest(String source, String expectedResult) {
		runMacroTest(source, DdocMacros.getDefaultMacros(), expectedResult);
	}
	public void runMacroTest(String source, Map<String, String> macroDefinitions, String expectedResult) {
		String result = DdocMacros.replaceMacros(source, macroDefinitions);
		assertEquals(result, expectedResult);
	}
	
}