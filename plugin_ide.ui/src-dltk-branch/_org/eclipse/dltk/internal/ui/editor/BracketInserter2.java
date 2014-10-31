/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
/**
 * 
 */
package _org.eclipse.dltk.internal.ui.editor;

import java.util.Stack;

import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor.BracketLevel;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

public abstract class BracketInserter2 implements VerifyKeyListener,
		ILinkedModeListener {

	protected final ScriptEditor2 editor;
	protected boolean fCloseBrackets = true;
	protected boolean fCloseStrings = true;
	protected boolean fCloseAngularBrackets = true;
	protected final String CATEGORY;
	protected IPositionUpdater fUpdater;
	protected Stack<BracketLevel> fBracketLevelStack = new Stack<BracketLevel>();

	protected BracketInserter2(ScriptEditor2 editor) {
		this.editor = editor;
		CATEGORY = this.editor.toString();
		fUpdater = new ScriptEditor2.ExclusivePositionUpdater(CATEGORY);
	}

	public void setCloseBracketsEnabled(boolean enabled) {
		fCloseBrackets = enabled;
	}

	public void setCloseStringsEnabled(boolean enabled) {
		fCloseStrings = enabled;
	}

	public void setCloseAngularBracketsEnabled(boolean enabled) {
		fCloseAngularBrackets = enabled;
	}

	protected boolean isAngularIntroducer(String identifier) {
		return false;
	}

	protected static char getEscapeCharacter(char character) {
		switch (character) {
		case '"':
		case '\'':
			return '\\';
		default:
			return 0;
		}
	}

	protected static char getPeerCharacter(char character) {
		switch (character) {
		case '(':
			return ')';

		case ')':
			return '(';

		case '<':
			return '>';

		case '>':
			return '<';

		case '[':
			return ']';

		case ']':
			return '[';

		case '{':
			return '}';

		case '}':
			return '{';

		case '"':
			return character;

		case '\'':
			return character;

		default:
			throw new IllegalArgumentException();
		}
	}

	@Override
	public abstract void verifyKey(VerifyEvent event);

	@Override
	public void left(LinkedModeModel environment, int flags) {

		final BracketLevel level = fBracketLevelStack.pop();

		if (flags != ILinkedModeListener.EXTERNAL_MODIFICATION) {
			return;
		}

		// remove brackets
		final ISourceViewer sourceViewer = this.editor.getScriptSourceViewer();
		final IDocument document = sourceViewer.getDocument();
		if (document instanceof IDocumentExtension) {
			IDocumentExtension extension = (IDocumentExtension) document;
			extension.registerPostNotificationReplace(null,
					new IDocumentExtension.IReplace() {

						@Override
						public void perform(IDocument d, IDocumentListener owner) {
							if ((level.fFirstPosition.isDeleted || level.fFirstPosition.length == 0)
									&& !level.fSecondPosition.isDeleted
									&& level.fSecondPosition.offset == level.fFirstPosition.offset) {
								try {
									document.replace(
											level.fSecondPosition.offset,
											level.fSecondPosition.length, ""); //$NON-NLS-1$
								} catch (BadLocationException e) {
									DLTKUIPlugin.log(e);
								}
							}

							if (fBracketLevelStack.size() == 0) {
								document.removePositionUpdater(fUpdater);
								try {
									document.removePositionCategory(CATEGORY);
								} catch (BadPositionCategoryException e) {
									DLTKUIPlugin.log(e);
								}
							}
						}
					});
		}
	}

	@Override
	public void suspend(LinkedModeModel environment) {
	}

	@Override
	public void resume(LinkedModeModel environment, int flags) {
	}

	protected void insertBrackets(final IDocument document, final int offset,
			final int length, final char character, final char closingCharacter)
			throws BadLocationException, BadPositionCategoryException {
		document.replace(offset, length, new String(new char[] { character,
				closingCharacter }));

		BracketLevel level = new ScriptEditor.BracketLevel();
		fBracketLevelStack.push(level);

		LinkedPositionGroup group = new LinkedPositionGroup();
		group.addPosition(new LinkedPosition(document, offset + 1, 0,
				LinkedPositionGroup.NO_STOP));

		LinkedModeModel model = new LinkedModeModel();
		model.addLinkingListener(this);
		model.addGroup(group);
		model.forceInstall();

		level.fOffset = offset;
		level.fLength = 2;

		// set up position tracking for our magic peers
		if (fBracketLevelStack.size() == 1) {
			document.addPositionCategory(CATEGORY);
			document.addPositionUpdater(fUpdater);
		}

		level.fFirstPosition = new Position(offset, 1);
		level.fSecondPosition = new Position(offset + 1, 1);
		document.addPosition(CATEGORY, level.fFirstPosition);
		document.addPosition(CATEGORY, level.fSecondPosition);

		final ISourceViewer sourceViewer = this.editor.getScriptSourceViewer();
		level.fUI = new EditorLinkedModeUI(model, sourceViewer);
		level.fUI.setSimpleMode(true);
		level.fUI.setExitPolicy(this.editor.new ExitPolicy(closingCharacter,
				getEscapeCharacter(closingCharacter), fBracketLevelStack));
		level.fUI.setExitPosition(sourceViewer, offset + 2, 0,
				Integer.MAX_VALUE);
		level.fUI.setCyclingMode(LinkedModeUI.CYCLE_NEVER);
		level.fUI.enter();

		IRegion newSelection = level.fUI.getSelectedRegion();
		sourceViewer.setSelectedRange(newSelection.getOffset(), newSelection
				.getLength());
	}

	/**
	 * Validates the content type at the specified location
	 * 
	 * @param document
	 * @param offset
	 * @param partitioning
	 * @param contentTypes
	 *            acceptable content types, if empty only
	 *            IDocument.DEFAULT_CONTENT_TYPE is checked.
	 * @return
	 * @throws BadLocationException
	 * @since 2.0
	 */
	protected static boolean validatePartitioning(IDocument document,
			int offset, String partitioning, String... contentTypes)
			throws BadLocationException {
		final ITypedRegion partition = TextUtilities.getPartition(document,
				partitioning, offset, true);
		if (contentTypes.length != 0) {
			for (String contentType : contentTypes) {
				if (contentType.equals(partition.getType()))
					return true;
			}
			return false;
		} else {
			return IDocument.DEFAULT_CONTENT_TYPE.equals(partition.getType());
		}
	}
}
