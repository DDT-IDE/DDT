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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import melnorme.lang.tests.CommonToolingTest;
import melnorme.lang.utils.MiscFileUtils;
import melnorme.utilbox.core.fntypes.VoidFunction;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.misc.PathUtil;
import dtool.dub.BundlePath;
import dtool.dub.DubHelper;
import dtool.engine.modules.ModuleNamingRules;
import dtool.util.NewUtils;

public class CommonDToolTest extends CommonToolingTest {
	
	public static BundlePath bundlePath(Location basePath, String other) {
		return BundlePath.create(basePath.resolve_fromValid(other));
	}
	
	public static String testsDubPath() {
		return DubHelper.DUB_PATH_OVERRIDE == null ? "dub" : DubHelper.DUB_PATH_OVERRIDE;
	}
	
	public static Path testsDubPath2() {
		return testsDubPath() == null ? null : PathUtil.createPathOrNull(testsDubPath());
	}
	
	/* -----------------  ----------------- */
	
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
					return addInAnyFileName || ModuleNamingRules.isValidCompilationUnitName(childName);
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
