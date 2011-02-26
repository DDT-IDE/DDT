/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation
 * 	   Alex Panchenko <alex@xored.com>
 *******************************************************************************/
package org.eclipse.dltk.ruby.internal.ui.text;

import java.util.Arrays;

import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.PreferencesLookupDelegate;
import org.eclipse.dltk.ruby.internal.ui.RubyUI;
import org.eclipse.dltk.ui.CodeFormatterConstants;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.text.util.AutoEditUtils;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;

public class RubyAutoEditStrategy extends DefaultIndentLineAutoEditStrategy {

	private static final int[] INDENT_TO_BLOCK_TOKENS = {
			IRubySymbols.TokenELSE, IRubySymbols.TokenELSIF,
			IRubySymbols.TokenEND, IRubySymbols.TokenENSURE,
			IRubySymbols.TokenRESCUE, IRubySymbols.TokenWHEN,
			IRubySymbols.TokenRBRACE };

	private static final int[] CONTINUATION_TOKENS = {
			IRubySymbols.TokenBACKSLASH, IRubySymbols.TokenCOMMA,
			IRubySymbols.TokenSLASH, IRubySymbols.TokenPLUS,
			IRubySymbols.TokenMINUS, IRubySymbols.TokenSTAR };

	private static final int[] REMOVE_IDENTATION_TOKENS = {
			IRubySymbols.TokenRDOCBEGIN, IRubySymbols.TokenRDOCEND };

	static {
		Arrays.sort(INDENT_TO_BLOCK_TOKENS);
		Arrays.sort(CONTINUATION_TOKENS);
		Arrays.sort(REMOVE_IDENTATION_TOKENS);
	}

	private boolean fIsSmartMode;
	private boolean fCloseBlocks = true;
	private RubyPreferenceInterpreter fPreferences;

	public RubyAutoEditStrategy(String partitioning) {
		this(partitioning, RubyUI.getDefault().getPreferenceStore());
	}

	public RubyAutoEditStrategy(String partitioning, IPreferenceStore store) {
		fPreferences = new RubyPreferenceInterpreter(store);
	}

	private void clearCachedValues() {
		fCloseBlocks = fPreferences.closeBlocks();
		fIsSmartMode = fPreferences.isSmartMode();
	}

	private void closeBlock(IDocument d, DocumentCommand c, String indent,
			String afterCursor, RubyHeuristicScanner scanner)
			throws BadLocationException {
		c.caretOffset = c.offset + c.text.length();
		c.length = afterCursor.length();
		c.shiftsCaret = false;
		String delimiter = TextUtilities.getDefaultLineDelimiter(d);
		c.text += afterCursor.trim() + delimiter + indent
				+ getApropriateBlockEnding(d, scanner, c.offset);
	}

	private String getApropriateBlockEnding(IDocument d,
			RubyHeuristicScanner scanner, int offset)
			throws BadLocationException {
		int beginning = scanner.findBlockBeginningOffset(offset);
		if (beginning == RubyHeuristicScanner.NOT_FOUND)
			throw new BadLocationException();

		IRegion line = d.getLineInformationOfOffset(beginning);
		int ending = Math.min(line.getOffset() + line.getLength(), offset);
		int blockOffset = scanner.findBlockBeginningOffset(ending);
		int token = scanner.nextToken(blockOffset, ending);
		if (token == IRubySymbols.TokenLBRACE) {
			return "}"; //$NON-NLS-1$
		} else {
			return "end"; //$NON-NLS-1$
		}
	}

	private boolean isSmartMode() {
		return fIsSmartMode;
	}

	public void customizeDocumentCommand(IDocument d, DocumentCommand c) {
		if (c.doit == false)
			return;

		clearCachedValues();
		if (!isSmartMode()) {
			super.customizeDocumentCommand(d, c);
			return;
		}

		try {
			if (c.length == 0 && c.text != null && isLineDelimiter(d, c.text))
				smartIndentAfterNewLine(d, c);
			else if (c.length == 0 && c.text != null && isSpace(c.text))
				smartInsertEndOnSpace(d, c);
			else if (isRepresentingTab(c.text))
				smartTab(d, c);
			else if (c.text.length() == 1)
				smartIndentOnKeypress(d, c);
			else if (c.text.length() > 1 && fPreferences.isSmartPaste())
				smartPaste(d, c); // no smart backspace for paste
			else
				super.customizeDocumentCommand(d, c);
		} catch (BadLocationException e) {
			DLTKUIPlugin.log(e);
		}
	}

	/**
	 * @param document
	 * @param c
	 * @throws BadLocationException
	 */
	private void smartInsertEndOnSpace(IDocument document, DocumentCommand c)
			throws BadLocationException {
		IRegion line = document.getLineInformationOfOffset(c.offset);
		RubyHeuristicScanner scanner = new RubyHeuristicScanner(document);
		int prevToken = scanner.previousToken(c.offset - 1, line.getOffset());
		if (c.offset > 1 && prevToken == ISymbols.TokenEOF) {
			return;
		}
		int prevTokenOffset = scanner.getPosition();
		if (prevTokenOffset < 0)
			prevTokenOffset = 0;
		String previous = document.get(prevTokenOffset,
				c.offset - prevTokenOffset).trim();

		int hasOffset = line.getOffset();
		int hasLength = (prevTokenOffset - line.getOffset());
		boolean hasPrefixContent = ((hasLength > 0) && (document.get(hasOffset,
				hasLength).trim().length() > 0));

		hasOffset = (prevTokenOffset + previous.length() + 1);
		hasLength = (line.getLength() - (hasOffset - line.getOffset()));
		boolean hasSuffixContent = ((hasLength > 0)
				&& ((hasOffset + hasLength) <= document.getLength()) && (document
				.get(hasOffset, hasLength).trim().length() > 0));

		if (!"case".equals(previous) && !"class".equals(previous) //$NON-NLS-1$ //$NON-NLS-2$
				&& !"def".equals(previous) && !"do".equals(previous) //$NON-NLS-1$ //$NON-NLS-2$
				&& !"if".equals(previous) && !"module".equals(previous) //$NON-NLS-1$ //$NON-NLS-2$
				&& !"unless".equals(previous) //$NON-NLS-1$
				&& !"while".equals(previous)) //$NON-NLS-1$
			return;
		if ((hasPrefixContent && !"do".equals(previous)) //$NON-NLS-1$
				|| hasSuffixContent)
			return;
		if ((prevTokenOffset + previous.length()) < (c.offset - 1))
			return;
		if (fCloseBlocks && !isBlockClosed(document, c.offset)) {
			c.caretOffset = c.offset + 1;
			c.shiftsCaret = false;
			c.text = c.text + TextUtilities.getDefaultLineDelimiter(document)
					+ getBlockIndent(document, c.offset, scanner) + "end"; //$NON-NLS-1$
		}
	}

	/**
	 * Tells whether the given inserted string represents hitting the Tab key.
	 * 
	 * @param text
	 *            the text to check
	 * @return <code>true</code> if the text represents hitting the Tab key
	 * @since 3.5
	 */
	private boolean isRepresentingTab(String text) {
		if (text == null)
			return false;

		if (isInsertingSpacesForTab()) {
			if (text.length() == 0
					|| text.length() > getVisualTabLengthPreference())
				return false;
			for (int i = 0; i < text.length(); i++) {
				if (text.charAt(i) != ' ')
					return false;
			}
			return true;
		} else
			return text.length() == 1 && text.charAt(0) == '\t';
	}

	/**
	 * The preference setting that tells whether to insert spaces when pressing
	 * the Tab key.
	 * 
	 * @return <code>true</code> if spaces are inserted when pressing the Tab
	 *         key
	 * @since 3.5
	 */
	private boolean isInsertingSpacesForTab() {
		return CodeFormatterConstants.SPACE.equals(getOption(RubyUI.PLUGIN_ID,
				CodeFormatterConstants.FORMATTER_TAB_CHAR));
	}

	private IScriptProject getProject() {
		// TODO implement getProject()
		return null;
	}

	/**
	 * @param project
	 * @param qualifier
	 * @param key
	 * @return
	 */
	private String getOption(String qualifier, String key) {
		return new PreferencesLookupDelegate(getProject()).getString(qualifier,
				key);
	}

	/**
	 * @param project
	 * @param qualifier
	 * @param key
	 * @return
	 */
	private int getIntOption(String qualifier, String key) {
		return new PreferencesLookupDelegate(getProject()).getInt(qualifier,
				key);
	}

	private int getVisualTabLengthPreference() {
		return getIntOption(RubyUI.PLUGIN_ID,
				CodeFormatterConstants.FORMATTER_TAB_SIZE);
	}

	private boolean isSpace(String text) {
		return text.length() == 1 && text.charAt(0) == ' ';
	}

	private boolean isLineDelimiter(IDocument document, String text) {
		String[] delimiters = document.getLegalLineDelimiters();
		return delimiters != null
				&& TextUtilities.equals(delimiters, text) > -1;
	}

	private void smartTab(IDocument d, DocumentCommand c)
			throws BadLocationException {
		IRegion info = d.getLineInformationOfOffset(c.offset);
		int endOffset = info.getOffset() + info.getLength();
		String line = d.get(info.getOffset(), info.getLength());
		String linePrefix = line.substring(0, c.offset - info.getOffset());
		final String linePostfix = line.substring(c.offset - info.getOffset(),
				endOffset - info.getOffset());
		String postfixIndent = AutoEditUtils.getLineIndent(linePostfix);

		RubyHeuristicScanner scanner = new RubyHeuristicScanner(d);
		String rightIndent;
		if (nextIsIdentToBlockToken(scanner, c.offset, endOffset)) {
			rightIndent = getBlockIndent(d, c.offset, scanner);
		} else {
			rightIndent = getLineIndent(d, c.offset, scanner);
		}

		if (linePrefix.trim().length() != 0
				|| (linePostfix.trim().length() != 0
						&& postfixIndent.length() == 0 && computeVisualLength(linePrefix) >= computeVisualLength(rightIndent))) {
			c.text = fPreferences.getIndent();
			return;
		}

		c.text = rightIndent + linePostfix.trim();
		c.offset = info.getOffset();
		c.length = info.getLength();
		c.caretOffset = info.getOffset() + rightIndent.length();
		c.shiftsCaret = false;
	}

	public void smartIndentOnKeypress(IDocument d, DocumentCommand c)
			throws BadLocationException {
		RubyHeuristicScanner scanner = new RubyHeuristicScanner(d);
		IRegion info = d.getLineInformationOfOffset(c.offset);
		int token = scanner.previousTokenAfterInput(c.offset, c.text);

		if (Arrays.binarySearch(INDENT_TO_BLOCK_TOKENS, token) >= 0) {
			String indent = ""; //$NON-NLS-1$
			indent = getBlockIndent(d, info.getOffset(), scanner);

			// ssanders: If Block was opened on same line, add extra indent
			int blockStart = scanner.findBlockBeginningOffset(c.offset);
			int prevBlockStart = scanner.findBlockBeginningOffset(info
					.getOffset());
			if (blockStart >= info.getOffset() && prevBlockStart != -1)
				indent += fPreferences.getIndent();

			int pos = scanner.findNonWhitespaceForwardInAnyPartition(info
					.getOffset(), c.offset);
			String line = ""; //$NON-NLS-1$
			if (pos != RubyHeuristicScanner.NOT_FOUND) {
				line = d.get(pos, c.offset - pos);
			}

			c.text = indent + line + c.text;
			c.length = c.offset - info.getOffset();
			c.offset = info.getOffset();

		} else if (Arrays.binarySearch(REMOVE_IDENTATION_TOKENS, token) >= 0) {
			int start = scanner.findNonWhitespaceForward(info.getOffset(),
					c.offset);
			c.text = d.get(start, c.offset - start) + c.text;
			c.length = c.offset - info.getOffset();
			c.offset = info.getOffset();
		} else {
			// if previous was indented to block, restore original indentation
			int wsPos = scanner.findNonIdentifierBackward(c.offset, info
					.getOffset());
			int previosToken = scanner.previousToken(c.offset, wsPos);
			if (Arrays.binarySearch(INDENT_TO_BLOCK_TOKENS, previosToken) >= 0
					&& Character.isJavaIdentifierPart(c.text.charAt(0))) {
				String indent = getPreviousLineIndent(d, info.getOffset() - 1,
						scanner);

				int pos = scanner.findNonWhitespaceForwardInAnyPartition(info
						.getOffset(), c.offset);
				String line = ""; //$NON-NLS-1$
				if (pos != RubyHeuristicScanner.NOT_FOUND) {
					line = d.get(pos, c.offset - pos);
				}

				c.text = indent + line + c.text;
				c.length = c.offset - info.getOffset();
				c.offset = info.getOffset();
			}
		}
	}

	private String getLineIndent(IDocument d, int offset,
			RubyHeuristicScanner scanner) {
		int blockOffset = scanner.findBlockBeginningOffset(offset);
		if (blockOffset != RubyHeuristicScanner.NOT_FOUND) {
			try {
				return AutoEditUtils.getLineIndent(d, d
						.getLineOfOffset(blockOffset))
						+ fPreferences.getIndent();
			} catch (BadLocationException e) {
				DLTKUIPlugin.log(e);
			}
		}
		return ""; //$NON-NLS-1$
	}

	private String getBlockIndent(IDocument d, int offset,
			RubyHeuristicScanner scanner) {
		int blockOffset = scanner.findBlockBeginningOffset(offset);
		if (blockOffset != RubyHeuristicScanner.NOT_FOUND) {
			try {
				return AutoEditUtils.getLineIndent(d, d
						.getLineOfOffset(blockOffset));
			} catch (BadLocationException e) {
				DLTKUIPlugin.log(e);
			}
		}
		return ""; //$NON-NLS-1$
	}

	public void smartIndentAfterNewLine(IDocument d, DocumentCommand c)
			throws BadLocationException {
		IRegion line = d.getLineInformationOfOffset(c.offset);
		int lineEnd = line.getOffset() + line.getLength();
		RubyHeuristicScanner scanner = new RubyHeuristicScanner(d);

		// eat pending whitespace
		int nonWsPos = scanner.findNonWhitespaceForwardInAnyPartition(c.offset,
				lineEnd);
		if (nonWsPos != RubyHeuristicScanner.NOT_FOUND) {
			c.length = nonWsPos - c.offset;
		}

		// if pending statement is end, else etc. then indent it to block
		// beginning
		if (nextIsIdentToBlockToken(scanner, c.offset, lineEnd)) {
			c.text += getBlockIndent(d, c.offset, scanner);
			return;
		}

		// else
		String indent = getPreviousLineIndent(d, c.offset, scanner);
		c.text += indent;

		if (previousIsBlockBeginning(d, scanner, c.offset)) {
			// if this line was beginning of the block
			c.text += fPreferences.getIndent();

			// Auto close blocks
			if (fCloseBlocks
					&& scanner.isBlockBeginning(line.getOffset(), lineEnd)
					&& !isBlockClosed(d, c.offset)) {
				closeBlock(d, c, indent, d.get(c.offset, lineEnd - c.offset),
						scanner);
			}
		} else if (previousIsFirstContinuation(d, scanner, c.offset, line
				.getOffset())) {
			// or if this line was the first line ending with one of
			// continuation symbols
			c.text += fPreferences.getIndent();

		} else if (hasUnclosedParen(scanner, c.offset, line.getOffset())) {
			// or if this line contains unclosed paren
			c.text += fPreferences.getIndent();
		}
	}

	private boolean hasUnclosedParen(RubyHeuristicScanner scanner, int offset,
			int bound) {
		int pos = scanner.findOpeningPeer(offset, bound, '(', ')');
		return pos != RubyHeuristicScanner.NOT_FOUND;
	}

	private boolean previousIsFirstContinuation(IDocument d,
			RubyHeuristicScanner scanner, int offset, int bound)
			throws BadLocationException {

		IRegion previousLine = null;
		int line = d.getLineOfOffset(offset);
		if (line > 0) {
			previousLine = d.getLineInformation(line - 1);
		}

		return previousIsContinuation(scanner, offset, bound)
				&& (previousLine == null || !previousIsContinuation(scanner,
						previousLine.getOffset() + previousLine.getLength(),
						previousLine.getOffset()));

	}

	private boolean previousIsContinuation(RubyHeuristicScanner scanner,
			int offset, int bound) {
		int token = scanner.previousToken(offset, bound);
		return Arrays.binarySearch(CONTINUATION_TOKENS, token) >= 0;
	}

	private boolean previousIsBlockBeginning(IDocument d,
			RubyHeuristicScanner scanner, int offset)
			throws BadLocationException {
		int previousLineOffset = scanner.findPrecedingNotEmptyLine(offset);
		IRegion previousLine = d.getLineInformationOfOffset(previousLineOffset);
		int previousLineEnd = Math.min(previousLine.getOffset()
				+ previousLine.getLength(), offset);

		boolean previousIsBlockBeginning = scanner.isBlockBeginning(
				previousLine.getOffset(), previousLineEnd)
				|| scanner.isBlockMiddle(previousLine.getOffset(),
						previousLineEnd);
		return previousIsBlockBeginning;
	}

	private boolean nextIsIdentToBlockToken(RubyHeuristicScanner scanner,
			int offset, int bound) {
		int token = scanner.nextToken(offset, bound);
		return Arrays.binarySearch(INDENT_TO_BLOCK_TOKENS, token) >= 0;
	}

	public void smartPaste(IDocument d, DocumentCommand c)
			throws BadLocationException {
		// fix first line whitespace
		IRegion info = d.getLineInformationOfOffset(c.offset);
		String line = d.get(info.getOffset(), c.offset - info.getOffset());
		int startFixFrom = 1;
		if (line.trim().length() == 0) {
			c.length += line.length();
			c.offset -= line.length();
			startFixFrom = 0;
		}

		RubyHeuristicScanner scanner = new RubyHeuristicScanner(d);
		String indent = getLineIndent(d, c.offset, scanner);
		String delimiter = TextUtilities.getDefaultLineDelimiter(d);
		boolean addLastDelimiter = c.text.endsWith(delimiter);
		String[] lines = c.text.split(delimiter);
		if (lines.length > startFixFrom) {
			String currentIndent = ""; //$NON-NLS-1$
			for (int i = startFixFrom; i < lines.length; i++) {
				if (lines[i].trim().length() != 0) {
					currentIndent = AutoEditUtils.getLineIndent(lines[i]);
					break;
				}
			}

			int shift = computeVisualLength(indent)
					- computeVisualLength(currentIndent);
			StringBuffer result = new StringBuffer();
			for (int i = 0; i < startFixFrom; i++) {
				result.append(lines[i]).append(delimiter);
			}
			for (int i = startFixFrom; i < lines.length - 1; i++) {
				result.append(shiftIdentation(lines[i], shift)).append(
						delimiter);
			}
			result.append(shiftIdentation(lines[lines.length - 1], shift));
			if (addLastDelimiter) {
				result.append(delimiter);
			}

			c.text = result.toString();
		}
	}

	private String shiftIdentation(String line, int shift) {
		if (shift > 0) {
			return fPreferences.getIndentByVirtualSize(shift) + line;
		} else {
			int pos = 0;
			while (shift < 0 && pos < line.length()
					&& Character.isWhitespace(line.charAt(pos))) {
				shift += computeVisualLength(line.substring(pos, pos + 1));
				pos++;
			}
			return line.substring(pos);
		}
	}

	/**
	 * Computes the length of a <code>CharacterSequence</code>, counting a tab
	 * character as the size until the next tab stop and every other character
	 * as one.
	 * 
	 * @param indent
	 *            the string to measure
	 * @return the visual length in characters
	 */
	private int computeVisualLength(CharSequence indent) {
		final int tabSize = fPreferences.getTabSize();
		int length = 0;
		for (int i = 0; i < indent.length(); i++) {
			char ch = indent.charAt(i);
			switch (ch) {
			case '\t':
				if (tabSize > 0) {
					int reminder = length % tabSize;
					length += tabSize - reminder;
				}
				break;
			case ' ':
				length++;
				break;
			}
		}
		return length;
	}

	/**
	 * Computes the indentation at <code>offset</code>.
	 * 
	 * @param scanner
	 * 
	 * @param offset
	 *            the offset in the document
	 * @return a String which reflects the correct indentation for the line in
	 *         which offset resides, or <code>null</code> if it cannot be
	 *         determined
	 * @throws BadLocationException
	 */
	private String getPreviousLineIndent(IDocument d, int offset,
			RubyHeuristicScanner scanner) throws BadLocationException {
		StringBuffer result = new StringBuffer();

		if (offset < 0 || d.getLength() == 0)
			return result.toString();

		// find start of line
		int start = scanner.findPrecedingNotEmptyLine(offset);
		IRegion info = d.getLineInformationOfOffset(start);
		int end = scanner.findNonWhitespaceForwardInAnyPartition(start, start
				+ info.getLength());

		if (end > start) {
			// append to input
			result.append(d.get(start, end - start));
		}
		return result.toString();
	}

	private boolean isBlockClosed(IDocument document, int offset)
			throws BadLocationException {
		// TODO: Remove this comment when Ruby parser become able to report
		// unclosed blocks
		//
		// RubyHeuristicScanner scanner = new RubyHeuristicScanner(document);
		// IRegion sourceRange = scanner.findSurroundingBlock(offset);
		// if (sourceRange != null) {
		// String source = document.get(sourceRange.getOffset(), sourceRange
		// .getLength());
		// char[] buffer = source.toCharArray();
		//
		// SyntaxErrorListener listener = new SyntaxErrorListener();
		// ISourceParser parser;
		// try {
		// parser = DLTKLanguageManager
		// .getSourceParser(RubyNature.NATURE_ID);
		// parser.parse(null, buffer, listener);
		// if (listener.errorOccured())
		// return false;
		// } catch (CoreException e) {
		// DLTKUIPlugin.log(e);
		// }
		// }
		return getBlockBalance(document, offset) <= 0;
	}

	/**
	 * Returns the block balance, i.e. zero if the blocks are balanced at
	 * <code>offset</code>, a negative number if there are more closing than
	 * opening braces, and a positive number if there are more opening than
	 * closing braces.
	 * 
	 * @param document
	 * @param offset
	 * @param partitioning
	 * @return the block balance
	 */
	private static int getBlockBalance(IDocument document, int offset) {
		if (offset < 1)
			return -1;
		if (offset >= document.getLength())
			return 1;

		int begin = offset;
		int end = offset - 1;

		RubyHeuristicScanner scanner = new RubyHeuristicScanner(document);

		while (true) {
			begin = scanner.findBlockBeginningOffset(begin);
			end = scanner.findBlockEndingOffset(end);
			if (begin == RubyHeuristicScanner.NOT_FOUND
					&& end == RubyHeuristicScanner.NOT_FOUND)
				return 0;
			if (begin == RubyHeuristicScanner.NOT_FOUND)
				return -1;
			if (end == RubyHeuristicScanner.NOT_FOUND)
				return 1;
		}
	}

	// TODO: Remove this comment when Ruby parser become able to report
	// unclosed blocks
	//
	// private static class SyntaxErrorListener implements IProblemReporter {
	// private boolean fError = false;
	//
	// public void clearMarkers() {
	// }
	//
	// public IMarker reportProblem(IProblem problem) throws CoreException {
	// int id = problem.getID();
	// if ((id & IProblem.Syntax) != 0 || id == IProblem.Unclassified) {
	// fError = true;
	// }
	// return null;
	// }
	//
	// public boolean errorOccured() {
	// return fError;
	// }
	// }
}
