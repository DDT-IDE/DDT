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
package mmrnmhrm.core.engine;

import static dtool.engine.CommonSemanticManagerTest.resolutionKey;
import static dtool.tests.MockCompilerInstalls.DEFAULT_DMD_INSTALL_EXE_PATH;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.util.HashSet;

import melnorme.lang.ide.core.engine.StructureModelManager.StructureInfo;
import melnorme.lang.ide.core.tests.CommonCoreTest;
import melnorme.lang.ide.core.tests.LangCoreTestResources;
import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.lang.tooling.structure.SourceFileStructure;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;
import mmrnmhrm.core.DeeCorePreferences;
import mmrnmhrm.tests.DeeCoreTestResources;
import mmrnmhrm.tests.TestFixtureProject;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;
import org.junit.Test;

import dtool.dub.BundlePath;
import dtool.engine.BundleResolution;
import dtool.engine.ModuleParseCache_Test;
import dtool.engine.SemanticManager;
import dtool.engine.SemanticManager.ManifestUpdateOptions;


public class DToolClient_Test extends CommonCoreTest {
	
	protected static final DToolClient client = DToolClient.getDefault();
	
	protected TestFixtureProject testsProject;
	
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
		
		IFile newFile = SRC_FOLDER.getFile("new_file.d");
		updateFileContents(newFile, "module new_file;"); 
		checkModuleContains(newFile, "new_file");
		
		IFolder newPackage = createFolder(SRC_FOLDER.getFolder("new_package"));
		IFile newFile2 = newPackage.getFile("new_file2.d");
		updateFileContents(newFile2, "module new_file2;");
		checkModuleContains(newFile2, "new_package.new_file2");
		
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
		checkModuleExists(file, moduleName, true);
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
		
		RunWithTextFileBuffer test = new RunWithTextFileBuffer() {
			
			@Override
			protected void doRun(IFile moduleFile, ITextFileBuffer fileBuffer) 
					throws CoreException, IOException {
				
				IDocument document = fileBuffer.getDocument();
				
				String originalFileContents = "module wc_change0;";
				updateFileContents(moduleFile, originalFileContents);
				fileBuffer.revert(null);
				
				doCodeCompletion(moduleFile, document.get());
				
				document.set("module wc_change1;");
				assertEquals(readFileContents(moduleFile), originalFileContents);
				
				doCodeCompletion(moduleFile, document.get());
				
				document.set("module wc_change2;");
				doCodeCompletion(moduleFile, document.get());
				
				fileBuffer.revert(null);
				doCodeCompletion(moduleFile, document.get());
				
				// Test commit 
				
				document.set("module wc_commitWC_Test;");
				fileBuffer.commit(null, true);
				doCodeCompletion(moduleFile, document.get());
				
				document.set("module wc_commitWC_Test2;");
				fileBuffer.commit(null, true);
				fileBuffer.revert(null);
				doCodeCompletion(moduleFile, document.get());
			};
		};
		IFile moduleFile = testsProject.getFile("source/basic_foo.d");
		test.run(moduleFile, moduleFile.getFullPath(), LocationKind.IFILE);
		test.run(moduleFile, moduleFile.getLocation(), LocationKind.LOCATION);
		
	}
	
	public static abstract class RunWithTextFileBuffer {
		
		public RunWithTextFileBuffer() {
		}
		
		public final void run(IFile moduleFile, IPath fullPath, LocationKind locationKind) 
				throws CoreException, IOException {
			
			ITextFileBufferManager fbm = FileBuffers.getTextFileBufferManager();
			
			// Try connect using LocationKind.IFILE
			try {
				assertTrue(fbm.getTextFileBuffer(fullPath, LocationKind.NORMALIZE) == null);
				
				fbm.connect(fullPath, locationKind, null);
				ITextFileBuffer fileBuffer = fbm.getTextFileBuffer(fullPath, locationKind);
				if(locationKind != LocationKind.LOCATION) {
					assertTrue(fileBuffer == fbm.getTextFileBuffer(fullPath, LocationKind.NORMALIZE));
				}
				
				doRun(moduleFile, fileBuffer);
			} finally {
				fbm.disconnect(fullPath, locationKind, null);
			}
			
		}
		
		protected abstract void doRun(IFile moduleFile, ITextFileBuffer fileBuffer) 
				throws CoreException, IOException;
	}
	
	// Note: we don't use this method to test code completion, we are testing the Working Copies of the server.
	// Code completion is just being used as a convenient way to check the source contents of the server's WCs.
	protected void doCodeCompletion(IFile file, String fileContents) 
			throws CoreException, IOException {
		Location fileLoc = ResourceUtils.getResourceLocation(file);
		
		SourceFileStructure currentStructure = getCurrentStructure(fileLoc);
		assertTrue(currentStructure != null);
		assertAreEqual(currentStructure.parsedModule.source, fileContents);
		
	}
	
	protected StructureInfo getStructureInfo(Location fileLoc) {
		return client.getStructureManager().getStructureInfo(fileLoc);
	}
	
	protected SourceFileStructure getCurrentStructure(Location fileLoc) {
		try {
			return getStructureInfo(fileLoc).getCurrentStructure();
		} catch(InterruptedException e) {
			throw assertFail();
		}
	}
	
}