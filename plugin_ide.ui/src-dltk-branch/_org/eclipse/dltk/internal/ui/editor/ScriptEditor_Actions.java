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


import java.text.CharacterIterator;

import org.eclipse.dltk.internal.ui.editor.DLTKEditorMessages;
import org.eclipse.dltk.internal.ui.editor.ToggleCommentAction;
import org.eclipse.dltk.internal.ui.text.DLTKWordIterator;
import org.eclipse.dltk.internal.ui.text.DocumentCharacterIterator;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.actions.DLTKActionConstants;
import org.eclipse.dltk.ui.actions.IScriptEditorActionDefinitionIds;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.TextNavigationAction;
import org.eclipse.ui.texteditor.TextOperationAction;

import _org.eclipse.dltk.internal.ui.actions.CompositeActionGroup;
import _org.eclipse.dltk.internal.ui.actions.FoldingActionGroup;
import _org.eclipse.dltk.ui.actions.GenerateActionGroup;
import _org.eclipse.dltk.ui.actions.GoToNextPreviousMemberAction;
import _org.eclipse.dltk.ui.actions.GotoMatchingBracketAction;

public abstract class ScriptEditor_Actions extends ScriptEditor2 {
	
	public static final int BREAK_ITERATOR__DONE = -1;
	
	private ActionGroup fFoldingGroup;
	private CompositeActionGroup fContextMenuGroup;
	private CompositeActionGroup fActionGroups;
	
	public ScriptEditor_Actions() {
		super();
	}
	
	ActionGroup getFoldingActionGroup() {
		return fFoldingGroup;
	}
	
	@Override
	protected void createActions() {
		super.createActions();

		fActionGroups = new CompositeActionGroup(new ActionGroup[] { });

		fContextMenuGroup = new CompositeActionGroup(new ActionGroup[] { });

		fFoldingGroup = createFoldingActionGroup();

		// ResourceAction resAction = new TextOperationAction(DLTKEditorMessages
		// .getBundleForConstructedKeys(), "ShowDocumentaion.", this,
		// ISourceViewer.INFORMATION, true);
		//
		// resAction = new InformationDispatchAction(DLTKEditorMessages
		// .getBundleForConstructedKeys(), "ShowDocumentation.",
		// (TextOperationAction) resAction);
		//
		// resAction
		// .setActionDefinitionId(IScriptEditorActionDefinitionIds.
		// SHOW_DOCUMENTATION);
		// setAction("ShowDocumentation", resAction);

		Action action = new GotoMatchingBracketAction(this);
		action.setActionDefinitionId(IScriptEditorActionDefinitionIds.GOTO_MATCHING_BRACKET);
		setAction(GotoMatchingBracketAction.GOTO_MATCHING_BRACKET, action);

		Action outlineAction = new TextOperationAction(
				DLTKEditorMessages.getBundleForConstructedKeys(),
				"ShowOutline.", this, //$NON-NLS-1$
				ScriptSourceViewer.SHOW_OUTLINE, true);
		outlineAction.setActionDefinitionId(IScriptEditorActionDefinitionIds.SHOW_OUTLINE);
		setAction(IScriptEditorActionDefinitionIds.SHOW_OUTLINE, outlineAction);

		// ContentAssistProposal
		action = new ContentAssistAction(
				DLTKEditorMessages.getBundleForConstructedKeys(),
				"ContentAssistProposal.", this); //$NON-NLS-1$
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction("ContentAssistProposal", action); //$NON-NLS-1$
		markAsStateDependentAction("ContentAssistProposal", true); //$NON-NLS-1$

		// ContentAssistContextInformation
		action = new TextOperationAction(
				DLTKEditorMessages.getBundleForConstructedKeys(),
				"ContentAssistContextInformation.", this, ISourceViewer.CONTENTASSIST_CONTEXT_INFORMATION); //$NON-NLS-1$
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_CONTEXT_INFORMATION);
		setAction("ContentAssistContextInformation", action); //$NON-NLS-1$
		markAsStateDependentAction("ContentAssistContextInformation", true); //$NON-NLS-1$

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

		final ActionGroup generateActions = createGenerateActionGroup();
		if (generateActions != null) {
			fActionGroups.addGroup(generateActions);
			fContextMenuGroup.addGroup(generateActions);
		}
	}

	protected ActionGroup createGenerateActionGroup() {
		return new GenerateActionGroup(this, ITextEditorActionConstants.GROUP_EDIT);
	}

	protected ActionGroup createFoldingActionGroup() {
		return new FoldingActionGroup(this, getSourceViewer_(), getScriptPreferenceStore());
	}
	
	@Override
	protected void createNavigationActions() {
		super.createNavigationActions();
		final StyledText textWidget = getSourceViewer().getTextWidget();

		IAction action = new NavigatePreviousSubWordAction();
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.WORD_PREVIOUS);
		setAction(ITextEditorActionDefinitionIds.WORD_PREVIOUS, action);
		textWidget.setKeyBinding(SWT.CTRL | SWT.ARROW_LEFT, SWT.NULL);

		action = new NavigateNextSubWordAction();
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.WORD_NEXT);
		setAction(ITextEditorActionDefinitionIds.WORD_NEXT, action);
		textWidget.setKeyBinding(SWT.CTRL | SWT.ARROW_RIGHT, SWT.NULL);

		action = new SelectPreviousSubWordAction();
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.SELECT_WORD_PREVIOUS);
		setAction(ITextEditorActionDefinitionIds.SELECT_WORD_PREVIOUS, action);
		textWidget.setKeyBinding(SWT.CTRL | SWT.SHIFT | SWT.ARROW_LEFT,
				SWT.NULL);

		action = new SelectNextSubWordAction();
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.SELECT_WORD_NEXT);
		setAction(ITextEditorActionDefinitionIds.SELECT_WORD_NEXT, action);
		textWidget.setKeyBinding(SWT.CTRL | SWT.SHIFT | SWT.ARROW_RIGHT,
				SWT.NULL);

		action = new DeletePreviousSubWordAction();
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.DELETE_PREVIOUS_WORD);
		setAction(ITextEditorActionDefinitionIds.DELETE_PREVIOUS_WORD, action);
		textWidget.setKeyBinding(SWT.CTRL | SWT.BS, SWT.NULL);
		markAsStateDependentAction(
				ITextEditorActionDefinitionIds.DELETE_PREVIOUS_WORD, true);

		action = new DeleteNextSubWordAction();
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.DELETE_NEXT_WORD);
		setAction(ITextEditorActionDefinitionIds.DELETE_NEXT_WORD, action);
		textWidget.setKeyBinding(SWT.CTRL | SWT.DEL, SWT.NULL);
		markAsStateDependentAction(
				ITextEditorActionDefinitionIds.DELETE_NEXT_WORD, true);
	}
	
	@Override
	public void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);
		
		menu.insertAfter(ICommonMenuConstants.GROUP_OPEN, new GroupMarker(ICommonMenuConstants.GROUP_SHOW));
		
		ActionContext context = new ActionContext(getSelectionProvider().getSelection());
		context.setInput(getEditorInput());
		fContextMenuGroup.setContext(context);
		fContextMenuGroup.fillContextMenu(menu);
		fContextMenuGroup.setContext(null);
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
	
	/* ----------------- ----------------- */

	/**
	 * Text navigation action to navigate to the next sub-word.
	 * 
	 * 
	 */
	protected abstract class NextSubWordAction extends TextNavigationAction {
		protected DLTKWordIterator fIterator = new DLTKWordIterator();

		/**
		 * Creates a new next sub-word action.
		 * 
		 * @param code
		 *            Action code for the default operation. Must be an action
		 *            code from
		 * @see org.eclipse.swt.custom.ST.
		 */
		protected NextSubWordAction(int code) {
			super(getSourceViewer().getTextWidget(), code);
		}

		/*
		 * @see org.eclipse.jface.action.IAction#run()
		 */
		@Override
		public void run() {
			// Check whether we are in ascriptcode partition and the preference
			// is enabled
			final IPreferenceStore store = getPreferenceStore();
			if (!store.getBoolean(PreferenceConstants.EDITOR_SUB_WORD_NAVIGATION)) {
				super.run();
				return;
			}
			final ISourceViewer viewer = getSourceViewer();
			final IDocument document = viewer.getDocument();
			fIterator
					.setText((CharacterIterator) new DocumentCharacterIterator(
							document));
			int position = widgetOffset2ModelOffset(viewer, viewer
					.getTextWidget().getCaretOffset());
			if (position == -1)
				return;
			int next = findNextPosition(position);
			if (next != BREAK_ITERATOR__DONE) {
				setCaretPosition(next);
				getTextWidget().showSelection();
				fireSelectionChanged();
			}
		}

		/**
		 * Finds the next position after the given position.
		 * 
		 * @param position
		 *            the current position
		 * @return the next position
		 */
		protected int findNextPosition(int position) {
			ISourceViewer viewer = getSourceViewer();
			int widget = -1;
			while (position != BREAK_ITERATOR__DONE && widget == -1) { // TODO:
				// optimize
				position = fIterator.following(position);
				if (position != BREAK_ITERATOR__DONE)
					widget = modelOffset2WidgetOffset(viewer, position);
			}
			return position;
		}

		/**
		 * Sets the caret position to the sub-word boundary given with
		 * <code>position</code>.
		 * 
		 * @param position
		 *            Position where the action should move the caret
		 */
		protected abstract void setCaretPosition(int position);
	}

	/**
	 * Text navigation action to navigate to the next sub-word.
	 */
	protected class NavigateNextSubWordAction extends NextSubWordAction {
		/**
		 * Creates a new navigate next sub-word action.
		 */
		public NavigateNextSubWordAction() {
			super(ST.WORD_NEXT);
		}

		@Override
		protected void setCaretPosition(final int position) {
			getTextWidget().setCaretOffset(
					modelOffset2WidgetOffset(getSourceViewer(), position));
		}
	}

	/**
	 * Text operation action to delete the next sub-word.
	 */
	protected class DeleteNextSubWordAction extends NextSubWordAction implements
			IUpdate {
		/**
		 * Creates a new delete next sub-word action.
		 */
		public DeleteNextSubWordAction() {
			super(ST.DELETE_WORD_NEXT);
		}

		@Override
		protected void setCaretPosition(final int position) {
			if (!validateEditorInputState())
				return;
			final ISourceViewer viewer = getSourceViewer();
			final int caret, length;
			Point selection = viewer.getSelectedRange();
			if (selection.y != 0) {
				caret = selection.x;
				length = selection.y;
			} else {
				caret = widgetOffset2ModelOffset(viewer, viewer.getTextWidget()
						.getCaretOffset());
				length = position - caret;
			}
			try {
				viewer.getDocument().replace(caret, length, ""); //$NON-NLS-1$
			} catch (BadLocationException exception) {
				// Should not happen
			}
		}

		@Override
		protected int findNextPosition(int position) {
			return fIterator.following(position);
		}

		/*
		 * @see org.eclipse.ui.texteditor.IUpdate#update()
		 */
		@Override
		public void update() {
			setEnabled(isEditorInputModifiable());
		}
	}

	/**
	 * Text operation action to select the next sub-word.
	 * 
	 * 
	 */
	protected class SelectNextSubWordAction extends NextSubWordAction {
		/**
		 * Creates a new select next sub-word action.
		 */
		public SelectNextSubWordAction() {
			super(ST.SELECT_WORD_NEXT);
		}

		@Override
		protected void setCaretPosition(final int position) {
			final ISourceViewer viewer = getSourceViewer();
			final StyledText text = viewer.getTextWidget();
			if (text != null && !text.isDisposed()) {
				final Point selection = text.getSelection();
				final int caret = text.getCaretOffset();
				final int offset = modelOffset2WidgetOffset(viewer, position);
				if (caret == selection.x)
					text.setSelectionRange(selection.y, offset - selection.y);
				else
					text.setSelectionRange(selection.x, offset - selection.x);
			}
		}
	}

	/**
	 * Text navigation action to navigate to the previous sub-word.
	 * 
	 * 
	 */
	protected abstract class PreviousSubWordAction extends TextNavigationAction {
		protected DLTKWordIterator fIterator = new DLTKWordIterator();

		/**
		 * Creates a new previous sub-word action.
		 * 
		 * @param code
		 *            Action code for the default operation. Must be an action
		 *            code from
		 * @see org.eclipse.swt.custom.ST.
		 */
		protected PreviousSubWordAction(final int code) {
			super(getSourceViewer().getTextWidget(), code);
		}

		/*
		 * @see org.eclipse.jface.action.IAction#run()
		 */
		@Override
		public void run() {
			// Check whether we are in ascriptcode partition and the preference
			// is enabled
			final IPreferenceStore store = getPreferenceStore();
			if (!store
					.getBoolean(PreferenceConstants.EDITOR_SUB_WORD_NAVIGATION)) {
				super.run();
				return;
			}
			final ISourceViewer viewer = getSourceViewer();
			final IDocument document = viewer.getDocument();
			fIterator
					.setText((CharacterIterator) new DocumentCharacterIterator(
							document));
			int position = widgetOffset2ModelOffset(viewer, viewer
					.getTextWidget().getCaretOffset());
			if (position == -1)
				return;
			int previous = findPreviousPosition(position);
			if (previous != BREAK_ITERATOR__DONE) {
				setCaretPosition(previous);
				getTextWidget().showSelection();
				fireSelectionChanged();
			}
		}

		/**
		 * Finds the previous position before the given position.
		 * 
		 * @param position
		 *            the current position
		 * @return the previous position
		 */
		protected int findPreviousPosition(int position) {
			ISourceViewer viewer = getSourceViewer();
			int widget = -1;
			while (position != BREAK_ITERATOR__DONE && widget == -1) { // TODO:
				// optimize
				position = fIterator.preceding(position);
				if (position != BREAK_ITERATOR__DONE)
					widget = modelOffset2WidgetOffset(viewer, position);
			}
			return position;
		}

		/**
		 * Sets the caret position to the sub-word boundary given with
		 * <code>position</code>.
		 * 
		 * @param position
		 *            Position where the action should move the caret
		 */
		protected abstract void setCaretPosition(int position);
	}

	/**
	 * Text navigation action to navigate to the previous sub-word.
	 */
	protected class NavigatePreviousSubWordAction extends PreviousSubWordAction {
		/**
		 * Creates a new navigate previous sub-word action.
		 */
		public NavigatePreviousSubWordAction() {
			super(ST.WORD_PREVIOUS);
		}

		@Override
		protected void setCaretPosition(final int position) {
			getTextWidget().setCaretOffset(
					modelOffset2WidgetOffset(getSourceViewer(), position));
		}
	}

	/**
	 * Text operation action to delete the previous sub-word.
	 */
	protected class DeletePreviousSubWordAction extends PreviousSubWordAction
			implements IUpdate {
		/**
		 * Creates a new delete previous sub-word action.
		 */
		public DeletePreviousSubWordAction() {
			super(ST.DELETE_WORD_PREVIOUS);
		}

		@Override
		protected void setCaretPosition(int position) {
			if (!validateEditorInputState())
				return;
			final int length;
			final ISourceViewer viewer = getSourceViewer();
			Point selection = viewer.getSelectedRange();
			if (selection.y != 0) {
				position = selection.x;
				length = selection.y;
			} else {
				length = widgetOffset2ModelOffset(viewer, viewer
						.getTextWidget().getCaretOffset())
						- position;
			}
			try {
				viewer.getDocument().replace(position, length, ""); //$NON-NLS-1$
			} catch (BadLocationException exception) {
				// Should not happen
			}
		}

		@Override
		protected int findPreviousPosition(int position) {
			return fIterator.preceding(position);
		}

		@Override
		public void update() {
			setEnabled(isEditorInputModifiable());
		}
	}

	/**
	 * Text operation action to select the previous sub-word.
	 */
	protected class SelectPreviousSubWordAction extends PreviousSubWordAction {
		/**
		 * Creates a new select previous sub-word action.
		 */
		public SelectPreviousSubWordAction() {
			super(ST.SELECT_WORD_PREVIOUS);
		}

		@Override
		protected void setCaretPosition(final int position) {
			final ISourceViewer viewer = getSourceViewer();
			final StyledText text = viewer.getTextWidget();
			if (text != null && !text.isDisposed()) {
				final Point selection = text.getSelection();
				final int caret = text.getCaretOffset();
				final int offset = modelOffset2WidgetOffset(viewer, position);
				if (caret == selection.x)
					text.setSelectionRange(selection.y, offset - selection.y);
				else
					text.setSelectionRange(selection.x, offset - selection.x);
			}
		}
	}
	
}