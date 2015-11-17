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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.engine.EngineClient;
import melnorme.lang.ide.core.engine.IStructureModelListener;
import melnorme.lang.ide.core.engine.StructureModelManager.SourceModelRegistration;
import melnorme.lang.ide.core.engine.StructureModelManager.StructureInfo;
import melnorme.lang.ide.core.tests.CommonCoreTest;
import melnorme.lang.ide.core.tests.LangCoreTestResources;
import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.lang.tooling.structure.SourceFileStructure;
import melnorme.utilbox.misc.Location;
import mmrnmhrm.tests.TestFixtureProject;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;
import org.junit.Test;

import dtool.engine.ModuleParseCache_Test;


public class DToolClient_Test extends CommonCoreTest {
	
	protected static final DeeEngineClient client = DeeEngineClient.getDefault();
	
	protected TestFixtureProject testsProject;
	
	@Test
	public void testUpdates() throws Exception { testUpdates________________(); }
	public void testUpdates________________() throws Exception {
		testsProject = new DToolFixtureProject();
		testUpdatesToWorkingCopy();
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
	
	public static void updateFileContents(IFile file, String contents) throws IOException, CoreException {
		ModuleParseCache_Test.writeToFileAndUpdateMTime(path(file.getLocation()), contents);
		file.refreshLocal(0, null);
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
			EngineClient engineClient = LangCore.getEngineClient();
			
			IStructureModelListener structureListener = new IStructureModelListener() {
				@Override
				public void structureChanged(StructureInfo lockedStructureInfo) {
				}
			};
			
			// Try connect using LocationKind.IFILE
			try {
				assertTrue(fbm.getTextFileBuffer(fullPath, LocationKind.NORMALIZE) == null);
				
				fbm.connect(fullPath, locationKind, null);
				ITextFileBuffer fileBuffer = fbm.getTextFileBuffer(fullPath, locationKind);
				if(locationKind != LocationKind.LOCATION) {
					assertTrue(fileBuffer == fbm.getTextFileBuffer(fullPath, LocationKind.NORMALIZE));
				}
				
				IDocument doc = fileBuffer.getDocument();
				
				Location fileLoc = ResourceUtils.getResourceLocation(moduleFile);
				
				SourceModelRegistration updateRegistration = 
						engineClient.connectStructureUpdates3(fileLoc, doc, structureListener);
				
				try{
					doRun(moduleFile, fileBuffer);
				} finally {
					updateRegistration.dispose();
				}
				
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
	
	protected SourceFileStructure getCurrentStructure(Location fileLoc) {
		try {
			return client.getStoredStructureInfo(fileLoc).awaitUpdatedData();
		} catch(InterruptedException e) {
			throw assertFail();
		}
	}
	
}