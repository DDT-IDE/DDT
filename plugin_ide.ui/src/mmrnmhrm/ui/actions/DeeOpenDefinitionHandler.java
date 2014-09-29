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

import melnorme.lang.ide.ui.actions.AbstractOpenDefinitionHandler;
import melnorme.lang.ide.ui.editor.EditorUtils.OpenNewEditorMode;
import melnorme.lang.tooling.ast.SourceRange;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

public class DeeOpenDefinitionHandler extends AbstractOpenDefinitionHandler  {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ITextEditor editor = (ITextEditor) HandlerUtil.getActiveEditorChecked(event);
		runOperation(editor);
		return null;
	}
	
	@Override
	public DeeOpenDefinitionOperation createOperation(ITextEditor editor, OpenNewEditorMode newEditorMode) {
		return (DeeOpenDefinitionOperation) super.createOperation(editor, newEditorMode);
	}
	
	@Override
	public DeeOpenDefinitionOperation createOperation(ITextEditor editor, SourceRange range,
			OpenNewEditorMode newEditorMode) {
		return new DeeOpenDefinitionOperation(editor, newEditorMode, range.getOffset());
	}
	
}