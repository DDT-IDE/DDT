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


import melnorme.lang.ide.ui.editor.AbstractLangEditor;
import melnorme.lang.ide.ui.editor.EditorUtils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;

import _org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import _org.eclipse.dltk.internal.ui.editor.ScriptEditorErrorTickUpdater2;

/**
 * A similar class to {@link AbstractLangEditor}, based on the DLTK hieararchy.
 * Eventually we would like to completely remove the DLTK dependency and just use {@link AbstractLangEditor}.
 */
public abstract class AbstractLangEditor_DLTK extends ScriptEditor {
	
	public AbstractLangEditor_DLTK() {
		super();
	}
	
	/* -----------------  ----------------- */
	
	protected ScriptEditorErrorTickUpdater2 fScriptEditorErrorTickUpdater = new ScriptEditorErrorTickUpdater2(this);
	
	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		
		fScriptEditorErrorTickUpdater.updateEditorImage(EditorUtils.getAssociatedFile(input));
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public void dispose() {
		if (fScriptEditorErrorTickUpdater != null) {
			fScriptEditorErrorTickUpdater.dispose();
			fScriptEditorErrorTickUpdater = null;
		}
		super.dispose();
	}
	
}