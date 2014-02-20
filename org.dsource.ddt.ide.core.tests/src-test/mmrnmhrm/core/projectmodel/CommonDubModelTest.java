/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.projectmodel;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;

import melnorme.utilbox.concurrency.IExecutorAgent;
import melnorme.utilbox.concurrency.LatchRunnable;
import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.WorkspaceUtils;
import mmrnmhrm.tests.BaseDeeTest;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import dtool.dub.CommonDubTest;
import dtool.dub.DubBundleDescription;
import dtool.dub.DubDescribeParserTest;

/**
 * Utilities for manipualtion Dub projects
 */
public abstract class CommonDubModelTest extends BaseDeeTest {
	
	@BeforeClass
	public static void initDubRepositoriesPath() {
		DubModelManager.startDefault();
		DubDescribeParserTest.initDubRepositoriesPath();
	}
	@AfterClass
	public static void cleanupDubRepositoriesPath() {
		DubDescribeParserTest.cleanupDubRepositoriesPath();
	}
	
	public static String readFileContents(Path path) throws IOException {
		assertTrue(path.isAbsolute());
		return FileUtil.readStringFromFile(path.toFile(), StringUtil.UTF8);
	}
	
	public static void writeStringToFile(IProject project, String name, String contents) 
			throws CoreException {
		IFile file = project.getFile(name);
		WorkspaceUtils.writeFile(file, new ByteArrayInputStream(contents.getBytes(StringUtil.UTF8)));
	}
	
	
	public static String jsEntry(String key, String value) {
		return "\""+key+"\" : \""+value+"\",";
	}
	
	public static String jsFileEnd() {
		return "\"dummyEndKey\" : null } ";
	}
	
	public static String jsEntry(String key, CharSequence value) {
		return "\"" + key + "\" : " + jsToString(value) + ",";
	}
	
	private static String jsToString(CharSequence value) {
		if(value instanceof String) {
			return "\""+value+"\"";
		} else {
			return value.toString();
		}
	}
	
	public static StringBuilder jsArray(CharSequence... objs) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (CharSequence obj : objs) {
			sb.append(jsToString(obj));
			sb.append(",");
		}
		sb.append("]");
		return sb;
	}
	
	protected static IExecutorAgent getDubExecutorAgent() {
		return DubModelManager.getDefault().internal_getExecutorAgent();
	}
	
	protected static DubModelManager getProjectModel() {
		return DubModelManager.getDefault();
	}
	
	protected static DubBundleDescription getExistingDubBundleInfo(String projectName) {
		return assertNotNull(DubModel.getBundleInfo(projectName));
	}
	
	protected static LatchRunnable writeDubJson(IProject project, String contents) throws CoreException {
		LatchRunnable latchRunnable = new LatchRunnable();
		getDubExecutorAgent().submit(latchRunnable);
		writeStringToFile(project, "dub.json", contents);
		return latchRunnable;
	}
	
	public static Path[] srcFolders(String... elems) {
		return CommonDubTest.paths(elems);
	}
	
}