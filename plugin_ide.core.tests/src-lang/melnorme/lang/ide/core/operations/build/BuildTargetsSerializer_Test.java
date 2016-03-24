/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.core.operations.build;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import org.junit.Test;

import melnorme.lang.ide.core.LangCore;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.tests.CommonTest;

public class BuildTargetsSerializer_Test extends CommonTest {
	
	public static BuildTargetData bt(String targetName, boolean enabled, String buildArguments, String checkArguments,
			String executablePath) {
		return new BuildTargetData(
			targetName, 
			enabled, 
			buildArguments, 
			checkArguments, 
			executablePath
		);
	}
	
	protected final BuildManager buildMgr = LangCore.getBuildManager();
	protected BuildTargetsSerializer serializer = buildMgr.createSerializer();
	
	@Test
	public void testname() throws Exception { testname$(); }
	public void testname$() throws Exception {
		testSerialize(new ArrayList2<>());
		testSerialize(new ArrayList2<>(createBuildTarget(false, "", null, null, null)));
		testSerialize(new ArrayList2<>(createBuildTarget(true, "", "-opt", "-check", "foo.exe")));
		testSerialize(new ArrayList2<>(
				createBuildTarget(false, "", "", "", ""),
				createBuildTarget(true, "blah", "-opt", null, "foo.exe"),
				createBuildTarget(true, "xxx", null, "-check", "foo/bar.ooo")
		));
	}
	
	protected BuildTargetData createBuildTarget(boolean enabled, String targetName, String buildArgs, String checkArgs,
			String filePath) {
		return bt(targetName, enabled, buildArgs, checkArgs, filePath);
	}
	
	protected void testSerialize(ArrayList2<BuildTargetData> buildTargetsData) {
		try {
			String xml = serializer.writeProjectBuildInfo(buildTargetsData);
			assertAreEqual(buildTargetsData, serializer.readProjectBuildInfo(xml));
		} catch(CommonException e) {
			assertFail();
		}
	}
	
}