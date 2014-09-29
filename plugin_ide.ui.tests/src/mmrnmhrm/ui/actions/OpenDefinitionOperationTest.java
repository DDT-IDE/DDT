/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
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
import melnorme.lang.ide.ui.EditorSettings_Actual;
import melnorme.lang.ide.ui.actions.UIUserInteractionsHelper;
import melnorme.lang.ide.ui.editor.EditorUtils;
import melnorme.lang.ide.ui.editor.EditorUtils.OpenNewEditorMode;
import melnorme.lang.ide.ui.utils.WorkbenchUtils;
import mmrnmhrm.tests.IOutsideBuildpathTestResources;
import mmrnmhrm.tests.ITestResourcesConstants;
import mmrnmhrm.tests.SampleMainProject;
import mmrnmhrm.tests.SampleNonDeeProject;
import mmrnmhrm.ui.CommonDeeUITest;
import mmrnmhrm.ui.editor.DeeEditor;

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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dtool.engine.operations.FindDefinitionOperation;
import dtool.engine.operations.FindDefinitionResult;

public class OpenDefinitionOperationTest extends CommonDeeUITest {
	
	protected IFile file; 
	protected IEditorPart editor;
	protected ITextEditor srcEditor;
	
	@BeforeClass
	public static void commonSetUp() throws Exception {
		UIUserInteractionsHelper.unitTestsMode = true;
	}
	
	@Before
	public void setUp() throws Exception {
		IProject project = SampleMainProject.scriptProject.getProject();
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
	public void testOpenRef_TargetNotFound() throws CoreException {
		int offset = getOffsetForString("NotFound notfound");
		doTest(offset, FindDefinitionOperation.FIND_DEF_ReferenceResolveFailed, 
			file.getProject(), IOutsideBuildpathTestResources.TEST_SRCFILE); 
	}
	
	@Test
	public void testOpenRef_OnADefUnit() throws CoreException {
		int offset = getOffsetForString("Foo {");
		doTest(offset, null, file.getProject(), IOutsideBuildpathTestResources.TEST_SRCFILE); 
	}
	
	@Test
	public void testOpenRef_TargetInAnotherFile() throws CoreException {
		int offset = getOffsetForString("SampleClass sampleCl");
		// find target in other file
		doTest(offset, null, file.getProject(), IOutsideBuildpathTestResources.TEST_SRC_TARGETFILE); 
	}
	
	@Test
	public void testOpenRef_OriginFileOutsideBuildpath_TargetInSameFile() throws CoreException {
		IProject project = SampleMainProject.scriptProject.getProject();
		setupWithFile(project, IOutsideBuildpathTestResources.TEST_OUTFILE);
		int offset = getOffsetForString("Foo foo");
		doTest(offset, null, project, IOutsideBuildpathTestResources.TEST_OUTFILE);
	}
	
	@Test
	public void testOpenRef_OriginFileOutsideBuildpath_TargetInAnotherFile() throws CoreException {
		IProject project = SampleMainProject.scriptProject.getProject();
		setupWithFile(project, IOutsideBuildpathTestResources.TEST_OUTFILE);
		int offset = getOffsetForString("SampleClass sampleCl");
		doTest(offset, null, project, IOutsideBuildpathTestResources.TEST_SRC_TARGETFILE);
	}
	
	
	@Test
	public void testOpenRef_OriginFileNotOnDProject_TargetInSameFile() throws CoreException {
		IProject project = SampleNonDeeProject.project;
		SampleNonDeeProject.project.getProject().getFolder(ITestResourcesConstants.TR_SAMPLE_SRC1).exists();
		setupWithFile(project, IOutsideBuildpathTestResources.TEST_NONDEEPROJ_FILE);
		int offset = getOffsetForString("Foo foo");
		doTest(offset, null, project, IOutsideBuildpathTestResources.TEST_NONDEEPROJ_FILE);
	}
	
	@Test
	public void testOpenRef_OriginFileNotOnDProject_NotFound() throws CoreException {
		IProject project = SampleNonDeeProject.project;
		setupWithFile(project, IOutsideBuildpathTestResources.TEST_NONDEEPROJ_FILE);
		int offset = getOffsetForString("SampleClass sampleCl");
		doTest(offset, FindDefinitionOperation.FIND_DEF_ReferenceResolveFailed, 
			project, IOutsideBuildpathTestResources.TEST_NONDEEPROJ_FILE);
	}
	
	protected void doTest(int offset, String errorMessageContains, IProject project, String editorFile) 
			throws CoreException {
		EditorUtils.setEditorSelection(srcEditor, offset, 0);
		FindDefinitionResult opResult = new DeeOpenDefinitionHandler().createOperation(srcEditor, 
			OpenNewEditorMode.TRY_REUSING_EXISTING_EDITORS).executeWithResult();
		assertTrue(errorMessageContains == null || opResult.errorMessage.contains(errorMessageContains));
		
		assertCurrentEditorIsEditing(project.getFullPath(), editorFile);
	}
	
	protected void assertCurrentEditorIsEditing(IPath prjpath, String targetpath) {
		DeeEditor deeEditor;
		deeEditor = (DeeEditor) WorkbenchUtils.getActivePage().getActiveEditor();
		IFile editorFile = ((FileEditorInput) deeEditor.getEditorInput()).getFile();
		IPath path = editorFile.getFullPath();
		assertEquals(path, prjpath.append(targetpath));
	}
	
}