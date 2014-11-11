/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.ui.actions;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.internal.ui.editor.DLTKEditorMessages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;

import _org.eclipse.dltk.internal.ui.editor.ScriptEditor2;

public class GotoMatchingBracketAction extends Action {

	public final static String GOTO_MATCHING_BRACKET = "GotoMatchingBracket"; //$NON-NLS-1$

	private final ScriptEditor2 fEditor;

	public GotoMatchingBracketAction(ScriptEditor2 editor) {
		super(DLTKEditorMessages.GotoMatchingBracket_label);
		Assert.isNotNull(editor);
		fEditor = editor;
		setEnabled(true);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
		// IJavaHelpContextIds.GOTO_MATCHING_BRACKET_ACTION);
	}

	@Override
	public void run() {
		gotoMatchingBracket(fEditor);
	}
	
	/**
	 * Returns the signed current selection. The length will be negative if the
	 * resulting selection is right-to-left (RtoL).
	 * <p>
	 * The selection offset is model based.
	 * </p>
	 * 
	 * @param sourceViewer
	 *            the source viewer
	 * @return a region denoting the current signed selection, for a resulting
	 *         RtoL selections length is < 0
	 */
	protected static IRegion getSignedSelection(ISourceViewer sourceViewer) {
		StyledText text = sourceViewer.getTextWidget();
		Point selection = text.getSelectionRange();

		if (text.getCaretOffset() == selection.x) {
			selection.x = selection.x + selection.y;
			selection.y = -selection.y;
		}
		
		selection.x = ScriptEditor2.widgetOffset2ModelOffset_(sourceViewer, selection.x);
		
		return new Region(selection.x, selection.y);
	}

	protected final static char[] BRACKETS = { '{', '}', '(', ')', '[', ']' };

	protected static boolean isBracket(char character) {
		for (int i = 0; i != BRACKETS.length; ++i)
			if (character == BRACKETS[i])
				return true;
		return false;
	}

	protected static boolean isSurroundedByBrackets(IDocument document,
			int offset) {
		if (offset == 0 || offset == document.getLength())
			return false;

		try {
			return isBracket(document.getChar(offset - 1))
					&& isBracket(document.getChar(offset));

		} catch (BadLocationException e) {
			return false;
		}
	}

	public static void gotoMatchingBracket(ScriptEditor2 editor) {
		final ICharacterPairMatcher bracketMatcher = editor.getBracketMatcher();
		if (bracketMatcher == null) {
			return;
		}
		ISourceViewer sourceViewer = editor.getSourceViewer_();
		IDocument document = sourceViewer.getDocument();
		if (document == null)
			return;

		IRegion selection = getSignedSelection(sourceViewer);

		int selectionLength = Math.abs(selection.getLength());
		if (selectionLength > 1) {
			editor.setStatusLineErrorMessage(DLTKEditorMessages.ScriptEditor_nobracketSelected);
			sourceViewer.getTextWidget().getDisplay().beep();
			return;
		}

		// #26314
		int sourceCaretOffset = selection.getOffset() + selection.getLength();
		if (isSurroundedByBrackets(document, sourceCaretOffset))
			sourceCaretOffset -= selection.getLength();

		IRegion region = bracketMatcher.match(document, sourceCaretOffset);
		if (region == null) {
			editor.setStatusLineErrorMessage(DLTKEditorMessages.ScriptEditor_noMatchingBracketFound);
			sourceViewer.getTextWidget().getDisplay().beep();
			return;
		}

		int offset = region.getOffset();
		int length = region.getLength();

		if (length < 1)
			return;

		int anchor = bracketMatcher.getAnchor();
		// http://dev.eclipse.org/bugs/show_bug.cgi?id=34195
		int targetOffset = (ICharacterPairMatcher.RIGHT == anchor) ? offset + 1
				: offset + length;

		boolean visible = false;
		if (sourceViewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension = (ITextViewerExtension5) sourceViewer;
			visible = (extension.modelOffset2WidgetOffset(targetOffset) > -1);
		} else {
			IRegion visibleRegion = sourceViewer.getVisibleRegion();
			// http://dev.eclipse.org/bugs/show_bug.cgi?id=34195
			visible = (targetOffset >= visibleRegion.getOffset() && targetOffset <= visibleRegion
					.getOffset() + visibleRegion.getLength());
		}

		if (!visible) {
			editor.setStatusLineErrorMessage(DLTKEditorMessages.ScriptEditor_matchingBracketIsOutsideSelectedElement);
			sourceViewer.getTextWidget().getDisplay().beep();
			return;
		}

		if (selection.getLength() < 0)
			targetOffset -= selection.getLength();

		sourceViewer.setSelectedRange(targetOffset, selection.getLength());
		sourceViewer.revealRange(targetOffset, selection.getLength());
	}

}