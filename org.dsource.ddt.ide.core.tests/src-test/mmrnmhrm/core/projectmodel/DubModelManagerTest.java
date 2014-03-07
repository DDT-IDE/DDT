package mmrnmhrm.core.projectmodel;

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

import org.eclipse.core.resources.IProject;
import org.junit.Test;

import dtool.dub.CommonDubTest.DubBundleChecker;
import dtool.dub.DubBundle;
import dtool.dub.DubBundleDescription;
import dtool.dub.DubManifestParser;
import dtool.dub.DubManifestParserTest;

public class DubModelManagerTest extends BaseDubModelManagerTest {
	
	@Test
	public void testShutdown() throws Exception { testShutdown$(); }
	public void testShutdown$() throws Exception {
		DubModelManager dmm = new DubModelManager(new DubModel()); 
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
	public static Path DUB_WORKSPACE = DubManifestParserTest.DUB_WORKSPACE;
	
	protected static final DubBundleChecker FOO_LIB_BUNDLE = bundle(DUB_WORKSPACE.resolve("foo_lib"), 
		null, "foo_lib", DEFAULT_VERSION, paths("src", "src2"));
	protected static final DubBundleChecker BAR_LIB_BUNDLE = bundle(DUB_WORKSPACE.resolve("bar_lib"), 
		null, "bar_lib", DEFAULT_VERSION, paths("source"));
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		IProject project;
		long taskCount = getModelAgent().getSubmittedTaskCount();
		project = createAndOpenDeeProject(DUB_TEST, true).getProject();
		// check no change:
		assertTrue(getModelAgent().getSubmittedTaskCount() == taskCount); 
		assertTrue(DubModel.getBundleInfo(DUB_TEST) == null);
		
		DubModelManager.getDefault().syncPendingUpdates();
		runBasicTestSequence______________(project);
		project.delete(true, null); // cleanup
		assertTrue(DubModel.getBundleInfo(project.getName()) == null);
		assertTrue(getDubContainer(project) == null);
		
		// Verify code path where a project that already has dub manifest is added.
		project = createAndOpenProject(DUB_TEST, true);
		LatchRunnable latch = writeDubJson(project, "{"+ jsEntry("name", "xptobundle")+ jsFileEnd());
		setupStandardDeeProject(project);
		DubBundleDescription unresolvedBundleDesc = getExistingDubBundleInfo(project.getName());
		latch.releaseAll();
		
		Path location = project.getLocation().toFile().toPath();
		checkDubModel(unresolvedBundleDesc, project, 
			main(location, null, "xptobundle", DubBundle.DEFAULT_VERSION, srcFolders(), rawDeps()));
		
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
			readFileContents(DUB_WORKSPACE.resolve("XptoBundle/dub.json")),
			
			project,
			main(location, null, "xptobundle", DEFAULT_VERSION, srcFolders("src", "src-test"), 
				rawDeps("foo_lib"), 
				FOO_LIB_BUNDLE,
				BAR_LIB_BUNDLE
			)
		);
		
		writeDubJsonAndCheckDubModel("{"+ jsEntry("nameMISSING", "xptobundle")+ jsFileEnd(),
			project, 
			bundle(DubManifestParser.ERROR_BUNDLE_NAME_UNDEFINED, IGNORE_STR));
		
		writeDubJsonAndCheckDubModel("{"+ jsEntry("name", "xptobundle")+ jsFileEnd(),
			project, 
			main(location, null, "xptobundle", DEFAULT_VERSION, srcFolders(), rawDeps()));
		
		
		// Test errors occurring from running dub describe
		writeDubJsonAndCheckDubModel(
			readFileContents(DUB_WORKSPACE.resolve("ErrorBundle_MissingDep/dub.json")),
			
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
		writeDubJson(project, dubTestJson).releaseAll();
		
		IProject libProject = createAndOpenDeeProject(DUB_LIB, true).getProject();
		String dubLibJson = jsObject(jsEntry("name", "dub_lib"));
		writeDubJson(libProject, dubLibJson).releaseAll();
		
		DubModelManager.getDefault().syncPendingUpdates();
		DubBundleDescription dubBundle = getExistingDubBundleInfo(project.getName());
		checkFullyResolvedCode(project, dubBundle, 
			main(loc(project), null, "dub_test", DEFAULT_VERSION, srcFolders(), 
				rawDeps("dub_lib"), 
				new ProjDepChecker(libProject, "dub_lib")
			));
		
		Path libProjectLocation = loc(libProject);
		libProject.delete(true, null);
		DubModelManager.getDefault().syncPendingUpdates();
		
		checkFullyResolvedCode(project, dubBundle, 
			main(loc(project), null, "dub_test", DEFAULT_VERSION, srcFolders(), 
				rawDeps("dub_lib"), 
				bundle(libProjectLocation, "dub_lib")
			));
	}
	
	protected Path loc(IProject project) {
		return project.getLocation().toFile().toPath();
	}
}