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
import java.util.List;

import melnorme.utilbox.core.Function;
import melnorme.utilbox.core.Predicate;
import melnorme.utilbox.core.VoidFunction;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.CollectionUtil;
import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.StringUtil;
import dtool.DeeNamingRules_Test;


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
		return toFnParameterList(getDeeModuleList(folder, true), null);
	}
	
	public static Collection<Object[]> toFnParameterList(List<File> fileList, Predicate<File> filter) {
		if(filter != null) {
			fileList = CollectionUtil.filter(fileList, filter);
		}
		Function<File, Object[]> arrayWrap = new Function<File, Object[]>() {
			@Override
			public Object[] evaluate(File obj) {
				return new Object[] { obj };
			};
		};
		
		return Arrays.asList(ArrayUtil.map(fileList, arrayWrap, Object[].class));
	}
	
}