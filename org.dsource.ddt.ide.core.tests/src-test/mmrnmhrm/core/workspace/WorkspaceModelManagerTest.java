package mmrnmhrm.core.workspace;

import static dtool.dub.CommonDubTest.ERROR_DUB_RETURNED_NON_ZERO;
import static dtool.dub.CommonDubTest.bundle;
import static dtool.dub.CommonDubTest.main;
import static dtool.dub.CommonDubTest.paths;
import static dtool.dub.CommonDubTest.rawDeps;
import static dtool.dub.DubBundle.DEFAULT_VERSION;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import melnorme.utilbox.concurrency.LatchRunnable;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.workspace.WorkspaceModel;
import mmrnmhrm.core.workspace.WorkspaceModelManager;

import org.eclipse.core.resources.IProject;
import org.junit.Test;

import dtool.dub.CommonDubTest.DubBundleChecker;
import dtool.dub.DubBundle;
import dtool.dub.DubBundleDescription;
import dtool.dub.DubManifestParser;
import dtool.dub.DubManifestParserTest;
import dtool.tests.CommonDToolTest;

public class WorkspaceModelManagerTest extends AbstractDubModelManagerTest {
	
	@Test
	public void testShutdown() throws Exception { testShutdown$(); }
	public void testShutdown$() throws Exception {
		WorkspaceModelManager dmm = new WorkspaceModelManager(new WorkspaceModel()); 
		dmm.initializeModelManager();
		final CountDownLatch latch = new CountDownLatch(1);
		
		dmm.modelAgent.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				latch.countDown();
				new CountDownLatch(1).await(); // wait until interrupted
				throw DeeCore.createCoreException("error", new Exception());
			}
		});
		latch.await();
		// Test that shutdown happens successfully even with pending task, and no log entries are made.
		dmm.shutdownManager(); 
	}
	
	public static final String DUB_TEST = "DubTest";
	public static final String DUB_LIB = "DubLib";
	public static final Path DUB_TEST_BUNDLES = DubManifestParserTest.DUB_TEST_BUNDLES;
	
	protected static final DubBundleChecker FOO_LIB_BUNDLE = bundle(DUB_TEST_BUNDLES.resolve("foo_lib"), 
		null, "foo_lib", DEFAULT_VERSION, paths("src", "src2"));
	protected static final DubBundleChecker BAR_LIB_BUNDLE = bundle(DUB_TEST_BUNDLES.resolve("bar_lib"), 
		null, "bar_lib", DEFAULT_VERSION, paths("source"));
	
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		deleteProject(DUB_TEST); // In case drop-to-frame is used during debugging.
		_awaitModelUpdates_();
		
		assertTrue(model.getBundleInfo(project(DUB_TEST)) == null);
		
		IProject project;
		long taskCount;
		
		// Test project with dub.json, but no D nature
		LatchRunnable latchRunnable = new LatchRunnable();
		getModelAgent().submit(latchRunnable);
		taskCount = getModelAgent().getSubmittedTaskCount();
		project = createAndOpenProject(DUB_TEST, true).getProject();
		writeDubJson(project, jsObject(jsEntry("name", "xptobundle")));
		// check no changes or updates submitted:
		assertTrue(getModelAgent().getSubmittedTaskCount() == taskCount);
		assertTrue(model.getBundleInfo(project) == null);
		latchRunnable.releaseAll();
		
		// Ensure non-d projects dont provoke updates
		deleteProject(DUB_TEST);
		assertTrue(getModelAgent().getSubmittedTaskCount() == taskCount);
		
		// Test project with D nature, but no dub.json
		taskCount = getModelAgent().getSubmittedTaskCount();
		project = createAndOpenDeeProject(DUB_TEST, true).getProject();
		// check no changes or updates submitted:
		assertTrue(getModelAgent().getSubmittedTaskCount() == taskCount); 
		assertTrue(model.getBundleInfo(project) == null);
		
		_awaitModelUpdates_();
		runBasicTestSequence______________(project);
		project.delete(true, null); // cleanup
		assertTrue(model.getBundleInfo(project) == null);
		assertTrue(model.getProjectInfo(project) == null);
		
		// Verify code path where a non-D project that already has dub manifest is made a D project.
		_awaitModelUpdates_();
		project = createAndOpenProject(DUB_TEST, true);
		LatchRunnable modelLatch = writeDubJsonWithModelLatch(project, jsObject(jsEntry("name", "xptobundle")));
		setupStandardDeeProject(project);
		System.out.println("--------- .project contents: ");
		System.out.println(readFileContents(project.getFile(".project")));
		
		DubBundleDescription unresolvedBundleDesc = getExistingDubBundleInfo(project);
		modelLatch.releaseAll();
		checkDubModel(unresolvedBundleDesc, project, 
			main(loc(project), null, "xptobundle", DubBundle.DEFAULT_VERSION, srcFolders(), rawDeps()));
		
		testProjectBPDependencies();
	}
	
	public void runBasicTestSequence______________(IProject project) throws Exception {
		Path location = project.getLocation().toFile().toPath();
		
		writeDubJsonAndCheckDubModel("{"+ jsEntry("name", "xptobundle")+ jsFileEnd(),
			project, 
			main(location, null, "xptobundle", DEFAULT_VERSION, srcFolders(), rawDeps()));
		
		writeDubJsonAndCheckDubModel("{"+
			jsEntry("name", "xptobundle")+
			jsEntry("importPaths", jsArray("src", "src-test"))+
			jsEntry("version", "2.1")+
			jsFileEnd(),
			
			project, 
			main(location, null, "xptobundle", "2.1", srcFolders("src", "src-test"), rawDeps())
		);
		
		
		writeDubJsonAndCheckDubModel(
			readFileContents(DUB_TEST_BUNDLES.resolve("XptoBundle/dub.json")),
			
			project,
			main(location, null, "xptobundle", DEFAULT_VERSION, srcFolders("src", "src-test", "src-import"), 
				rawDeps("foo_lib"), 
				FOO_LIB_BUNDLE,
				BAR_LIB_BUNDLE
			)
		);
		
		writeDubJsonAndCheckDubModel("{"+ jsEntry("nameMISSING", "xptobundle")+ jsFileEnd(),
			project, 
			bundle(DubManifestParser.ERROR_BUNDLE_NAME_UNDEFINED, CommonDToolTest.IGNORE_STR));
		
		writeDubJsonAndCheckDubModel("{"+ jsEntry("name", "xptobundle")+ jsFileEnd(),
			project, 
			main(location, null, "xptobundle", DEFAULT_VERSION, srcFolders(), rawDeps()));
		
		
		// Test errors occurring from running dub describe
		writeDubJsonAndCheckDubModel(
			readFileContents(DUB_TEST_BUNDLES.resolve("ErrorBundle_MissingDep/dub.json")),
			
			project,
			main(location, ERROR_DUB_RETURNED_NON_ZERO, "ErrorBundle_MissingDep", DEFAULT_VERSION, srcFolders("src"), 
				rawDeps("foo_lib", "NonExistantDep"), 
				FOO_LIB_BUNDLE,
				bundle("" , "NonExistantDep")
			)
		);
		
	}
	
	public void testProjectBPDependencies() throws Exception {
		IProject project = createAndOpenDeeProject(DUB_TEST, true).getProject();
		String dubTestJson = jsObject(jsEntry("name", "dub_test"), 
			jsEntryValue("dependencies", "{ \"dub_lib\": \"~master\"}"));
		writeDubJson(project, dubTestJson);
		
		IProject libProject = createAndOpenProject(DUB_LIB, true).getProject();
		Path libProjectLocation = loc(libProject);
		String dubLibJson = jsObject(jsEntry("name", "dub_lib"));
		writeDubJson(libProject, dubLibJson);
		
		// DUB_LIB project buildpath entry not on DUB_TEST yet
		_awaitModelUpdates_();
		DubBundleDescription dubBundle = getExistingDubBundleInfo(project);
		checkFullyResolvedCode(project, dubBundle, 
			main(loc(project), null, "dub_test", DEFAULT_VERSION, srcFolders(), 
				rawDeps("dub_lib"), 
				bundle(libProjectLocation, "dub_lib")
			));
		
		setupStandardDeeProject(libProject);
		_awaitModelUpdates_();
		// Check project buildpath entry has been added
		checkFullyResolvedCode(project, dubBundle, 
			main(loc(project), null, "dub_test", DEFAULT_VERSION, srcFolders(), 
				rawDeps("dub_lib"), 
				new ProjDepChecker(libProject, "dub_lib")
			));
		
		libProject.delete(true, null);
		_awaitModelUpdates_();
		// Check project buildpath entry has been removed
		checkFullyResolvedCode(project, dubBundle, 
			main(loc(project), null, "dub_test", DEFAULT_VERSION, srcFolders(), 
				rawDeps("dub_lib"), 
				bundle(libProjectLocation, "dub_lib")
			));
	}
	
}