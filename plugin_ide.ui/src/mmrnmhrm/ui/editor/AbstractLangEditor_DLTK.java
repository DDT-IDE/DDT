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


import melnorme.lang.ide.ui.LangUIPlugin_Actual;
import melnorme.lang.ide.ui.editor.AbstractLangEditor;
import melnorme.lang.ide.ui.editor.AbstractLangEditorActions;

import org.dsource.ddt.lang.ui.editor.ScriptEditorExtension;
import org.eclipse.jface.action.IMenuManager;

/**
 * A similar class to {@link AbstractLangEditor}, based on the DLTK hieararchy.
 * Eventually we would like to completely remove the DLTK dependency and just use {@link AbstractLangEditor}.
 */
public abstract class AbstractLangEditor_DLTK extends ScriptEditorExtension {
	
	public AbstractLangEditor_DLTK() {
		super();
	}
	
	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		initialize_setContextMenuIds();
	}
	
	/* ----------------- actions ----------------- */
	
	protected void initialize_setContextMenuIds() {
		setEditorContextMenuId(LangUIPlugin_Actual.EDITOR_CONTEXT);
		setRulerContextMenuId(LangUIPlugin_Actual.RULER_CONTEXT);
	}
	
	protected AbstractLangEditorActions editorActionsManager;
	
	@Override
	protected void createActions() {
		super.createActions();
		
		editorActionsManager = createActionsManager();
	}
	
	protected abstract AbstractLangEditorActions createActionsManager();
	
	@Override
	public void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);
		editorContextMenuAboutToShow_extend(menu);
	}
	
	protected void editorContextMenuAboutToShow_extend(IMenuManager menu) {
		editorActionsManager.editorContextMenuAboutToShow(menu);
	}
	
}