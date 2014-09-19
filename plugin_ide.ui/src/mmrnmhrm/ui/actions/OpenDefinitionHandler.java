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
package mmrnmhrm.ui.actions;

import melnorme.lang.ide.ui.editor.EditorUtils;
import melnorme.lang.ide.ui.editor.EditorUtils.OpenNewEditorMode;
import mmrnmhrm.ui.DeeUIPlugin;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.engine.operations.FindDefinitionResult;

public class OpenDefinitionHandler extends AbstractHandler  {
	
	public static final String COMMAND_ID = DeeUIPlugin.PLUGIN_ID + ".commands.openDefinition";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ITextEditor editor = (ITextEditor) HandlerUtil.getActiveEditorChecked(event);
		executeOperation(editor, OpenNewEditorMode.TRY_REUSING_EXISTING_EDITORS);
		return null;
	}
	
	
	public static FindDefinitionResult executeOperation(ITextEditor srcEditor, OpenNewEditorMode openNewEditor) {
		TextSelection sel = EditorUtils.getSelection(srcEditor);
		return new OpenDefinitionOperation(srcEditor, openNewEditor, sel.getOffset()).executeWithResult();
	}
	
}