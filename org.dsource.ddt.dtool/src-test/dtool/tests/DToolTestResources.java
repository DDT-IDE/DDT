/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/

package dtool.tests;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import dtool.DToolBundle;
import dtool.tests.utils.MiscFileUtils;


public class DToolTestResources {
	
	protected static final String TEST_RESOURCES_BASE_DIR_PROPERTY = DToolTests.DTOOL_PREFIX + "TestResourcesDir";
	protected static final String TEST_RESOURCES_WORKING_DIR_PROPERTY = DToolTests.DTOOL_PREFIX + "TestsWorkingDir";
	
	protected static final String TESTDATA = "testdata";
	
	protected static DToolTestResources instance;
	
	// lazy loaded
	public static synchronized DToolTestResources getInstance() {
		if(instance == null) {
			instance = new DToolTestResources();
			initWorkingdir(); // attempt default init
		}
		return instance;
	}
	
	private String testResourcesDir;
	
	public DToolTestResources() {
		testResourcesDir = System.getProperty(TEST_RESOURCES_BASE_DIR_PROPERTY);
		if(testResourcesDir == null) {
			// Assume a default based on process working directory
			// This is so test can be started from typical Eclipse workspace without setting up VM properties
			testResourcesDir = "../"+DToolBundle.BUNDLE_ID+"/"+TESTDATA;
		}
		System.out.println("testResourcesDir:" + testResourcesDir);
	}
	
	public File getResourcesDir() {
		File file = new File(testResourcesDir);
		assertTrue(file.exists() && file.isDirectory());
		return file;
	}
	
	public static File getTestResource(String... segments) {
		return MiscFileUtils.getFile(DToolTestResources.getInstance().getResourcesDir(), segments);
	}
	
	public static Path getTestResourcePath(String... segments) {
		try {
			return getTestResource(segments).getCanonicalFile().toPath();
		} catch (IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	protected static String testsWorkingDir;
	
	protected static void initWorkingDir(String workingDir) {
		assertTrue(workingDir != null);
		assertTrue(testsWorkingDir == null);
		testsWorkingDir = workingDir;
		
		System.out.println("====>> WORKING DIR: " + testsWorkingDir);
		
		File file = new File(testsWorkingDir);
		if(!file.exists()) {
			file.mkdir();
		}
	}
	
	public static File getWorkingDir() {
		getInstance();
		assertNotNull(testsWorkingDir);
		File file = new File(testsWorkingDir);
		assertTrue(file.exists() && file.isDirectory());
		return file;
	}
	
	protected static void initWorkingdir() {
		// default init:
		String property = System.getProperty(TEST_RESOURCES_WORKING_DIR_PROPERTY);
		if(property != null) {
			initWorkingDir(property);
		} else {
			initWorkingDir(System.getProperty("java.io.tmpdir") + "/_tests_workingdir");
		}
	}
	
	public static String resourceFileToString(File file) {
		return resourceFileToString(file, TESTDATA);
	}
	
	public static String resourceFileToString(File file, String rootDir) {
		if(file.getName().equals(rootDir)) {
			return "#";
		} else {
			File parentFile = file.getParentFile();
			String parentStr = (parentFile != null) ? resourceFileToString(parentFile, rootDir) : ""; 
			return parentStr + "/" + file.getName();
		}
	}
	
}