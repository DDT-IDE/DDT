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

import static melnorme.lang.ide.core.operations.build.BuildTargetsSerializer_Test.bt;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;

import melnorme.lang.ide.core.BundleInfo;
import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.launch.BuildTargetSource;
import melnorme.lang.ide.core.launch.CompositeBuildTargetSettings;
import melnorme.lang.ide.core.launch.LaunchMessages;
import melnorme.lang.ide.core.operations.ILangOperationsListener_Default.IOperationConsoleHandler;
import melnorme.lang.ide.core.operations.ILangOperationsListener_Default.NoopOperationConsoleHandler;
import melnorme.lang.ide.core.operations.build.BuildManager.BuildType;
import melnorme.lang.ide.core.project_model.LangBundleModel;
import melnorme.lang.ide.core.tests.BuildTestsHelper;
import melnorme.lang.ide.core.tests.SampleProject;
import melnorme.lang.tooling.bundle.BuildConfiguration;
import melnorme.lang.tooling.bundle.BuildTargetNameParser;
import melnorme.lang.tooling.data.StatusException;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Indexable;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;
import melnorme.utilbox.tests.CommonTest;

public class BuildManager_Test extends CommonTest {
	
	public static final BuildTargetData sampleBT_A = 
			bt("TargetA", true, false, null, null);
	public static final BuildTargetData sampleBT_B = 
			bt("TargetB", false, true, "B: build_args", "B: exe_path");
	public static final BuildTargetData sampleBT_STRICT = 
			bt("ConfigA#strict", true, true, "S: build_args", "S: exe_path");
	
	public static final ArrayList2<BuildTargetData> DEFAULT_TARGETS = list(
		sampleBT_A,
		sampleBT_B,
		sampleBT_STRICT
	);
	
	protected final BuildManager buildMgr = new TestsBuildManager(LangCore.getBundleModel());

	public class TestsBuildManager extends BuildManager {
		public TestsBuildManager(LangBundleModel bundleModel) {
			super(bundleModel);
		}
		
		@Override
		public BuildTargetNameParser getBuildTargetNameParser() {
			return new BuildTargetNameParser() {
				@Override
				public String getNameSeparator() {
					return "#";
				};
			};
		}
		
		@Override
		protected Indexable<BuildType> getBuildTypes_do() {
			return list(
				new SampleBuildType("default"),
				new SampleStrictBuildType("strict")
			);
		}
		
		@Override
		protected ArrayList2<BuildTarget> getDefaultBuildTargets(IProject project, BundleInfo newBundleInfo) {
			return createBuildTargets(project, DEFAULT_TARGETS);
		}
	}
	
	protected String SEP = buildMgr.getBuildTargetNameParser().getNameSeparator();
	
	public ArrayList2<BuildTarget> createBuildTargets(IProject project, Indexable<BuildTargetData> buildTargetsData) {
		try {
			return buildTargetsData.mapx((buildTargetData) -> {
				return buildMgr.createBuildTarget(project, buildTargetData);
			});
		} catch(CommonException e) {
			throw assertFail();
		}
	}

	public class SampleStrictBuildType extends BuildType {
		public SampleStrictBuildType(String name) {
			super(name);
		}
		
		@Override
		public String getDefaultBuildArguments(BuildTarget bt) throws CommonException {
			return "default: build_args";
		}
		
		@Override
		public CommonBuildTargetOperation getBuildOperation(BuildTarget bt, IOperationConsoleHandler opHandler, 
				Path buildToolPath, String buildArguments) 
				throws CommonException {
			return new CommonBuildTargetOperation(buildMgr, bt, opHandler, buildToolPath, buildArguments) {
				@Override
				protected void processBuildOutput(ExternalProcessResult processResult, IProgressMonitor pm)
						throws CommonException, OperationCancellation {
				}
			};
		}
		
		@Override
		protected BuildConfiguration getValidBuildconfiguration(String buildConfigName, BundleInfo bundleInfo)
				throws CommonException {
			if(list("ConfigA", "ConfigB").contains(buildConfigName)) {
				return new BuildConfiguration(buildConfigName, null);
			}
			throw new CommonException(BuildManagerMessages.BuildConfig_NotFound(buildConfigName));
		}
	}

	public class SampleBuildType extends SampleStrictBuildType {
		
		public SampleBuildType(String name) {
			super(name);
		}
		
		@Override
		protected BuildConfiguration getValidBuildconfiguration(String buildConfigName,
				BundleInfo bundleInfo) throws CommonException {
			// Allow implicit configurations
			return new BuildConfiguration(buildConfigName, null);
		}
	}

	
	protected SampleProject sampleProject;
	protected IProject project;
	
	protected SampleProject initSampleProject() throws CoreException, CommonException {
		this.sampleProject = new SampleProject(getClass().getSimpleName());
		this.project = sampleProject.getProject();
		return sampleProject;
	}
	
	protected final BundleInfo bundleInfo = BuildTestsHelper.createSampleBundleInfoA("SampleBundle", null);
	
	/* -----------------  ----------------- */
	
	@Test
	public void test() throws Exception { test$(); }
	public void test$() throws Exception {
		
		try(SampleProject sampleProj = initSampleProject()){
			
			buildMgr.loadProjectBuildInfo(project, bundleInfo);
			
			ProjectBuildInfo buildInfo = buildMgr.getBuildInfo(project);
			assertNotNull(buildInfo);
			checkBuildTargets(buildInfo.getBuildTargets().toArrayList(), list(
				sampleBT_A,
				sampleBT_B,
				sampleBT_STRICT)
			);

			assertEquals(
				buildMgr.getBuildTarget(project, "TargetA", true).getData(),
				sampleBT_A);
			assertEquals(
				buildMgr.getBuildTarget(project, "TargetB", true).getData(),
				sampleBT_B);
			verifyThrows(
				() -> buildMgr.getBuildTarget(project, "TargetA#default", true).getData(),
				CommonException.class,
				LaunchMessages.BuildTarget_NotFound);
			
			verifyThrows(
				() -> buildMgr.getBuildTarget(project, "TargetA"+SEP+"bad_config", false).getData(),
				CommonException.class,
				"No such build type: `bad_config`"); // Build Type not found
			
			assertEquals(
				buildMgr.getBuildTarget(project, "ImplicitTarget"+SEP+"default", false).getData(),
				bt("ImplicitTarget"+SEP+"default", false, false, null, null));
			
			verifyThrows(
				() -> buildMgr.getBuildTarget(project, "ImplicitTarget"+SEP+"strict", false).getData(),
				CommonException.class,
				"Build configuration `ImplicitTarget` not found"); // Config not found
			
			
			testBuildOperation();
		}
		
		try(SampleProject sampleProj = initSampleProject()){
			testSaveLoadProjectInfo();
		}
	}
	
	protected void testSaveLoadProjectInfo() throws CommonException {
		
		SampleStrictBuildType buildType = new SampleStrictBuildType("default");
		BuildConfiguration buildConfig = new BuildConfiguration("configA", null);
		
		BuildTarget btA = new BuildTarget(project, bundleInfo, bt("TargetA", false, true, "new1", "new3"), 
			buildType, buildConfig);
		BuildTarget btNonExistentButValid = new BuildTarget(project, bundleInfo, 
			new BuildTargetData("TargetA" + SEP + "default", true, false), 
			buildType, buildConfig);
		BuildTarget btNonExistent = new BuildTarget(project, bundleInfo, 
			new BuildTargetData("TargetA" + SEP + "NonExistentType", false, true), 
			buildType, buildConfig);
		
		ProjectBuildInfo newProjectBuildInfo = new ProjectBuildInfo(buildMgr, project, bundleInfo, 
			new ArrayList2<>(btA, btNonExistentButValid, btNonExistent));
		buildMgr.setProjectBuildInfoAndSave(project, newProjectBuildInfo);
		
		buildMgr.getBuildModel().removeProjectInfo(project);
		assertTrue(buildMgr.getBuildModel().getProjectInfo(project) == null);
		buildMgr.loadProjectBuildInfo(project, bundleInfo);
		
		ProjectBuildInfo buildInfo = buildMgr.getBuildInfo(project);
		checkBuildTargets(buildInfo.getBuildTargets().toArrayList(), list(
			bt("TargetA", false, true, "new1", "new3"), // Ensure TargetA uses previous settings
			sampleBT_B,
			sampleBT_STRICT)
		);
		
	}
	
	public void checkBuildTargets(Indexable<BuildTarget> buildTargets, Indexable<BuildTargetDataView> expectedSettings) {
		assertTrue(buildTargets.size() == expectedSettings.size());
		for(int ix = 0; ix < buildTargets.size(); ix++) {
			BuildTargetDataView expectedData = expectedSettings.get(ix);
			assertTrue(buildTargets.get(ix).getData().equals(expectedData));
		}
	}
	
	/* -----------------  ----------------- */
	
	@Test
	public void test_compositeBuildTargetSettings() throws Exception { test_compositeBuildTargetSettings$(); }
	public void test_compositeBuildTargetSettings$() throws Exception {
		
		try(SampleProject sampleProj = initSampleProject()){
			
			assertEquals(
				btSettings("TargetA", null, null).getValidBuildTarget().getData(), 
				bt("TargetA", true, false, null, null));
			
			assertEquals(
				btSettings("TargetB", null, null).getValidBuildTarget().getData(), 
				bt("TargetB", false, true, "B: build_args", "B: exe_path"));
			
			assertEquals(
				btSettings("TargetB", "ARGS", "EXEPATH").getValidBuildTarget().getData(), 
				bt("TargetB", false, true, "ARGS", "EXEPATH"));
			
			assertEquals(
				btSettings("ImplicitTarget", "ARGS", "EXEPATH").getValidBuildTarget().getData(), 
				bt("ImplicitTarget", false, false, "ARGS", "EXEPATH"));
		}
		
	}
	
	protected CompositeBuildTargetSettings btSettings(
			String buildTargetName, String buildArguments, String artifactPath) {
		return getBuiltTargetSettingsValidator(sampleProject.getName(), buildTargetName, buildArguments, artifactPath);
	}
	
	protected CompositeBuildTargetSettings getBuiltTargetSettingsValidator(
			String projectName, String buildTargetName, String buildArguments, String artifactPath) {
		
		BuildTargetSource buildTargetSource = new BuildTargetSource() {
			@Override
			protected BuildManager getBuildManager() {
				return buildMgr;
			}
			
			@Override
			public String getProjectName() {
				return projectName;
			}
			
			@Override
			public String getBuildTargetName() {
				return buildTargetName;
			}
		};
		
		CompositeBuildTargetSettings btSettings = new CompositeBuildTargetSettings(buildTargetSource) {
			@Override
			public String getBuildArguments() {
				return buildArguments;
			}
			
			@Override
			public String getExecutablePath() {
				return artifactPath;
			}
		};
		return btSettings;
	}
	
	/* -----------------  ----------------- */
	
	@Test
	public void testBuildType() throws Exception { testBuildType$(); }
	public void testBuildType$() throws Exception {
		
		BuildManager.BuildType buildType = new SampleBuildType("default");
		
		try(SampleProject sampleProj = initSampleProject()){
			
			IProject project = sampleProj.getProject();

			ProjectBuildInfo buildInfo = buildMgr.getValidBuildInfo(project);
			BundleInfo bundleInfo = buildInfo.getBundleInfo();

			BuildTargetData targetA = bt("SampleTarget", true, true, null, null);
			BuildTarget buildTargetA = BuildTarget.create(project, bundleInfo, targetA, buildType, "");
			verifyThrows(() -> buildTargetA.getEffectiveValidExecutablePath(), CommonException.class, 
				LaunchMessages.MSG_BuildTarget_NoExecutableAvailable());
			
			BuildTargetData target2 = bt("SampleTarget2", true, true, "sample args", "sample path");
			BuildTarget buildTarget2 = BuildTarget.create(project, bundleInfo, target2, buildType, "");
			
			assertAreEqual(buildTarget2.getEffectiveValidExecutablePath(), "sample path");
			
		}
	}
	
	protected NoopOperationConsoleHandler consoleHandler = new NoopOperationConsoleHandler();
	
	protected void testBuildOperation() throws CommonException, StatusException, CoreException {
		BuildTarget btA = buildMgr.getBuildTarget(project, "TargetA", true);
		assertTrue(btA.getData().getBuildArguments() == null);
		
		BuildTarget btB = buildMgr.getBuildTarget(project, "TargetB", true);
		assertTrue(btB.getData().getBuildArguments() != null);
		
		assertAreEqual(
			btA.getBuildOperation(consoleHandler, path("blah")).getEffectiveEvaluatedArguments(), 
			list("default:", "build_args")
		);
		
		assertAreEqual(
			btB.getBuildOperation(consoleHandler, path("blah")).getEffectiveEvaluatedArguments(), 
			list("B:", "build_args")
		);
	}
}