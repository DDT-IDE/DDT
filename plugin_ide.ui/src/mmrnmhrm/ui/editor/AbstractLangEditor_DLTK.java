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
import melnorme.lang.ide.ui.editor.AbstractLangEditorActions;
import melnorme.lang.ide.ui.editor.EditorUtils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

import _org.eclipse.dltk.internal.ui.editor.ScriptEditorErrorTickUpdater2;
import _org.eclipse.dltk.internal.ui.editor.ScriptEditor_Actions;

/**
 * A similar class to {@link AbstractLangEditor}, based on the DLTK hieararchy.
 * Eventually we would like to completely remove the DLTK dependency and just use {@link AbstractLangEditor}.
 */
public abstract class AbstractLangEditor_DLTK extends ScriptEditor_Actions {
	
	public AbstractLangEditor_DLTK() {
		super();
	}
	
	/* ----------------- text presentation ----------------- */
	
	/* FIXME: review this, convert to go Lang? */
	
	protected ICharacterPairMatcher bracketMatcher = new DefaultCharacterPairMatcher("{}[]()".toCharArray());
	
	@Override
	protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {
		support.setCharacterPairMatcher(bracketMatcher);
		support.setMatchingCharacterPainterPreferenceKeys(MATCHING_BRACKETS, MATCHING_BRACKETS_COLOR);
		
		super.configureSourceViewerDecorationSupport(support);
	}
	
	@Override
	protected ICharacterPairMatcher createBracketMatcher() {
		return bracketMatcher;
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
	
	/* ----------------- actions ----------------- */
	
	@Override
	protected abstract AbstractLangEditorActions createActionsManager();
	
}