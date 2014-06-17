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

import mmrnmhrm.lang.ui.EditorUtil;
import mmrnmhrm.ui.DeeUIPlugin;
import mmrnmhrm.ui.actions.OpenDefinitionOperation.EOpenNewEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.resolver.api.FindDefinitionResult;

public class OpenDefinitionHandler extends AbstractHandler  {
	
	public static final String COMMAND_ID = DeeUIPlugin.PLUGIN_ID + ".commands.openDefinition";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ITextEditor editor = (ITextEditor) HandlerUtil.getActiveEditorChecked(event);
		executeOperation(editor, EOpenNewEditor.TRY_REUSING_EXISTING_EDITORS);
		return null;
	}
	
	
	public static FindDefinitionResult executeOperation(ITextEditor srcEditor, EOpenNewEditor openNewEditor) {
		TextSelection sel = EditorUtil.getSelection(srcEditor);
		return new OpenDefinitionOperation(srcEditor, openNewEditor, sel.getOffset()).executeWithResult();
	}
	
}