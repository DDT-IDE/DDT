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
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import melnorme.utilbox.core.fntypes.Predicate;
import melnorme.utilbox.misc.SimpleLogger;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.sourcegen.TemplatedSourceProcessor;
import dtool.sourcegen.TemplatedSourceProcessorCommonTest.TestsTemplatedSourceProcessor;
import dtool.sourcegen.TemplatedSourceProcessorParser.TspExpansionElement;
import dtool.tests.DToolTests;
import dtool.tests.DeeFileBasedTest;
import dtool.util.NewUtils;

@RunWith(Parameterized.class)
public abstract class CommonTemplatedSourceBasedTest extends DeeFileBasedTest {
	
	public static SimpleLogger templatedTestsSourceLog = SimpleLogger.create("TemplatedSourceBasedTest");
	public static SimpleLogger expandedTestCaseLog = SimpleLogger.create("TemplatedSourceBasedTest");
	
	public static ArrayList<File> getDeeModuleList(String testFolder) {
		return getDeeModuleList(getTestResource(testFolder), true);
	}
	
	public static void addCommonDefinitions(Class<?> klass, String testFolder,
		Map<String, TspExpansionElement> commonDefs) {
		ArrayList<File> commonDefsFileList = getDeeModuleList(testFolder);
		commonDefsFileList = filter(commonDefsFileList, new TemplatedTestFilesFilter(){{filterHeaders = false;}});
		
		String klassSimpleName = klass.getSimpleName();
		testsLogger.println(">>>>>>============ " + klassSimpleName + " COMMON DEFINITIONS FILES: ============" );
		for (File headerFile : commonDefsFileList) {
			testsLogger.println(headerFile);
			TemplatedSourceProcessor tsp = new DeeTestsTemplateSourceProcessor() {
				@Override
				protected void addFullyProcessedSourceCase(ProcessingState caseState) {
					assertTrue(caseState.isHeaderCase);
				}
			};
			tsp.processSource_unchecked("#", readStringFromFileUnchecked(headerFile));
			assertTrue(tsp.getGenCases().size() == 0);
			NewUtils.addNew(commonDefs, tsp.getGlobalExpansions());
		}
		testsLogger.println("--------------" );
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
			if(file.getName().endsWith(".txt")) return true;
			if(file.getParentFile().getName().equals("0_common")) return filterHeaders;
			if(file.getName().contains(".export") || file.getName().contains(".EXPORT")) return filterHeaders;
			if(file.getName().endsWith(".tsp")) return !filterHeaders;
			
			if(file.getName().endsWith(".d")) {
				try {
					assertTrue(!TemplatedSourceProcessor.isTSPSourceStart(new FileReader(file)));
				} catch(IOException e) {
					assertFail();
				}
				// Allow if file had no TSP data.
				return true;
			}
			throw assertFail();
		}
	}
	
	public CommonTemplatedSourceBasedTest(String testUIDescription, File file) {
		super(testUIDescription, file);
	}
	
	public AnnotatedSource[] getTestCasesFromFile(Map<String, TspExpansionElement> commonDefinitions) {
		DeeTestsTemplateSourceProcessor tsp = new DeeTestsTemplateSourceProcessor();
		if(commonDefinitions != null) {
			tsp.addGlobalExpansions(commonDefinitions);
		}
		testsLogger.print(">>>====== " + getClass().getSimpleName() + " on: " + resourceFileToString(file));
		AnnotatedSource[] sourceBasedTests = tsp.processSource_unchecked("#", readStringFromFileUnchecked(file));
		testsLogger.println(" ("+sourceBasedTests.length+") ======<<<");
		return sourceBasedTests;
	}
	
	public static AnnotatedSource[] getSourceBasedTestCases(String fileSource) {
		return new DeeTestsTemplateSourceProcessor().processSource_unchecked("#", fileSource);
	}
	
	protected static class DeeTestsTemplateSourceProcessor extends TestsTemplatedSourceProcessor {
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
		HashSet<String> printedTemplatedSources = new HashSet<String>();
		
		boolean printTestCaseSource = true;
		int splitTemplateChildCount = -1;
		for (AnnotatedSource testCase : sourceBasedTests) {
			
			if(testCase.findMetadata("comment", "NO_STDOUT") != null) {
				printTestCaseSource = false;
			}
			boolean printCaseSeparator = testCase.findMetadata("comment", "PRINT_SEP") != null;
			
			if(!printedTemplatedSources.contains(testCase.originalTemplatedSource)) {
				printCaseEnd(splitTemplateChildCount);
				splitTemplateChildCount = 0;
				String testClassName = getClass().getSimpleName();
				String fileName = file.getName();
				templatedTestsSourceLog.println(
					">> ----------- "+testClassName+" TEMPLATE ("+fileName+") : ----------- <<");
				templatedTestsSourceLog.print(testCase.originalTemplatedSource);
				if(!testCase.originalTemplatedSource.endsWith("\n"))
					templatedTestsSourceLog.println();
				templatedTestsSourceLog.println(" ----------- ^^^^ ----------- ");
			}
			printedTemplatedSources.add(testCase.originalTemplatedSource);
			
			checkOffsetInvariant(testCase);
			enterAnnotatedSourceCase(testCase, printTestCaseSource, printCaseSeparator);
			splitTemplateChildCount++;
		}
		printCaseEnd(splitTemplateChildCount);
		templatedTestsSourceLog.println();
	}
	
	protected void printCaseEnd(int originalTemplateChildCount) {
		if(originalTemplateChildCount > 10 && originalTemplateChildCount != -1) {
			templatedTestsSourceLog.println("<< ^^^ Previous case count: " + originalTemplateChildCount);
		}
	}
	
	protected void enterAnnotatedSourceCase(AnnotatedSource testCase, boolean printTestCaseSource,
		boolean printCaseSeparator) {
		if(printTestCaseSource) {
			printTestCaseSource(testCase, printCaseSeparator);
		}
		runAnnotatedSourceTest(testCase);
	}
	
	public static void checkOffsetInvariant(AnnotatedSource testSource) {
		int mdOffset = 0;
		for (MetadataEntry mde : testSource.metadata) {
			if(mde.isTopLevelMetadata()) {
				assertTrue(mde.offset >= mdOffset);
			}
			mdOffset = mde.offset;
		}
	}
	
	protected static final Pattern STARTING_NEWLINES_TRIMMER = Pattern.compile("(^(\\n|\\r\\n)*)|((\\n|\\r\\n)*$)");
	
	public void printTestCaseSource(AnnotatedSource testCase, boolean printCaseSeparator) {
		if(printCaseSeparator) {
			expandedTestCaseLog.println(">-----------");
		}
//		String caseSource = AnnotatedSource.printSourceWithMetadata(testCase);
		String caseSource = testCase.source;
		expandedTestCaseLog.println(STARTING_NEWLINES_TRIMMER.matcher(caseSource).replaceAll(""));
	}
	
	protected abstract void runAnnotatedSourceTest(AnnotatedSource testCase);
	
}