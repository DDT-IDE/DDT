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

import descent.core.ddoc.DdocMacros;

public class DDocMacrosTest {
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		 runMacroTest("abc", "abc");
		 runMacroTest("abc $(B mybold) def", "abc " + boldResult("mybold") + " def");
		 
		 //Nested
		 runMacroTest("abc $(B $(I my) bold) def", "abc " + boldResult(italicResult("my") + " bold") + " def");
		 
		//Nested + Recursive
		 Map<String, String> macroDefsA = new HashMap<>(DdocMacros.getDefaultMacros());
		 macroDefsA.put("X", "<u>xxx $(I $0) </u>");
		 runMacroTest("abc $(B $(X my) bold) xyz", macroDefsA, 
			 "abc " + boldResult("<u>xxx "+italicResult("my")+" </u>"  + " bold") + " xyz");
		 
		 // Recursive - cycle
		 macroDefsA.put("CYCLE", "<u>xxx $(CYCLE blah) </u>");
		 runMacroTest("abc $(CYCLE my) xyz", macroDefsA, 
			 "abc " + "<u>xxx "+("")+" </u>" + " xyz");
		 
		 // Not found:
		 runMacroTest("abc $(NON_EXISTANT my) xyz", macroDefsA, 
			 "abc " + "$(NON_EXISTANT my)" + " xyz");
		 
		 // macro odd syntax:
		 runMacroTest("abc) (my) ) ", macroDefsA, 
			 "abc) (my) ) ");

	}
	public static String boldResult(String string) {
		return "<b>" + string + "</b>";
	}
	public static String italicResult(String string) {
		return "<i>" + string + "</i>";
	}
	
	public void runMacroTest(String source, String expectedResult) {
		runMacroTest(source, DdocMacros.getDefaultMacros(), expectedResult);
		
		runMacroTest("$(B "+source+")", DdocMacros.getDefaultMacros(), boldResult(expectedResult));
	}
	public void runMacroTest(String source, Map<String, String> macroDefinitions, String expectedResult) {
		String result = DdocMacros.replaceMacros(source, macroDefinitions);
		assertEquals(result, expectedResult);
	}
	
}