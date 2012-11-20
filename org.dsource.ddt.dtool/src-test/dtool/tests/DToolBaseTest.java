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
import java.util.Arrays;
import java.util.Collection;

import dtool.DeeNamingRules_Test;

import melnorme.utilbox.core.Function;
import melnorme.utilbox.core.VoidFunction;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.StringUtil;


public class DToolBaseTest extends CommonTestUtils {
	
	public static PrintStream testsLogger = System.out;
	
	/* -------------  Resources stuff   ------------ */
	
	protected static final Charset DEFAULT_TESTDATA_ENCODING = StringUtil.UTF8;
	
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
			return new String(FileUtil.readBytesFromFile(file), DEFAULT_TESTDATA_ENCODING);
		} catch (IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	/* -------------  Source based test   ------------ */
	
	private static final String SPLIT_SOURCE_TEST = "//#SPLIT_SOURCE_TEST";

	public static String[] splitSourceBasedTests(String fileRawSource) {
		String[] split = fileRawSource.split(SPLIT_SOURCE_TEST + "[^\\\r\\\n]*\\\r?\\\n");
		if(fileRawSource.startsWith(SPLIT_SOURCE_TEST)) {
			assertTrue(split[0].isEmpty());
			return ArrayUtil.copyOfRange(split, 1, split.length);
		}
		return split;
	}
	
	public String[] enteringSourceBasedTest(File file) {
		String fileSource = readStringFromFileUnchecked(file);
		String[] splitSourceBasedTests = splitSourceBasedTests(fileSource);
		
		testsLogger.println("-- " + getClass().getSimpleName() + 
			" on file: " + DToolTestResources.resourceFileToString(file) + " ("+splitSourceBasedTests.length+")");
		return splitSourceBasedTests;
	}
	
	/* -------------  Module list stuff   ------------ */
	
	protected static ArrayList<File> getDeeModuleList(File folder, boolean recurseDirs) throws IOException {
		return getDeeModuleList(folder, recurseDirs, false);
	}
	protected static ArrayList<File> getDeeModuleList(File folder, boolean recurseDirs, final boolean validCUsOnly)
			throws IOException {
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
	
	public static Collection<Object[]> getTestFilesFromFolderAsParameterList(File folder) throws IOException {
		ArrayList<File> deeModuleList = getDeeModuleList(folder, true);
		
		Function<File, Object[]> arrayWrap = new Function<File, Object[]>() {
			@Override
			public Object[] evaluate(File obj) {
				return new Object[] { obj };
			};
		};
		
		return Arrays.asList(ArrayUtil.map(deeModuleList, arrayWrap, Object[].class));
	}
	
}