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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import melnorme.lang.ide.core.tests.CommonCoreTest;
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
import org.junit.Test;

import dtool.dub.BundlePath;
import dtool.engine.BundleResolution;
import dtool.resolver.DefUnitResultsChecker;
import dtool.resolver.PrefixDefUnitSearch;
import dtool.tests.DToolTestResources;
import dtool.tests.MockCompilerInstalls;

public class DToolClient_Test extends CommonCoreTest {
	
	protected static final DToolClient client = DToolClient.getDefault();
	
	protected TestFixtureProject testsProject;
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		ModuleSource moduleSource = new ModuleSource("relative/path/foo.d", "module blah;");
		Path filePath = DToolClient.getPathHandleForModuleSource(moduleSource);
		assertEquals(client.getParsedModuleOrNull_withSource(filePath, moduleSource).module.getName(), "blah");
		assertEquals(client.getExistingParsedModuleNodeOrNull(filePath).getName(), "blah");
		
		testCodeCompletion(moduleSource, 0, 
			"blah");
		testCodeCompletion(new ModuleSource("relative/path/foo.d", "module xpto;"), 0, 
			"xpto");
		
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
		PrefixDefUnitSearch cc = client.runCodeCompletion(moduleSource, offset, 
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
			DeeCoreTestResources.createFolderFromCoreResource("simple-source", sourceFolder);
			writeManifestFile();
		}
	}
	
	protected void doTestUpdates() throws CoreException, IOException {
		IFolder SRC_FOLDER = testsProject.getFolder("source");
		IFile basic_foo = exists(SRC_FOLDER.getFile("basic_foo.d"));
		
		doCodeCompletion(basic_foo, 0, "basic_foo", "barLibFunction");
		
		writeStringToFile(basic_foo, "module change1;");
		doCodeCompletion(basic_foo, 0, "change1");
		
		writeStringToFile(basic_foo, "module change2;");
		doCodeCompletion(basic_foo, 0, "change2");
		
		
		IFile newFile = SRC_FOLDER.getFile("new_file.d");
		writeStringToFile(newFile, "module new_file;"); 
		checkModuleContains(newFile, "new_file");
		
		IFolder newPackage = createFolder(SRC_FOLDER.getFolder("new_package"));
		IFile newFile2 = newPackage.getFile("new_file2.d");
		writeStringToFile(newFile2, "module new_file2;");
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
			sr = client.getServerSemanticManager().getUpdatedResolution(bundlePath);
		} catch (ExecutionException e) {
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
		writeStringToFile(moduleFile, originalFileContents);
		
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
		PrefixDefUnitSearch cc = client.runCodeCompletion(sourceModule, offset, 
			MockCompilerInstalls.DEFAULT_DMD_INSTALL_EXE_PATH);
		new DefUnitResultsChecker(cc.getResults()).simpleCheckResults(results);
	}
	
}