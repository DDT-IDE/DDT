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


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IScriptProject;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import dtool.dub.DubDescribeTest;
import dtool.dub.DubBundle.DubBundleDescription;
import melnorme.utilbox.concurrency.IExecutorAgent;
import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.WorkspaceUtils;
import mmrnmhrm.tests.BaseDeeTest;

/**
 * Utilities for manipualtion Dub projects
 */
public abstract class DubCommonTest extends BaseDeeTest {
	
	@BeforeClass
	public static void initDubRepositoriesPath() {
		DubDescribeTest.initDubRepositoriesPath();
	}
	@AfterClass
	public static void cleanupDubRepositoriesPath() {
		DubDescribeTest.cleanupDubRepositoriesPath();
	}
	
	public static String readFileContents(Path path) throws IOException {
		assertTrue(path.isAbsolute());
		return FileUtil.readStringFromFile(path.toFile(), StringUtil.UTF8);
	}
	
	public static void writeStringToFile(IScriptProject dubTestProject, String name, String contents) 
			throws CoreException {
		IFile file = dubTestProject.getProject().getFile(name);
		WorkspaceUtils.writeFile(file, new ByteArrayInputStream(contents.getBytes(StringUtil.UTF8)));
	}
	
	
	public static String jsEntry(String key, String value) {
		return "\""+key+"\" : \""+value+"\",";
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
		return DubProjectModel.getDefault().internal_getExecutorAgent();
	}
	
	protected static DubProjectModel getProjectModel() {
		return DubProjectModel.getDefault();
	}
	
	protected static DubBundleDescription getDubProjectInfo(String projectName) {
		return DubProjectModel.getDefault().getBundleInfo(projectName);
	}
	
	protected static void writeDubJson_AndSync(IScriptProject dubTestProject, String contents) throws CoreException {
		writeStringToFile(dubTestProject, "dub.json", contents);
		DubProjectModel.getDefault().syncPendingUpdates();
	}
	
}