/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.ide.ui.EditorSettings_Actual;
import melnorme.lang.ide.ui.editor.structure.AbstractLangStructureEditor;
import melnorme.lang.ide.ui.tests.CommonUITest;
import melnorme.lang.ide.ui.utils.WorkbenchUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;


public class CommonDeeUITest extends CommonUITest {
	
	public static AbstractLangStructureEditor openDeeEditorForFile(IFile file) {
		IWorkbenchPage page = WorkbenchUtils.getActivePage();
		try {
			AbstractLangStructureEditor editor = 
					(AbstractLangStructureEditor) IDE.openEditor(page, file, EditorSettings_Actual.EDITOR_ID);
			assertTrue(editor.getSourceViewer_() != null);
			return editor;
		} catch(PartInitException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
}