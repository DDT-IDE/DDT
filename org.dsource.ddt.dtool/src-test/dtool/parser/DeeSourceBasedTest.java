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

import java.io.File;

import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.TemplatedSourceProcessor;
import dtool.tests.DToolBaseTest;

public class DeeSourceBasedTest extends DToolBaseTest {
	
	protected static final class TestsTemplateSourceProcessor extends TemplatedSourceProcessor {
		@Override
		protected void reportError(int offset) throws TemplatedSourceException {
			assertFail();
		}
	}
	
	public AnnotatedSource[] getSourceBasedTests(File file, TemplatedSourceProcessor commonDefinitions) {
		TestsTemplateSourceProcessor tsp = new TestsTemplateSourceProcessor();
		if(commonDefinitions != null) {
			tsp.addGlobalExpansions(commonDefinitions.getGlobalExpansions());
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