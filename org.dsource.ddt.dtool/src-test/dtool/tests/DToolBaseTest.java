/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.tests;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import melnorme.utilbox.core.VoidFunction;
import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.StringUtil;

import org.junit.Before;

import dtool.DeeNamingRules_Test;
import dtool.util.NewUtils;


public class DToolBaseTest extends CommonTestUtils {
	
	public static PrintStream testsLogger = System.out;
	
	public static Set<String> executedTests = new HashSet<String>();
	
	@Before
	public void printSeparator() throws Exception {
		String simpleName = getClass().getSimpleName();
		if(!executedTests.contains(simpleName)) {
			testsLogger.println("===============================  "+simpleName+"  ===============================");
			executedTests.add(simpleName);
		}
	}
	
	/* -------------  Resources stuff   ------------ */
	
	public static final Charset DEFAULT_TESTDATA_ENCODING = StringUtil.UTF8;
	
	public static String readTestResourceFile(String filePath) throws IOException {
		File testDataDir = DToolTestResources.getInstance().getResourcesDir();
		File file = new File(testDataDir, filePath);
		return readStringFromFile(file);
	}
	
	public static String readStringFromFile(File file) throws IOException, FileNotFoundException {
		return new String(FileUtil.readBytesFromFile(file), DEFAULT_TESTDATA_ENCODING);
	}
	
	public static String readStringFromFileUnchecked(File file) {
		try {
			return NewUtils.readStringFromFile_PreserveBOM(file, DEFAULT_TESTDATA_ENCODING);
		} catch (IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	public static File getFile(String rootPath, String... segments) {
		File file = new File(rootPath);
		return getFile(file, segments);
	}
	
	public static File getFile(File file, String... segments) {
		for (String segment : segments) {
			assertTrue(segment.contains("/") == false && segment.contains("\\") == false);
			file = new File(file, segment);
		}
		return file;
	}
	
	/* -------------  Module list stuff   ------------ */
	
	protected static ArrayList<File> getDeeModuleList(File folder) {
		return getDeeModuleList(folder, true);
	}
	protected static ArrayList<File> getDeeModuleList(File folder, boolean recurseDirs) {
		return getDeeModuleList(folder, recurseDirs, false);
	}
	protected static ArrayList<File> getDeeModuleList(File folder, boolean recurseDirs, final boolean validCUsOnly) {
		assertTrue(folder.exists() && folder.isDirectory());
		
		final boolean addInAnyFileName = !validCUsOnly;
		final ArrayList<File> fileList = new ArrayList<File>();
		
		VoidFunction<File> fileVisitor = new VoidFunction<File>() {
			@Override
			public Void evaluate(File file) {
				if(file.isFile()) {
					fileList.add(file);
				}
				return null;
			}
		};
		
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File parent, String childName) {
				File childFile = new File(parent, childName);
				if(childFile.isDirectory()) {
					// exclude team private folder, like .svn, and other crap
					return !childName.startsWith(".");
				} else {
					return addInAnyFileName || DeeNamingRules_Test.isValidCompilationUnitName(childName);
				}
			}
		};
		MiscFileUtils.traverseFiles(folder, recurseDirs, fileVisitor, filter);
		return fileList;
	}
	
	public static Collection<Object[]> createTestListFromFiles(final boolean includeDescription, List<File> fileList) {
		final Collection<Object[]> testList = new ArrayList<>();
		addFilesToTestList(testList, fileList, includeDescription);
		return testList;
	}
	
	public static void addFilesToTestList(final Collection<Object[]> testList, List<File> fileList,
		final boolean includeDescription) {
		for (File file : fileList) {
			if(includeDescription) {
				testList.add(new Object[] { file.getName(), file });
			} else {
				testList.add(new Object[] { file });
			}
		}
	}
	
}