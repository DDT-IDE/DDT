/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.texteditor.ITextEditor;

import _org.eclipse.dltk.internal.ui.editor.BasicScriptEditorActionContributor;
import melnorme.lang.ide.ui.editor.EditorUtils.OpenNewEditorMode;
import melnorme.lang.ide.ui.utils.operations.BasicUIOperation;
import melnorme.lang.tooling.ast.SourceRange;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import mmrnmhrm.ui.actions.DeeOpenDefinitionOperation;

public class DeeEditorActionContributor extends	BasicScriptEditorActionContributor {
	
	@Override
	protected DeeOpenDefinitionOperation createOpenDefinitionOperation(ITextEditor editor, SourceRange range,
			OpenNewEditorMode newEditorMode) {
		return new DeeOpenDefinitionOperation(editor, newEditorMode, range.getOffset());
	}
	
	@Override
	protected void registerOtherEditorHandlers() {
	}
	
	@Override
	protected IEditorOperationCreator getOpCreator_Format() {
		return editor -> new BasicUIOperation("Format") {
			@Override
			protected void doOperation() throws CoreException, CommonException, OperationCancellation {
				throw new CommonException("Not implemented");
			}
		};
	}
	
}