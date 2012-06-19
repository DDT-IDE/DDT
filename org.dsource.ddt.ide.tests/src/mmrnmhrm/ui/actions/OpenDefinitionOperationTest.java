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
import junit.framework.Assert;
import mmrnmhrm.core.codeassist.OutsideBuildpathTestResources;
import mmrnmhrm.lang.ui.EditorUtil;
import mmrnmhrm.tests.ITestResourcesConstants;
import mmrnmhrm.tests.SampleMainProject;
import mmrnmhrm.tests.SampleNonDeeProject;
import mmrnmhrm.tests.ui.BaseDeeUITest;
import mmrnmhrm.ui.actions.GoToDefinitionHandler.EOpenNewEditor;
import mmrnmhrm.ui.editor.DeeEditor;

import org.dsource.ddt.lang.ui.WorkbenchUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
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

public class OpenDefinitionOperationTest extends BaseDeeUITest {
	
	protected IFile file; 
	protected IEditorPart editor;
	protected ITextEditor srcEditor;
	
	@BeforeClass
	public static void commonSetUp() throws Exception {
		OperationsManager.get().unitTestMode = true;
	}
	
	@Before
	public void setUp() throws Exception {
		IProject project = SampleMainProject.scriptProject.getProject();
		setupWithFile(project, OutsideBuildpathTestResources.TEST_SRCFILE);
	}
	
	private void setupWithFile(IProject project, String path) throws PartInitException, CoreException {
		file = project.getFile(path);
		assertTrue(file.exists());
		IWorkbenchPage page = WorkbenchUtils.getActivePage();
		editor = IDE.openEditor(page, file, DeeEditor.EDITOR_ID);
		srcEditor = (ITextEditor) editor;
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testOpenRef_TargetInSameFile() {
		int offset = getOffsetForString("Foo foo");
		doTest(offset, IStatus.OK, file.getProject(), OutsideBuildpathTestResources.TEST_SRCFILE); 
	}
	
	protected int getOffsetForString(String string) {
		return srcEditor.getDocumentProvider().getDocument(srcEditor.getEditorInput()).get().indexOf(string);
	}
	
	@Test
	public void testOpenRef_TargetNotFound() {
		int offset = getOffsetForString("NotFound notfound");
		doTest(offset, IStatus.WARNING, file.getProject(), OutsideBuildpathTestResources.TEST_SRCFILE); 
	}
	
	@Test
	public void testOpenRef_OnADefUnit() {
		int offset = getOffsetForString("Foo {");
		doTest(offset, IStatus.INFO, file.getProject(), OutsideBuildpathTestResources.TEST_SRCFILE); 
	}
	
	@Test
	public void testOpenRef_TargetInAnotherFile() {
		int offset = getOffsetForString("SampleClass sampleCl");
		// find target in other file
		doTest(offset, IStatus.OK, file.getProject(), OutsideBuildpathTestResources.TEST_SRC_TARGETFILE); 
	}
	
	@Test
	public void testOpenRef_OriginFileOutsideBuildpath_TargetInSameFile() throws CoreException {
		IProject project = SampleMainProject.scriptProject.getProject();
		setupWithFile(project, OutsideBuildpathTestResources.TEST_OUTFILE);
		int offset = getOffsetForString("Foo foo");
		doTest(offset, IStatus.OK, project, OutsideBuildpathTestResources.TEST_OUTFILE);
	}
	
	@Test
	public void testOpenRef_OriginFileOutsideBuildpath_TargetInAnotherFile() throws CoreException {
		IProject project = SampleMainProject.scriptProject.getProject();
		setupWithFile(project, OutsideBuildpathTestResources.TEST_OUTFILE);
		int offset = getOffsetForString("SampleClass sampleCl");
		doTest(offset, IStatus.OK, project, OutsideBuildpathTestResources.TEST_SRC_TARGETFILE);
	}
	
	
	@Test
	public void testOpenRef_OriginFileNotOnDProject_TargetInSameFile() throws CoreException {
		IProject project = SampleNonDeeProject.project;
		SampleNonDeeProject.project.getProject().getFolder(ITestResourcesConstants.TR_SAMPLE_SRC1).exists();
		setupWithFile(project, OutsideBuildpathTestResources.TEST_NONDEEPROJ_FILE);
		int offset = getOffsetForString("Foo foo");
		doTest(offset, IStatus.OK, project, OutsideBuildpathTestResources.TEST_NONDEEPROJ_FILE);
	}
	
	@Test
	public void testOpenRef_OriginFileNotOnDProject_NotFound() throws CoreException {
		IProject project = SampleNonDeeProject.project;
		setupWithFile(project, OutsideBuildpathTestResources.TEST_NONDEEPROJ_FILE);
		int offset = getOffsetForString("SampleClass sampleCl");
		doTest(offset, IStatus.WARNING, project, OutsideBuildpathTestResources.TEST_NONDEEPROJ_FILE);
	}
	
	protected void doTest(int offset, int result, IProject project, String editorFile) {
		EditorUtil.setEditorSelection(srcEditor, offset, 0);
		GoToDefinitionHandler.executeChecked(srcEditor, EOpenNewEditor.TRY_REUSING_EXISTING_EDITORS);
		assertTrue(OperationsManager.get().opResult == result, "Got result: " + result);
		assertCurrentEditorIsEditing(project.getFullPath(), editorFile);
	}
	
	protected void assertCurrentEditorIsEditing(IPath prjpath, String targetpath) {
		DeeEditor deeEditor;
		deeEditor = (DeeEditor) WorkbenchUtils.getActivePage().getActiveEditor();
		IFile editorFile = ((FileEditorInput) deeEditor.getEditorInput()).getFile();
		IPath path = editorFile.getFullPath();
		Assert.assertEquals(path, prjpath.append(targetpath));
	}
	
}