/*******************************************************************************
 * Copyright (c) 2014, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.engine_client;

import static dtool.engine.CommonSemanticManagerTest.resolutionKey;
import static dtool.tests.MockCompilerInstalls.DEFAULT_DMD_INSTALL_EXE_PATH;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;

import melnorme.lang.ide.core.tests.CommonCoreTest;
import melnorme.lang.ide.core.tests.LangCoreTestResources;
import melnorme.lang.tooling.engine.completion.CompletionSearchResult;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.MiscUtil;
import mmrnmhrm.core.DeeCorePreferences;
import mmrnmhrm.core.engine_client.DToolClient.ClientModuleParseCache;
import mmrnmhrm.tests.DeeCoreTestResources;
import mmrnmhrm.tests.TestFixtureProject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.compiler.env.ModuleSource;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.junit.Ignore;
import org.junit.Test;

import dtool.dub.BundlePath;
import dtool.dub.CommonDubTest;
import dtool.dub.DubManifestParserTest;
import dtool.engine.BundleResolution;
import dtool.engine.ModuleParseCache_Test;
import dtool.engine.SemanticManager;
import dtool.engine.SemanticManager.ManifestUpdateOptions;
import dtool.resolver.DefUnitResultsChecker;
import dtool.tests.DToolTestResources;
import dtool.tests.MockCompilerInstalls;

public class DToolClient_Test extends CommonCoreTest {
	
	protected static final DToolClient client = DToolClient.getDefault();
	
	protected TestFixtureProject testsProject;
	
	@Ignore // relative paths no longer supported
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		String modulePath = "relative/path/foo.d";
		ModuleSource moduleSource = new ModuleSource(modulePath, "module blah;");
		Path filePath = DToolClient.getPathHandleForModuleSource(moduleSource);
		
		ClientModuleParseCache clientModuleCache = client.getClientModuleCache();
		assertEquals(clientModuleCache.getParsedModuleOrNull(filePath, moduleSource).module.getName(), "blah");
		assertEquals(clientModuleCache.getExistingParsedModuleNode(filePath).getName(), "blah");
		
		testCodeCompletion(moduleSource, 0, 
			"blah");
		testCodeCompletion(new ModuleSource(modulePath, "module xpto;"), 0, 
			"xpto");
		assertTrue(client.getServerSemanticManager().getParseCache().getEntry(MiscUtil.createPath(modulePath))
			.isWorkingCopy() == false);
		
		Path path = DToolTestResources.getTestResourcePath().resolve("dummy__non_existant.d");
		assertTrue(path.isAbsolute());
		testCodeCompletion(new ModuleSource(path.toString(), "module blah;"), 0, 
				"blah");
		
		// Error case
		try {
			client.doCodeCompletion((Path) null, 0, null);
			assertFail();
		} catch (CoreException e) {
		}
		
	}
	
	protected void testCodeCompletion(ModuleSource moduleSource, int offset, String... results) throws CoreException {
		CompletionSearchResult cc = client.runCodeCompletion(moduleSource, offset, 
			MockCompilerInstalls.DEFAULT_DMD_INSTALL_EXE_PATH);
		new DefUnitResultsChecker(cc.getResults()).simpleCheckResults(results);
	}
	
	@Test
	public void testUpdates() throws Exception { testUpdates________________(); }
	public void testUpdates________________() throws Exception {
		testsProject = new DToolFixtureProject();
		doTestUpdates();
		
		// Test again with module under a source folder. It should not make much difference, but never know..
		testsProject = new DToolFixtureProject() {
			@Override
			protected void createContents() throws CoreException {
				super.createContents();
				DeeCoreTestResources.addSourceFolder(sourceFolder);
			}
		};
		doTestUpdates();
	}
	
	protected class DToolFixtureProject extends TestFixtureProject {
		
		protected IFolder sourceFolder;
		
		private DToolFixtureProject() throws CoreException {
			super("DToolClientTest");
		}
		
		@Override
		protected void createContents() throws CoreException {
			sourceFolder = project.getFolder("source");
			LangCoreTestResources.createFolderFromCoreTestsResource("simple-source", sourceFolder);
			writeManifestFile();
		}
	}
	
	protected void doTestUpdates() throws CoreException, IOException {
		IFolder SRC_FOLDER = testsProject.getFolder("source");
		IFile basic_foo = exists(SRC_FOLDER.getFile("basic_foo.d"));
		
		doCodeCompletion(basic_foo, 0, "basic_foo", "barLibFunction");
		
		updateFileContents(basic_foo, "module change1;");
		doCodeCompletion(basic_foo, 0, "change1");
		
		updateFileContents(basic_foo, "module change2;");
		doCodeCompletion(basic_foo, 0, "change2");
		
		
		IFile newFile = SRC_FOLDER.getFile("new_file.d");
		updateFileContents(newFile, "module new_file;"); 
		checkModuleContains(newFile, "new_file");
		
		IFolder newPackage = createFolder(SRC_FOLDER.getFolder("new_package"));
		IFile newFile2 = newPackage.getFile("new_file2.d");
		updateFileContents(newFile2, "module new_file2;");
		checkModuleContains(newFile2, "new_package.new_file2", "new_file2/");
		
		deleteResource(newPackage);
		checkModuleExists(newFile2, "new_package.new_package_file", false);
		
		deleteResource(newFile);
		checkModuleExists(newFile, "new_file", false);
		
		testUpdatesToWorkingCopy();
	}
	
	protected <T extends IResource> T exists(T resource) {
		assertTrue(resource.exists());
		return resource;
	}
	
	public static void updateFileContents(IFile file, String contents) throws IOException, CoreException {
		ModuleParseCache_Test.writeToFileAndUpdateMTime(path(file.getLocation()), contents);
		file.refreshLocal(0, null);
	}
	
	protected void checkModuleContains(IFile file, String moduleName) throws CoreException {
		checkModuleContains(file, moduleName, moduleName + "/");
	}
	
	protected void checkModuleContains(IFile file, String moduleName, String... results) throws CoreException {
		checkModuleExists(file, moduleName, true);
		doCodeCompletion(file, 0, results);
	}
	
	protected void checkModuleExists(IFile file, String moduleName, boolean exists) {
		BundlePath bundlePath = BundlePath.create(file.getProject().getLocation().toFile().toPath());
		BundleResolution sr;
		try {
			SemanticManager sm = client.getServerSemanticManager();
			sr = sm.getUpdatedResolution(resolutionKey(bundlePath, DEFAULT_DMD_INSTALL_EXE_PATH), 
				new ManifestUpdateOptions(DeeCorePreferences.getEffectiveDubPath()));
		} catch (CommonException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
		HashSet<String> modules = sr.findModules(moduleName);
		assertTrue(modules.contains(moduleName) == exists);
	}
	
	protected void testUpdatesToWorkingCopy() throws CoreException, IOException {
		ISourceModule sourceModule = testsProject.getSourceModule("source/basic_foo.d");
		IFile moduleFile = (IFile) sourceModule.getResource();
		sourceModule.discardWorkingCopy();
		
		String originalFileContents = "module wc_change0;";
		updateFileContents(moduleFile, originalFileContents);
		
		sourceModule.becomeWorkingCopy(new NullProblemRequestor(), new NullProgressMonitor());
		doCodeCompletion(moduleFile, 0, "wc_change0");
		
		sourceModule.getBuffer().setContents("module wc_change1;");
		assertEquals(readFileContents(moduleFile), originalFileContents);
		
		doCodeCompletion(moduleFile, 0, "wc_change1");
		
		sourceModule.getBuffer().setContents("module wc_change2;");
		doCodeCompletion(moduleFile, 0, "wc_change2");
		
		sourceModule.discardWorkingCopy();
		doCodeCompletion(moduleFile, 0, "wc_change0");
		
		
		sourceModule = testsProject.getSourceModule("source/basic_pack/foo.d");
		moduleFile = (IFile) sourceModule.getResource();
		doCodeCompletion(moduleFile, 0, "basic_pack/");
		
		// Test commitWorkingCopy
		sourceModule.becomeWorkingCopy(new NullProblemRequestor(), new NullProgressMonitor());
		sourceModule.getBuffer().setContents("module wc_commitWC_Test;");
		sourceModule.commitWorkingCopy(true, new NullProgressMonitor());
		doCodeCompletion(moduleFile, 0, "wc_commitWC_Test/");

		sourceModule.getBuffer().setContents("module wc_commitWC_Test2;");
		sourceModule.commitWorkingCopy(true, new NullProgressMonitor());
		sourceModule.discardWorkingCopy();
		doCodeCompletion(moduleFile, 0, "wc_commitWC_Test2/");
		
		// Test setContents of non-working copy - only valid if sourceModule in buildpath it seems
		if(sourceModule.exists()) {
			sourceModule.getBuffer().setContents("module wc_change3;");
			assertTrue(sourceModule.isWorkingCopy() == false);
			doCodeCompletion(moduleFile, 0, "wc_change3/");
		}
	}
	
	// Note: we don't use this method to test code completion, we are test the Working Copies of the server.
	// Code completion is just being used as a convenient way to check the source contents of the server's WCs.
	protected void doCodeCompletion(IFile file, int offset, String... results) throws CoreException {
		ISourceModule sourceModule = DLTKCore.createSourceModuleFrom(file);
		CompletionSearchResult cc = client.runCodeCompletion(sourceModule, offset, 
			MockCompilerInstalls.DEFAULT_DMD_INSTALL_EXE_PATH);
		new DefUnitResultsChecker(cc.getResults()).simpleCheckResults(results);
	}
	
}