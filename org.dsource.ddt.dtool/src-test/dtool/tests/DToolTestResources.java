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

import dtool.DToolBundle;



public class DToolTestResources implements IDToolTestConstants {
	
	protected static final String TEST_RESOURCES_BASE_DIR_PROPERTY = DToolBaseTest.DTOOL_PREFIX + "TestResourcesDir";
	protected static final String TEST_RESOURCES_WORKING_DIR_PROPERTY = DToolBaseTest.DTOOL_PREFIX + "TestsWorkingDir";
	
	protected static final String TESTDATA = "testdata/";
	
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
	}
	
	public File getResourcesDir() {
		File file = new File(testResourcesDir);
		assertTrue(file.exists() && file.isDirectory());
		return file;
	}
	
	public static File getTestResource(String fileRelPath) {
		return new File(DToolTestResources.getInstance().getResourcesDir(), fileRelPath);
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
		if(testsWorkingDir == null) {
			initWorkingdir();
		}
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
		}
		// Maybe use workingDir = "../_runtime-tests" instead
	}

}
