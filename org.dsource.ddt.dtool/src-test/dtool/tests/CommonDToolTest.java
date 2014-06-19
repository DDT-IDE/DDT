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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import melnorme.utilbox.core.fntypes.VoidFunction;
import melnorme.utilbox.misc.SimpleLogger;
import melnorme.utilbox.tests.CommonTestExt;

import org.junit.Before;

import dtool.engine.modules.ModuleNamingRules;
import dtool.tests.utils.MiscFileUtils;
import dtool.util.NewUtils;

public class CommonDToolTest extends CommonTestExt {
	
	public static PrintStream testsLogger = System.out;
	public static SimpleLogger testsLogVerbose = SimpleLogger.create("verbose");
	
	public static Set<String> executedTests = new HashSet<String>();
	
	@Before
	public void printSeparator() throws Exception {
		String simpleName = getClass().getSimpleName();
		if(!executedTests.contains(simpleName)) {
			testsLogger.println("===============================  "+simpleName+"  ===============================");
			executedTests.add(simpleName);
		}
	}
	
	public static String readStringFromFile_PreserveBOM(File file) {
		try {
			return NewUtils.readStringFromFile_PreserveBOM(file, DEFAULT_TESTDATA_ENCODING);
		} catch (IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
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
					return addInAnyFileName || ModuleNamingRules.isValidCompilationUnitName(childName, false);
				}
			}
		};
		MiscFileUtils.traverseFiles(folder, recurseDirs, fileVisitor, filter);
		return fileList;
	}
	
	public static Collection<Object[]> createTestListFromFiles(final boolean includeDescription, List<File> fileList) {
		final Collection<Object[]> testList = new ArrayList<>();
		addFilesToTestParameters(testList, fileList, includeDescription);
		return testList;
	}
	
	public static void addFilesToTestParameters(final Collection<Object[]> testList, List<File> fileList,
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
