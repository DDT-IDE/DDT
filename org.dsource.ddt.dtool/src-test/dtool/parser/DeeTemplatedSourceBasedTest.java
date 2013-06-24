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


import static dtool.tests.DToolTestResources.getTestResource;
import static dtool.tests.DToolTestResources.resourceFileToString;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.misc.CollectionUtil.filter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import melnorme.utilbox.core.Predicate;
import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.sourcegen.TemplateSourceProcessorParser.TspExpansionElement;
import dtool.sourcegen.TemplatedSourceProcessor;
import dtool.tests.DToolTests;
import dtool.tests.DeeFileBasedTest;
import dtool.util.NewUtils;

@RunWith(Parameterized.class)
public abstract class DeeTemplatedSourceBasedTest extends DeeFileBasedTest {
	
	public static ArrayList<File> getDeeModuleList(String testFolder) {
		return getDeeModuleList(getTestResource(testFolder), true);
	}
	
	public static void addCommonDefinitions(String testFolder, Map<String, TspExpansionElement> commonDefs) {
		List<File> commonDefsFileList = getDeeModuleList(testFolder);
		commonDefsFileList = filter(commonDefsFileList, new TemplatedTestFilesFilter(){{filterHeaders = false;}});
		
		String klassSimpleName = DeeParserSourceBasedTest.class.getSimpleName();
		testsLogger.println(">>>>>>============ " + klassSimpleName + " COMMON DEFINITIONS FILES: ============" );
		for (File headerFile : commonDefsFileList) {
			testsLogger.println(headerFile);
			TemplatedSourceProcessor tsp = new TestsTemplateSourceProcessor() {
				@Override
				protected void addFullyProcessedSourceCase(ProcessingState caseState) {
					assertTrue(caseState.isHeaderCase);
				}
			};
			tsp.processSource_unchecked("#", readStringFromFileUnchecked(headerFile));
			assertTrue(tsp.getGenCases().size() == 0);
			NewUtils.addNew(commonDefs, tsp.getGlobalExpansions());
		}
		testsLogger.println("<<<<<<" );
	}
	
	/* ----------------------------------------------- */
	
	public static Collection<Object[]> createTestFileParameters(String testFolder) {
		return createTestFileParameters(testFolder, new TemplatedTestFilesFilter());
	}
	
	public static Collection<Object[]> createTestFileParameters(String testFolder, TemplatedTestFilesFilter filter) {
		List<File> fileList = getDeeModuleList(testFolder);
		fileList = filter != null ? filter(fileList, filter) : fileList;
		return createTestListFromFiles(true, fileList);
	}
	
	public static class TemplatedTestFilesFilter implements Predicate<File> {
		boolean filterHeaders = true;
		
		@Override
		public boolean evaluate(File file) {
			if(file.getName().endsWith("_TODO")) return true;
			if(file.getParentFile().getName().equals("0_common")) return filterHeaders;
			if(file.getName().contains(".export.") || file.getName().contains(".EXPORT.")) return filterHeaders;
			if(file.getName().endsWith(".tsp")) return !filterHeaders;
			throw assertFail();
		}
	}
	
	public DeeTemplatedSourceBasedTest(String testUIDescription, File file) {
		super(testUIDescription, file);
	}
	
	public AnnotatedSource[] getTestCasesFromFile(Map<String, TspExpansionElement> commonDefinitions) {
		TestsTemplateSourceProcessor tsp = new TestsTemplateSourceProcessor();
		if(commonDefinitions != null) {
			tsp.addGlobalExpansions(commonDefinitions);
		}
		testsLogger.print(">>>====== " + getClass().getSimpleName() + " on: " + resourceFileToString(file));
		AnnotatedSource[] sourceBasedTests = tsp.processSource_unchecked("#", readStringFromFileUnchecked(file));
		testsLogger.println(" ("+sourceBasedTests.length+") ======<<<");
		return sourceBasedTests;
	}
	
	public static AnnotatedSource[] getSourceBasedTestCases(String fileSource) {
		return new TestsTemplateSourceProcessor().processSource_unchecked("#", fileSource);
	}
	
	protected static class TestsTemplateSourceProcessor extends TemplatedSourceProcessor {
		@Override
		protected void reportError(int offset) throws TemplatedSourceException {
			assertFail();
		}
		
		@Override
		protected void putExpansion(ProcessingState sourceCase, String expansionId, TspExpansionElement expansion) {
			addExpansion(sourceCase, expansionId, expansion);
			
			if(DToolTests.TESTS_LITE_MODE) {
				String name = expansionId;
				if(name != null && name.endsWith("__LITE")) { 
					name = name.replace("__LITE", "");
					TspExpansionElement value = expansion;
					TspExpansionElement newElem = new TspExpansionElement(name, 
						value.pairedExpansionId, value.arguments, value.anonymousExpansion, value.dontOuputSource);
					assertTrue(sourceCase.getExpansion(name) != null);
					addExpansion(sourceCase, name, newElem);
				}
			}
			
		}
		
		public void addExpansion(ProcessingState sourceCase, String expansionId, TspExpansionElement expansion) {
			sourceCase.putExpansion(expansionId, expansion);
		}
	}
	
	
	public void runAnnotatedTests(AnnotatedSource[] sourceBasedTests) {
		HashSet<String> printSources = new HashSet<String>();
		Pattern trimStartNewlines = Pattern.compile("(^(\\n|\\r\\n)*)|((\\n|\\r\\n)*$)");
		
		int originalTemplateChildCount = -1;
		for (AnnotatedSource testCase : sourceBasedTests) {
			
			boolean printTestCaseSource = testCase.findMetadata("comment", "NO_STDOUT") == null;
			boolean printCaseSeparator = testCase.findMetadata("comment", "PRINT_SEP") != null;
			
			if(!printSources.contains(testCase.originalTemplatedSource)) {
				printCaseEnd(originalTemplateChildCount);
				originalTemplateChildCount = 0;
				testsLogger.println(">> ----------- Parser tests TEMPLATE ("+file.getName()+") : ----------- <<");
				testsLogger.print(testCase.originalTemplatedSource);
				if(printTestCaseSource && !printCaseSeparator) {
					testsLogger.println(" ----------- Parser source tests: ----------- ");
				}
			}
			if(printTestCaseSource) {
				if(printCaseSeparator) {
					testsLogger.println(">-----------");
				}
				testsLogger.println(trimStartNewlines.matcher(testCase.source).replaceAll(""));
			}
			printSources.add(testCase.originalTemplatedSource);
			
			checkOffsetInvariant(testCase);
			runAnnotatedSourceTest(testCase);
			originalTemplateChildCount++;
		}
		printCaseEnd(originalTemplateChildCount);
		testsLogger.println();
	}
	
	public static void checkOffsetInvariant(AnnotatedSource testSource) {
		int mdOffset = 0;
		for (MetadataEntry mde : testSource.metadata) {
			if(mde.offset != -1) {
				assertTrue(mde.offset >= mdOffset);
			}
			mdOffset = mde.offset;
		}
	}
	
	protected void printCaseEnd(int originalTemplateChildCount) {
		if(originalTemplateChildCount > 10 && originalTemplateChildCount != -1) {
			testsLogger.println("<< ^^^ Previous case count: " + originalTemplateChildCount);
		}
	}
	
	protected abstract void runAnnotatedSourceTest(AnnotatedSource testCase);
	
}