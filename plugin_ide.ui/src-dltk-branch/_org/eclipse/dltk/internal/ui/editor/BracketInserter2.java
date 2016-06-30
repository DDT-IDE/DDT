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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentEvent;
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
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import melnorme.lang.ide.core.LangCore;

public abstract class BracketInserter2 implements VerifyKeyListener,
		ILinkedModeListener {

	protected final ScriptEditor editor;
	protected boolean fCloseBrackets = true;
	protected boolean fCloseStrings = true;
	protected boolean fCloseAngularBrackets = true;
	protected final String CATEGORY;
	protected IPositionUpdater fUpdater;
	protected Stack<BracketLevel> fBracketLevelStack = new Stack<BracketLevel>();

	protected BracketInserter2(ScriptEditor editor) {
		this.editor = editor;
		CATEGORY = this.editor.toString();
		fUpdater = new ExclusivePositionUpdater(CATEGORY);
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

	@SuppressWarnings("unused") 
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
		final ISourceViewer sourceViewer = this.editor.getSourceViewer_();
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
									LangCore.logError("position error", e);
								}
							}

							if (fBracketLevelStack.size() == 0) {
								document.removePositionUpdater(fUpdater);
								try {
									document.removePositionCategory(CATEGORY);
								} catch (BadPositionCategoryException e) {
									LangCore.logError("position error", e);
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

		BracketLevel level = new BracketLevel();
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

		final ISourceViewer sourceViewer = this.editor.getSourceViewer_();
		level.fUI = new EditorLinkedModeUI(model, sourceViewer);
		level.fUI.setSimpleMode(true);
		level.fUI.setExitPolicy(new ExitPolicy(editor, closingCharacter,
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
	
	public static class BracketLevel {
		public int fOffset;
		public int fLength;
		public LinkedModeUI fUI;
		public Position fFirstPosition;
		public Position fSecondPosition;
	}

	public class ExitPolicy implements IExitPolicy {

		public final char fExitCharacter;
		public final char fEscapeCharacter;
		public final Stack<BracketLevel> fStack;
		public final int fSize;
		protected final ScriptEditor editor;

		public ExitPolicy(ScriptEditor scriptEditor, char exitCharacter, char escapeCharacter, 
				Stack<BracketLevel> stack) {
			this.editor = scriptEditor;
			fExitCharacter = exitCharacter;
			fEscapeCharacter = escapeCharacter;
			fStack = stack;
			fSize = fStack.size();
		}

		/*
		 * @see
		 * org.eclipse.jdt.internal.ui.text.link.LinkedPositionUI.ExitPolicy
		 * #doExit(org.eclipse.jdt.internal.ui.text.link.LinkedPositionManager,
		 * org.eclipse.swt.events.VerifyEvent, int, int)
		 */
		@Override
		public ExitFlags doExit(LinkedModeModel model, VerifyEvent event,
				int offset, int length) {

			if (fSize == fStack.size() && !isMasked(offset)) {
				if (event.character == fExitCharacter) {
					BracketLevel level = (BracketLevel) fStack.peek();
					if (level.fFirstPosition.offset > offset
							|| level.fSecondPosition.offset < offset)
						return null;
					if (level.fSecondPosition.offset == offset && length == 0)
						// don't enter the character if if its the closing peer
						return new ExitFlags(ILinkedModeListener.UPDATE_CARET,
								false);
				}
				// when entering an anonymous class between the parenthesis', we
				// don't want
				// to jump after the closing parenthesis when return is pressed
				if (event.character == SWT.CR && offset > 0) {
					// ssanders: If completion popup is displayed, Enter
					// dismisses it
					if (editor.getSourceViewer_().fInCompletionSession)
						return new ExitFlags(ILinkedModeListener.NONE, true);

					IDocument document = editor.getSourceViewer_().getDocument();
					try {
						if (document.getChar(offset - 1) == '{')
							return new ExitFlags(ILinkedModeListener.EXIT_ALL,
									true);
					} catch (BadLocationException e) {
					}
				}
			}
			return null;
		}

		private boolean isMasked(int offset) {
			IDocument document = editor.getSourceViewer_().getDocument();
			try {
				return fEscapeCharacter == document.getChar(offset - 1);
			} catch (BadLocationException e) {
			}
			return false;
		}
	}

	static class ExclusivePositionUpdater implements IPositionUpdater {

		/** The position category. */
		private final String fCategory;

		/**
		 * Creates a new updater for the given <code>category</code>.
		 * 
		 * @param category
		 *            the new category.
		 */
		public ExclusivePositionUpdater(String category) {
			fCategory = category;
		}

		/*
		 * @see
		 * org.eclipse.jface.text.IPositionUpdater#update(org.eclipse.jface.
		 * text.DocumentEvent)
		 */
		@Override
		public void update(DocumentEvent event) {

			int eventOffset = event.getOffset();
			int eventOldLength = event.getLength();
			int eventNewLength = event.getText() == null ? 0 : event.getText()
					.length();
			int deltaLength = eventNewLength - eventOldLength;

			try {
				Position[] positions = event.getDocument().getPositions(
						fCategory);

				for (int i = 0; i != positions.length; i++) {

					Position position = positions[i];

					if (position.isDeleted())
						continue;

					int offset = position.getOffset();
					int length = position.getLength();
					int end = offset + length;

					if (offset >= eventOffset + eventOldLength)
						// position comes
						// after change - shift
						position.setOffset(offset + deltaLength);
					else if (end <= eventOffset) {
						// position comes way before change -
						// leave alone
					} else if (offset <= eventOffset
							&& end >= eventOffset + eventOldLength) {
						// event completely internal to the position - adjust
						// length
						position.setLength(length + deltaLength);
					} else if (offset < eventOffset) {
						// event extends over end of position - adjust length
						int newEnd = eventOffset;
						position.setLength(newEnd - offset);
					} else if (end > eventOffset + eventOldLength) {
						// event extends from before position into it - adjust
						// offset
						// and length
						// offset becomes end of event, length adjusted
						// accordingly
						int newOffset = eventOffset + eventNewLength;
						position.setOffset(newOffset);
						position.setLength(end - newOffset);
					} else {
						// event consumes the position - delete it
						position.delete();
					}
				}
			} catch (BadPositionCategoryException e) {
				// ignore and return
			}
		}

		/**
		 * Returns the position category.
		 * 
		 * @return the position category
		 */
		public String getCategory() {
			return fCategory;
		}

	}

}
