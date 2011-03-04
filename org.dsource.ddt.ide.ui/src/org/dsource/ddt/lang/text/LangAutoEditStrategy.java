/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package org.dsource.ddt.lang.text;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;

import org.dsource.ddt.lang.text.BlockHeuristicsScannner.BlockBalanceResult;
import org.dsource.ddt.lang.text.BlockHeuristicsScannner.BlockTokenRule;
import org.eclipse.dltk.ui.text.util.AutoEditUtils;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;

public class LangAutoEditStrategy extends DefaultIndentLineAutoEditStrategy {
	
	protected LangAutoEditsPreferencesAdapter fPreferences;
	
	protected boolean fIsSmartMode;
	protected boolean fCloseBlocks = true;
	
	public LangAutoEditStrategy(IPreferenceStore store) {
		fPreferences = new LangAutoEditsPreferencesAdapter(store);
	}
	
	protected boolean isSmartMode() {
		return fIsSmartMode;
	}
	
	protected void clearCachedValues() {
		fCloseBlocks = fPreferences.closeBlocks();
		fIsSmartMode = fPreferences.isSmartMode();
	}
	
	@Override
	public void customizeDocumentCommand(IDocument doc, DocumentCommand cmd) {
		if (cmd.doit == false)
			return;
		
		clearCachedValues();
		if (!isSmartMode()) {
			super.customizeDocumentCommand(doc, cmd); 
			return;
		}
		
		try {
			if (AutoEditUtils.isNewLineInsertionCommand(doc, cmd))
				smartIndentAfterNewLine(doc, cmd);
			else if(smartDeIndentAfterDelete(doc, cmd))
				return;
			else if (AutoEditUtils.isSingleCharactedInsertionOrReplaceCommand(cmd))
				smartIndentOnKeypress(doc, cmd);
			else if (cmd.text.length() > 1 && fPreferences.isSmartPaste())
				smartPaste(doc, cmd); // no smart backspace for paste
			else
				super.customizeDocumentCommand(doc, cmd);
		} catch (BadLocationException e) {
			//DLTKUIPlugin.log(e);
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	protected BlockHeuristicsScannner createBlockHeuristicsScanner(IDocument doc) {
		// Default implementation
		String partitioning = IDocumentExtension3.DEFAULT_PARTITIONING;
		String contentType = IDocument.DEFAULT_CONTENT_TYPE;
		return new BlockHeuristicsScannner(doc, partitioning, contentType, new BlockTokenRule('{', '}'));
	}
	
	/* ------------------------------------- */
	
	protected void smartIndentAfterNewLine(IDocument doc, DocumentCommand cmd) throws BadLocationException {
		IRegion lineRegion = doc.getLineInformationOfOffset(cmd.offset);
		int lineEnd = lineRegion.getOffset() + lineRegion.getLength();
		
		BlockHeuristicsScannner bhscanner = createBlockHeuristicsScanner(doc);
		// Find block balances of preceding text (line start to edit cursor)
		LineIndentResult nli = determineIndent(doc, lineRegion, cmd.offset, bhscanner);
		cmd.text += nli.newLineIndent;
		BlockBalanceResult blockInfo = nli.blockInfo;
		
		if(blockInfo.unbalancedOpens > 0) {
			int postWsEndPos = AutoEditUtils.findEndOfWhiteSpace(doc, cmd.offset, lineEnd); 
			boolean hasPendingTextAfterEdit = postWsEndPos != lineEnd;
			if (fCloseBlocks && !hasPendingTextAfterEdit){
				if(bhscanner.shouldCloseBlock(blockInfo.rightmostUnbalancedBlockOpenOffset)) {
					//close block
					cmd.caretOffset = cmd.offset + cmd.text.length();
					cmd.shiftsCaret = false;
					String delimiter = TextUtilities.getDefaultLineDelimiter(doc);
					char openChar = doc.getChar(blockInfo.rightmostUnbalancedBlockOpenOffset);
					char closeChar = bhscanner.getClosingPeer(openChar); 
					cmd.text += delimiter + addIndent(nli.lineIndent, blockInfo.unbalancedOpens - 1) + closeChar;
				}
			}
			return;
		}
	}
	
	public static class LineIndentResult {
		String lineIndent;
		String newLineIndent;
		BlockBalanceResult blockInfo;
	}
	
	protected LineIndentResult determineIndent(IDocument doc, final IRegion lineRegion, final int editOffset,
			BlockHeuristicsScannner bhscanner) throws BadLocationException {
		int lineStart = lineRegion.getOffset();
		int lineEnd = lineStart + lineRegion.getLength();
		assertTrue(lineStart <= editOffset && editOffset <= lineEnd);
		LineIndentResult result = new LineIndentResult();
		BlockBalanceResult blockInfo = bhscanner.calculateBlockBalances(lineStart, editOffset);
		result.blockInfo = blockInfo;
		
		if(blockInfo.unbalancedOpens == 0 && blockInfo.unbalancedCloses > 0) {
			int blockStartOffset = bhscanner.findBlockStart(blockInfo.rightmostUnbalancedBlockCloseOffset);
			int blockStartLine = doc.getLineOfOffset(blockStartOffset);
			IRegion blockStartLineInfo = doc.getLineInformationOfOffset(blockStartOffset);
			
			assertTrue(blockStartLine < doc.getLineOfOffset(lineStart));
			String startLineIndent = getLineIndent(doc, blockStartLineInfo);
			
			// Now calculate the balance for the block start line, before the block start
			int lineOffset = blockStartLineInfo.getOffset();
			BlockBalanceResult blockStartInfo = bhscanner.calculateBlockBalances(lineOffset, blockStartOffset);
			
			// Add the indent of the start line, plus the unbalanced opens there
			result.newLineIndent = addIndent(startLineIndent, blockStartInfo.unbalancedOpens); 
			return result;
		}
		
		// The indent string to be added to the new line
		int maxIndentEnd = Math.min(editOffset, lineEnd);
		String lineIndent = getLineIndent(doc, lineStart, maxIndentEnd);
		if(blockInfo.unbalancedOpens == 0 && blockInfo.unbalancedCloses == 0) {
			result.newLineIndent = lineIndent;
			return result; // finished
		}
		
		if(blockInfo.unbalancedOpens > 0) {
			result.newLineIndent = addIndent(lineIndent, blockInfo.unbalancedOpens);
			result.lineIndent = lineIndent; // cache this value to not recalculate
			return result;
		}
		throw assertUnreachable();
	}
	
	protected static String getLineIndent(IDocument doc, IRegion line) throws BadLocationException {
		return getLineIndent(doc, line.getOffset(), line.getOffset() + line.getLength());
	}
	
	protected static String getLineIndent(IDocument doc, int start, int end) throws BadLocationException {
		assertTrue(start <= end);
		int indentEnd = AutoEditUtils.findEndOfWhiteSpace(doc, start, end);
		return doc.get(start, indentEnd - start);
	}
	
	protected String addIndent(String indentStr, int indentDelta) {
		return indentStr + fPreferences.getIndent(indentDelta);
	}
	
	/* ------------------------------------- */
	
	protected boolean smartDeIndentAfterDelete(IDocument doc, DocumentCommand cmd) throws BadLocationException {
		if(!fPreferences.isSmartDeIndent())
			return false;
		
		if(!cmd.text.isEmpty())
			return false;
		
		IRegion lineRegion = doc.getLineInformationOfOffset(cmd.offset);
		int lineEnd = lineRegion.getOffset() + lineRegion.getLength();
		int line = doc.getLineOfOffset(cmd.offset);
		
		
		// Delete at beginning of NL
		if(cmd.offset == lineEnd && lengthMatchesLineDelimiter(cmd.length, doc.getLineDelimiter(line))) {
			int indentLine = line+1;
			if(indentLine < doc.getNumberOfLines()) {
				assertTrue(doc.getLineInformation(indentLine).getOffset() == cmd.offset + cmd.length);
				
				IRegion indentLineRegion = doc.getLineInformation(indentLine);
				int indentEnd = findEndOfWhiteSpace(doc, indentLineRegion);
				String deletableIndentStr = calculateDeletableIndent(doc, indentLine, indentEnd);
				if(equalsDocumentString(deletableIndentStr, doc, indentLineRegion)) {
					cmd.length += deletableIndentStr.length();
					return true;
				}
			}
			return false;
		}
		
		// Backspace at end of indent case
		if(cmd.length == 1 && isIndentWhiteSpace(doc.getChar(cmd.offset)) && line > 0) {
			IRegion indentLineRegion = lineRegion;
			int indentLine = line;
			int indentEnd = findEndOfWhiteSpace(doc, indentLineRegion);
			if(cmd.offset < indentEnd) {
				// potentially true
				
				String deletableIndentStr = calculateDeletableIndent(doc, indentLine, indentEnd);
				if(equalsDocumentString(deletableIndentStr, doc, indentLineRegion)) {
					int acceptedIndentEnd = indentLineRegion.getOffset() + deletableIndentStr.length();
					if(cmd.offset == acceptedIndentEnd-1) {
						int lineDelimLen = doc.getLineDelimiter(line-1).length();
						cmd.offset = indentLineRegion.getOffset() - lineDelimLen;
						cmd.length = lineDelimLen + deletableIndentStr.length();
						return true;
					}
				}
			}
			return false;
		}
		
		return false;
	}
	
	protected static boolean lengthMatchesLineDelimiter(int length, String lineDelimiter) {
		return lineDelimiter != null && length == lineDelimiter.length();
	}
	
	protected boolean isIndentWhiteSpace(char ch) throws BadLocationException {
		return ch == ' ' || ch == '\t';
	}
	
	protected String calculateDeletableIndent(IDocument doc, int indentedLine, int indentEnd)
			throws BadLocationException {
		IRegion indentedLineRegion = doc.getLineInformation(indentedLine);

		String expectedIndentStr = determineExpectedIndent(doc, doc.getLineInformation(indentedLine-1));
		int indentLength = indentEnd - indentedLineRegion.getOffset(); 
		if(indentLength < expectedIndentStr.length()) {
			// cap expected length
			expectedIndentStr = expectedIndentStr.substring(0, indentLength);
		}
		return expectedIndentStr;
	}
	
	protected boolean equalsDocumentString(String expectedIndentStr, IDocument doc, IRegion lineRegion)
			throws BadLocationException {
		int length = Math.min(lineRegion.getLength(), expectedIndentStr.length());
		String lineIndent = doc.get(lineRegion.getOffset(), length);
		return expectedIndentStr.equals(lineIndent);
	}
	
	protected int findEndOfWhiteSpace(IDocument doc, IRegion region) throws BadLocationException {
		return AutoEditUtils.findEndOfWhiteSpace(doc, region.getOffset(), region.getOffset() + region.getLength());
	}
	
	protected String determineExpectedIndent(IDocument doc, IRegion lineRegion)
			throws BadLocationException {
		BlockHeuristicsScannner bhscanner = createBlockHeuristicsScanner(doc);
		int lineEnd = lineRegion.getOffset() + lineRegion.getLength();
		LineIndentResult nli = determineIndent(doc, lineRegion, lineEnd, bhscanner);
		String expectedIndentStr = nli.newLineIndent;
		return expectedIndentStr;
	}
	
	/* ------------------------------------- */
	
	protected void smartIndentOnKeypress(IDocument doc, DocumentCommand cmd) throws BadLocationException {
		super.customizeDocumentCommand(doc, cmd);
	}
	
	protected void smartPaste(IDocument doc, DocumentCommand cmd) throws BadLocationException {
		super.customizeDocumentCommand(doc, cmd);
	}
}