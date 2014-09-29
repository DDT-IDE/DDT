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
package mmrnmhrm.ui;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.ide.ui.EditorSettings_Actual;
import melnorme.lang.ide.ui.tests.CommonUITest;
import melnorme.lang.ide.ui.utils.WorkbenchUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;


public class CommonDeeUITest extends CommonUITest {
	
	public static ScriptEditor openDeeEditorForFile(IFile file) {
		IWorkbenchPage page = WorkbenchUtils.getActivePage();
		try {
			ScriptEditor editor = (ScriptEditor) IDE.openEditor(page, file, EditorSettings_Actual.EDITOR_ID);
			assertTrue(editor.getScriptSourceViewer() != null);
			return editor;
		} catch(PartInitException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
}