/*******************************************************************************
 * Copyright (c) 2008 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.actions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dtool.engine.operations.FindDefinitionOperation;
import dtool.engine.operations.FindDefinitionResult;
import melnorme.lang.ide.ui.EditorSettings_Actual;
import melnorme.lang.ide.ui.editor.EditorUtils;
import melnorme.lang.ide.ui.utils.UIOperationErrorHandlerImpl;
import melnorme.lang.ide.ui.utils.UIOperationsStatusHandler;
import melnorme.lang.ide.ui.utils.WorkbenchUtils;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import mmrnmhrm.tests.IOutsideBuildpathTestResources;
import mmrnmhrm.tests.ITestResourcesConstants;
import mmrnmhrm.tests.SampleMainProject;
import mmrnmhrm.tests.SampleNonDeeProject;
import mmrnmhrm.ui.CommonDeeUITest;
import mmrnmhrm.ui.editor.DeeEditor;

public class OpenDefinitionOperationTest extends CommonDeeUITest {
	
	protected IFile file; 
	protected IEditorPart editor;
	protected ITextEditor srcEditor;
	
	protected static UIOperationErrorHandlerImpl originalHandler;
	
	@BeforeClass
	public static void commonSetUp() throws Exception {
		originalHandler = UIOperationsStatusHandler.handler;
		UIOperationsStatusHandler.handler = new UIOperationsStatusHandler.Null_UIOperationErrorHandlerImpl();
	}
	
	@AfterClass
	public static void tearDownUp() throws Exception {
		UIOperationsStatusHandler.handler = originalHandler;
	}
	
	@Before
	public void setUp() throws Exception {
		IProject project = SampleMainProject.project;
		setupWithFile(project, IOutsideBuildpathTestResources.TEST_SRCFILE);
	}
	
	private void setupWithFile(IProject project, String path) throws PartInitException, CoreException {
		file = project.getFile(path);
		assertTrue(file.exists());
		IWorkbenchPage page = WorkbenchUtils.getActivePage();
		editor = IDE.openEditor(page, file, EditorSettings_Actual.EDITOR_ID);
		srcEditor = (ITextEditor) editor;
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testOpenRef_TargetInSameFile() throws Exception { testOpenRef_TargetInSameFile$(); }
	public void testOpenRef_TargetInSameFile$() throws Exception {
		int offset;
		offset = getOffsetForString("Foo foo");
		doTest(offset, null, file.getProject(), IOutsideBuildpathTestResources.TEST_SRCFILE); 
		offset = getOffsetForString("Foo foo") + "Foo".length();
		doTest(offset, null, file.getProject(), IOutsideBuildpathTestResources.TEST_SRCFILE);

		offset = getOffsetForString("testGoToDefOp.");
		doTest(offset, null, file.getProject(), IOutsideBuildpathTestResources.TEST_SRCFILE);
		
		offset = getEndPosForString("testGoToDefOp.");
		doTest(offset, FindDefinitionOperation.FIND_DEF_NoReferenceFoundAtCursor, file.getProject(), 
			IOutsideBuildpathTestResources.TEST_SRCFILE);
	}
	
	protected int getOffsetForString(String string) {
		return srcEditor.getDocumentProvider().getDocument(srcEditor.getEditorInput()).get().indexOf(string);
	}
	
	protected int getEndPosForString(String string) {
		return getOffsetForString(string) + string.length();
	}
	
	@Test
	public void testOpenRef_TargetNotFound() throws Exception {
		int offset = getOffsetForString("NotFound notfound");
		doTest(offset, FindDefinitionOperation.FIND_DEF_ReferenceResolveFailed, 
			file.getProject(), IOutsideBuildpathTestResources.TEST_SRCFILE); 
	}
	
	@Test
	public void testOpenRef_OnADefUnit() throws Exception {
		int offset = getOffsetForString("Foo {");
		doTest(offset, FindDefinitionOperation.FIND_DEF_PickedElementAlreadyADefinition, 
			file.getProject(), IOutsideBuildpathTestResources.TEST_SRCFILE); 
	}
	
	@Test
	public void testOpenRef_TargetInAnotherFile() throws Exception {
		int offset = getOffsetForString("SampleClass sampleCl");
		// find target in other file
		doTest(offset, null, file.getProject(), IOutsideBuildpathTestResources.TEST_SRC_TARGETFILE); 
	}
	
	@Test
	public void testOpenRef_OriginFileOutsideBuildpath_TargetInSameFile() throws Exception {
		IProject project = SampleMainProject.project;
		setupWithFile(project, IOutsideBuildpathTestResources.TEST_OUTFILE);
		int offset = getOffsetForString("Foo foo");
		doTest(offset, null, project, IOutsideBuildpathTestResources.TEST_OUTFILE);
	}
	
	@Test
	public void testOpenRef_OriginFileOutsideBuildpath_TargetInAnotherFile() throws Exception {
		IProject project = SampleMainProject.project;
		setupWithFile(project, IOutsideBuildpathTestResources.TEST_OUTFILE);
		int offset = getOffsetForString("SampleClass sampleCl");
		
		// This flag controls what behavior to expected when trying a semantic operation
		// on a file outside the buildpath.
		boolean fileOutsideBuildpathSeeImportOfContainedBundle = false;
		if(fileOutsideBuildpathSeeImportOfContainedBundle) {
			doTest(offset, null, project, IOutsideBuildpathTestResources.TEST_SRC_TARGETFILE);
		} else {
			doTest(offset, FindDefinitionOperation.FIND_DEF_ReferenceResolveFailed, 
				project, IOutsideBuildpathTestResources.TEST_OUTFILE);
		}
	}
	
	
	@Test
	public void testOpenRef_OriginFileNotOnDProject_TargetInSameFile() throws Exception {
		IProject project = SampleNonDeeProject.project;
		SampleNonDeeProject.project.getProject().getFolder(ITestResourcesConstants.TR_SAMPLE_SRC1).exists();
		setupWithFile(project, IOutsideBuildpathTestResources.TEST_NONDEEPROJ_FILE);
		int offset = getOffsetForString("Foo foo");
		doTest(offset, null, project, IOutsideBuildpathTestResources.TEST_NONDEEPROJ_FILE);
	}
	
	@Test
	public void testOpenRef_OriginFileNotOnDProject_NotFound() throws Exception {
		IProject project = SampleNonDeeProject.project;
		setupWithFile(project, IOutsideBuildpathTestResources.TEST_NONDEEPROJ_FILE);
		int offset = getOffsetForString("SampleClass sampleCl");
		doTest(offset, FindDefinitionOperation.FIND_DEF_ReferenceResolveFailed, 
			project, IOutsideBuildpathTestResources.TEST_NONDEEPROJ_FILE);
	}
	
	protected void doTest(int offset, String errorMessageContains, IProject project, String editorFile) 
			throws CoreException, CommonException, OperationCancellation {
		EditorUtils.setEditorSelection(srcEditor, offset, 0);
		DeeOpenDefinitionOperation op = new DeeOpenDefinitionOperation(srcEditor);
		op.execute();
		FindDefinitionResult opResult = op.getResultValue();
		
		if(errorMessageContains != null) {
			assertTrue(opResult.errorMessage.contains(errorMessageContains));
		} else {
			assertTrue(opResult.errorMessage == null);
		}
		
		assertCurrentEditorIsEditing(project.getFullPath(), editorFile);
	}
	
	protected void assertCurrentEditorIsEditing(IPath prjpath, String targetpath) {
		DeeEditor activeEditor = (DeeEditor) WorkbenchUtils.getActivePage().getActiveEditor();
		IFile activeEditorFile = ((FileEditorInput) activeEditor.getEditorInput()).getFile();
		assertEquals(activeEditorFile.getFullPath(), prjpath.append(targetpath));
	}
	
}