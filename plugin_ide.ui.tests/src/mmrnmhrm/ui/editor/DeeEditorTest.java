/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.editor;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.ide.ui.EditorSettings_Actual;
import melnorme.lang.ide.ui.editor.EditorUtils;
import melnorme.lang.ide.ui.utils.WorkbenchUtils;
import mmrnmhrm.tests.SampleMainProject;
import mmrnmhrm.ui.CommonDeeUITest;
import mmrnmhrm.ui.views.ASTViewer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DeeEditorTest extends CommonDeeUITest {
	
	public static IDocument getDocument(DeeEditor editor) {
		return EditorUtils.getEditorDocument(editor);
	}
	
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	
	@Test
	public void testDeeEditor() throws CoreException {
		IFile file = SampleMainProject.sampleBigFile;
		
		IWorkbenchPage page = WorkbenchUtils.getActivePage();
		IEditorPart editor = IDE.openEditor(page, file, EditorSettings_Actual.EDITOR_ID);
		assertTrue(editor instanceof DeeEditor);
		
		page.showView("org.eclipse.ui.views.ContentOutline");
		page.showView(ASTViewer.VIEW_ID);
	}
	
	@Test
	public void testDeeEditor2() throws CoreException {
		IFile file = SampleMainProject.sampleOutOfModelFile;
		
		IWorkbenchPage page = WorkbenchUtils.getActivePage();
		IEditorPart editor = IDE.openEditor(page, file, EditorSettings_Actual.EDITOR_ID);
		assertTrue(editor instanceof DeeEditor);
		
		page.showView("org.eclipse.ui.views.ContentOutline");
		page.showView(ASTViewer.VIEW_ID);
	}
	
	@Test
	public void testDeeEditor3() throws CoreException {
		IWorkbenchPage page = WorkbenchUtils.getActivePage();
		IFile file = SampleMainProject.sampleNonExistantFile;
		IDE.openEditor(page, file, EditorSettings_Actual.EDITOR_ID);
		logErrorListener.reset();
	}
	
}
