/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     xored software, Inc. - initial API and implementation
 *     xored software, Inc. - fix tab handling (Bug# 200024) (Alex Panchenko) 
 *******************************************************************************/
package _org.eclipse.dltk.internal.ui.editor;


import org.eclipse.dltk.internal.ui.editor.DLTKEditorMessages;
import org.eclipse.dltk.internal.ui.editor.ToggleCommentAction;
import org.eclipse.dltk.ui.actions.DLTKActionConstants;
import org.eclipse.dltk.ui.actions.IScriptEditorActionDefinitionIds;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.TextOperationAction;

import _org.eclipse.dltk.internal.ui.actions.FoldingActionGroup;
import _org.eclipse.dltk.ui.actions.GoToNextPreviousMemberAction;
import _org.eclipse.dltk.ui.actions.GotoMatchingBracketAction;

public abstract class ScriptEditor_Actions extends ScriptEditor {
	
	private FoldingActionGroup fFoldingGroup;
	
	public ScriptEditor_Actions() {
		super();
	}
	
	FoldingActionGroup getFoldingActionGroup() {
		return fFoldingGroup;
	}
	
	@Override
	protected void createActions() {
		super.createActions();

		fFoldingGroup = new FoldingActionGroup(this, getSourceViewer_(), getScriptPreferenceStore());

		Action action = new GotoMatchingBracketAction(this);
		action.setActionDefinitionId(IScriptEditorActionDefinitionIds.GOTO_MATCHING_BRACKET);
		setAction(GotoMatchingBracketAction.GOTO_MATCHING_BRACKET, action);

		Action outlineAction = new TextOperationAction(
				DLTKEditorMessages.getBundleForConstructedKeys(),
				"ShowOutline.", this, //$NON-NLS-1$
				ScriptSourceViewer.SHOW_OUTLINE, true);
		outlineAction.setActionDefinitionId(IScriptEditorActionDefinitionIds.SHOW_OUTLINE);
		setAction(IScriptEditorActionDefinitionIds.SHOW_OUTLINE, outlineAction);

		// GoToNextMember
		action = GoToNextPreviousMemberAction.newGoToNextMemberAction(this);
		action.setActionDefinitionId(IScriptEditorActionDefinitionIds.GOTO_NEXT_MEMBER);
		setAction(GoToNextPreviousMemberAction.NEXT_MEMBER, action);

		// GoToPreviousMember
		action = GoToNextPreviousMemberAction.newGoToPreviousMemberAction(this);
		action.setActionDefinitionId(IScriptEditorActionDefinitionIds.GOTO_PREVIOUS_MEMBER);
		setAction(GoToNextPreviousMemberAction.PREVIOUS_MEMBER, action);

		// Source menu actions
		action = new TextOperationAction(
				DLTKEditorMessages.getBundleForConstructedKeys(),
				"Comment.", this, ITextOperationTarget.PREFIX); //$NON-NLS-1$
		action.setActionDefinitionId(IScriptEditorActionDefinitionIds.COMMENT);
		setAction(DLTKActionConstants.COMMENT, action);
		markAsStateDependentAction(DLTKActionConstants.COMMENT, true);

		action = new TextOperationAction(
				DLTKEditorMessages.getBundleForConstructedKeys(),
				"Uncomment.", this, ITextOperationTarget.STRIP_PREFIX); //$NON-NLS-1$
		action.setActionDefinitionId(IScriptEditorActionDefinitionIds.UNCOMMENT);
		setAction(DLTKActionConstants.UNCOMMENT, action);
		markAsStateDependentAction(DLTKActionConstants.UNCOMMENT, true);

		action = new ToggleCommentAction(
				DLTKEditorMessages.getBundleForConstructedKeys(),
				"ToggleComment.", this); //$NON-NLS-1$
		action.setActionDefinitionId(IScriptEditorActionDefinitionIds.TOGGLE_COMMENT);
		setAction(DLTKActionConstants.TOGGLE_COMMENT, action);
		markAsStateDependentAction(DLTKActionConstants.TOGGLE_COMMENT, true);

		ISourceViewer sourceViewer = getSourceViewer();
		SourceViewerConfiguration configuration = getSourceViewerConfiguration();
		((ToggleCommentAction) action).configure(sourceViewer, configuration);

	}

	@Override
	public void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);
		
		menu.insertAfter(ICommonMenuConstants.GROUP_OPEN, new GroupMarker(ICommonMenuConstants.GROUP_SHOW));
		
		// Quick views
		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN,
				getAction(IScriptEditorActionDefinitionIds.SHOW_OUTLINE));
	}
	
	@Override
	protected void rulerContextMenuAboutToShow(IMenuManager menu) {
		super.rulerContextMenuAboutToShow(menu);
		IMenuManager foldingMenu = new MenuManager(
				DLTKEditorMessages.Editor_FoldingMenu_name, "projection"); //$NON-NLS-1$
		menu.appendToGroup(ITextEditorActionConstants.GROUP_RULERS, foldingMenu);

		IAction action = getAction("FoldingToggle"); //$NON-NLS-1$
		if (action != null) {
			foldingMenu.add(action);
		}
		action = getAction("FoldingExpandAll"); //$NON-NLS-1$
		if (action != null) {
			foldingMenu.add(action);
		}
		action = getAction("FoldingCollapseAll"); //$NON-NLS-1$
		if (action != null) {
			foldingMenu.add(action);
		}
		action = getAction("FoldingRestore"); //$NON-NLS-1$
		if (action != null) {
			foldingMenu.add(action);
		}
		action = getAction("FoldingCollapseMembers"); //$NON-NLS-1$
		if (action != null) {
			foldingMenu.add(action);
		}
		action = getAction("FoldingCollapseComments"); //$NON-NLS-1$
		if (action != null) {
			foldingMenu.add(action);
		}
	}
	
}