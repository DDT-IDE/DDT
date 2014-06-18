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

import melnorme.lang.ide.core.tests.CommonCoreTest;
import mmrnmhrm.tests.DeeCoreTestResources;
import mmrnmhrm.tests.TestFixtureProject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.compiler.env.ModuleSource;
import org.eclipse.dltk.core.ISourceModule;
import org.junit.Test;

import dtool.resolver.DefUnitResultsChecker;
import dtool.resolver.PrefixDefUnitSearch;
import dtool.tests.DToolTestResources;

public class DToolClient_Test extends CommonCoreTest {
	
	protected DToolClient client;
	protected TestFixtureProject testsProject;
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		client = new DToolClient();
		
		ModuleSource moduleSource = new ModuleSource("relative/path/foo.d", "module blah;");
		
		assertEquals(client.getParsedModuleOrNull(moduleSource).module.getName(), "blah");
		
		assertEquals(client.getExistingModuleNodeOrNull(moduleSource).getName(), "blah");
		assertEquals(client.getExistingParsedModuleOrNull(moduleSource).module.getName(), "blah");
		
		
		testCodeCompletion(moduleSource, 0, 
			"blah");
		testCodeCompletion(new ModuleSource("relative/path/foo.d", "module xpto;"), 0, 
			"xpto");
		
		Path path = DToolTestResources.getTestResourcePath("dummy__non_existant.d");
		assertTrue(path.isAbsolute());
		testCodeCompletion(new ModuleSource(path.toString(), "module blah;"), 0, 
				"blah");
		
		// Error case
		try {
			client.doCodeCompletion_Do(null, 0, null);
			assertFail();
		} catch (CoreException e) {
		}
		
	}
	
	protected void testCodeCompletion(ModuleSource moduleSource, int offset, String... results) throws CoreException {
		PrefixDefUnitSearch cc = client.doCodeCompletion(moduleSource, offset);
		new DefUnitResultsChecker(cc.getResults()).simpleCheckResults(results);
	}
	
	@Test
	public void testUpdates() throws Exception { testUpdates$(); }
	public void testUpdates$() throws Exception {
		if(DToolClient.USE_LEGACY_RESOLVER) 
			return; // test not valid
		
		client = DToolClient.getDefault();
		
		testsProject = new TestFixtureProject("DToolClientTest") {
			@Override
			protected void createContents() throws CoreException {
				DeeCoreTestResources.createSrcFolderFromCoreResource("simple-source", project.getFolder("source"));
				writeManifestFile();
			}
		};
		
		ISourceModule sourceModule = testsProject.getSourceModule("basic_foo.d");
		IFile file = (IFile) sourceModule.getResource();
		
		IFolder SRC_FOLDER = testsProject.project.getFolder("simple-source");
		
		doCodeCompletion(sourceModule, 0, "basic_foo", "barLibFunction");
		
		writeStringToFile(file, "module change1;");
		doCodeCompletion(sourceModule, 0, "change1");
		
		writeStringToFile(file, "module change2;");
		doCodeCompletion(sourceModule, 0, "change2");
		
		IFile newFile = SRC_FOLDER.getFile("new_file.d");
		writeStringToFile(newFile, "module new_file;");
		checkModuleContains(sourceModule, "new", "new_file", true);
		
		IFolder newPackage = createFolder(SRC_FOLDER.getFolder("new_package"));
		writeStringToFile(newPackage.getFile("new_package_file.d"), "module new_package_file;");
		checkModuleContains(sourceModule, "new", "new_package.new_package_file", true);
		
		deleteResource(newPackage);
		checkModuleContains(sourceModule, "new", "new_package.new_package_file", false);
		
		deleteResource(newFile);
		checkModuleContains(sourceModule, "new", "new_file", false);
		
		testUpdatesToWorkingCopy();
	}
	
	protected void checkModuleContains(ISourceModule sourceModule, String prefix, String moduleName, boolean contains) 
			throws CoreException {
		HashSet<String> modules = client.listModulesFor(sourceModule, prefix);
		assertTrue(modules.contains(moduleName) == contains);
		if(contains) {
			doCodeCompletion(sourceModule, 0, moduleName);
		}
	}
	
	protected void testUpdatesToWorkingCopy() throws CoreException, IOException {
		ISourceModule sourceModule = testsProject.getSourceModule("basic_foo.d");
		sourceModule.discardWorkingCopy();
		
		IFile file = (IFile) sourceModule.getResource();
		String originalFileContents = "module wc_change0;";
		writeStringToFile(file, originalFileContents);
		
		sourceModule.becomeWorkingCopy(new NullProblemRequestor(), new NullProgressMonitor());
		doCodeCompletion(sourceModule, 0, "wc_change0");
		
		sourceModule.getBuffer().setContents("module wc_change1;");
		assertEquals(readFileContents(file), originalFileContents);
		
		doCodeCompletion(sourceModule, 0, "wc_change1");
		
		sourceModule.getBuffer().setContents("module wc_change2;");
		doCodeCompletion(sourceModule, 0, "wc_change2");
		
		sourceModule.discardWorkingCopy();
		doCodeCompletion(sourceModule, 0, "basic_foo", "barLibFunction");
		
		
		sourceModule = testsProject.getSourceModule("basic_pack/foo.d");
		doCodeCompletion(sourceModule, 0, "foo");
		
		sourceModule.getBuffer().setContents("module wc_change3;");
		doCodeCompletion(sourceModule, 0, "wc_change3");
		sourceModule.commitWorkingCopy(true, new NullProgressMonitor());
		
		doCodeCompletion(sourceModule, 0, "wc_change3");
	}
	
	// Note: we don't use this method to test code completion, we are test the Working Copies of the server.
	// Code completion is just being used as a convenient way to check the source contents of the server's WCs.
	protected void doCodeCompletion(ISourceModule sourceModule, int offset, String... results) throws CoreException {
		PrefixDefUnitSearch cc = client.doCodeCompletion(sourceModule, offset);
		new DefUnitResultsChecker(cc.getResults()).simpleCheckResults(results);
	}
	
}