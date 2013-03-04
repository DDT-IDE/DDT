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


import static dtool.tests.DToolTestResources.resourceFileToString;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.util.Map;

import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.TemplatedSourceProcessor;
import dtool.sourcegen.TemplateSourceProcessorParser.TspExpansionElement;
import dtool.tests.DToolBaseTest;
import dtool.tests.DToolTests;

public class DeeSourceBasedTest extends DToolBaseTest {
	
	protected static class TestsTemplateSourceProcessor extends TemplatedSourceProcessor {
		@Override
		protected void reportError(int offset) throws TemplatedSourceException {
			assertFail();
		}
		
		@Override
		protected void putExpansion(ProcessingState sourceCase, String expansionId, TspExpansionElement expansion) {
			sourceCase.putExpansion(expansionId, expansion);
			
			if(DToolTests.TESTS_LITE_MODE) {
				String name = expansionId;
				if(name != null && name.endsWith("__LITE")) { 
					name = name.replace("__LITE", "");
					TspExpansionElement value = expansion;
					TspExpansionElement newElem = new TspExpansionElement(name, 
						value.pairedExpansionId, value.arguments, value.anonymousExpansion, value.dontOuputSource);
					assertTrue(sourceCase.getExpansion(name) != null);
					sourceCase.putExpansion(name, newElem);
				}
			}
		}
		
	}
	
	public AnnotatedSource[] getSourceBasedTests(File file, Map<String, TspExpansionElement> commonDefinitions) {
		TestsTemplateSourceProcessor tsp = new TestsTemplateSourceProcessor();
		if(commonDefinitions != null) {
			tsp.addGlobalExpansions(commonDefinitions);
		}
		testsLogger.print("===>>> " + getClass().getSimpleName() + " on file: " + resourceFileToString(file));
		AnnotatedSource[] sourceBasedTests = tsp.processSource_unchecked("#", readStringFromFileUnchecked(file));
		testsLogger.println(" ("+sourceBasedTests.length+") <<<===");
		return sourceBasedTests;
	}
	
	public static AnnotatedSource[] getSourceBasedTestCases(String fileSource) {
		return new TestsTemplateSourceProcessor().processSource_unchecked("#", fileSource);
	}
	
}