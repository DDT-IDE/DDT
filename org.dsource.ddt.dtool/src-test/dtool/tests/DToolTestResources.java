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
	
	protected static final String D_TOOL_TEST_RESOURCES_BASE_DIR = DToolBaseTest.DTOOL_PREFIX + "TestResourcesDir";
	protected static final String D_TOOL_TEST_RESOURCES_WORKING_DIR = DToolBaseTest.DTOOL_PREFIX + "TestsWorkingDir";
	
	private static final String TESTDATA = "testdata/";
	
	protected static DToolTestResources instance;
	
	private String testResourcesDir;
	private String testsWorkingDir;
	
	public DToolTestResources() {
		this(System.getProperty(D_TOOL_TEST_RESOURCES_WORKING_DIR));
	}
	
	public DToolTestResources(String workingDir) {
		testResourcesDir = System.getProperty(D_TOOL_TEST_RESOURCES_BASE_DIR);
		if(testResourcesDir == null) {
			// Assume a default based on process working dir
			testResourcesDir = "../"+DToolBundle.BUNDLE_ID+"/"+TESTDATA;
		}
		
		this.testsWorkingDir = workingDir;
		System.out.println("====>> WORKING DIR: " + testsWorkingDir);
		
		if(testsWorkingDir != null) {
			File file = new File(testsWorkingDir);
			if(!file.exists()) {
				file.mkdir();
			}
		}
	}
	
	protected static void initialize(String workingDir) {
		assertTrue(instance == null);
		instance = new DToolTestResources(workingDir);
	}
	
	public static synchronized DToolTestResources getInstance() {
		if(instance == null) {
			instance = new DToolTestResources();
		}
		return instance;
	}
	
	
	public File getResourcesDir() {
		File file = new File(testResourcesDir);
		assertTrue(file.exists() && file.isDirectory());
		return file;
	}
	
	public static File getTestResource(String fileRelPath) {
		return new File(DToolTestResources.getInstance().getResourcesDir(), fileRelPath);
	}
	
	public File getWorkingDir() {
		assertNotNull(testsWorkingDir); // Maybe use workingDir = "../_runtime-tests" instead
		File file = new File(testsWorkingDir);
		assertTrue(file.exists() && file.isDirectory());
		return file;
	}
	
}
