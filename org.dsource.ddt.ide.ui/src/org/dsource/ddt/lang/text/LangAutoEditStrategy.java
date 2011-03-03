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
		
		// eat whitespace after NL
		int postWsEndPos = AutoEditUtils.findEndOfWhiteSpace(doc, cmd.offset, lineEnd); 
		if (postWsEndPos != lineEnd) {
			cmd.length = postWsEndPos - cmd.offset; 
		}
		
		BlockHeuristicsScannner bhscanner = createBlockHeuristicsScanner(doc);
		
		// Find block delta of preceding text (line start to edit cursor)
		BlockBalanceResult blockInfo = bhscanner.calculateBlockBalances(lineRegion.getOffset(), cmd.offset);  
		
		if(blockInfo.unbalancedOpens == 0 && blockInfo.unbalancedCloses > 0) {
			int blockStartOffset = bhscanner.findBlockStart(blockInfo.rightmostUnbalancedBlockCloseOffset);
			int blockStartLine = doc.getLineOfOffset(blockStartOffset);
			
			assertTrue(blockStartLine < doc.getLineOfOffset(lineRegion.getOffset()));
			String startLineIndent = AutoEditUtils.getLineIndent(doc, blockStartLine);
			
			// Now calculate the balance for the block start line, before the block start
			int lineOffset = doc.getLineOffset(blockStartLine);
			BlockBalanceResult blockStartInfo = bhscanner.calculateBlockBalances(lineOffset, blockStartOffset);
			
			// Add the indent of the start line, plus the unbalanced opens there
			cmd.text += addIndent(startLineIndent, blockStartInfo.unbalancedOpens);
			return;
		}
		
		// The indent string to be added to the new line
		String lineIndent = getLineIndent(doc, lineRegion);
		if(blockInfo.unbalancedOpens == 0 && blockInfo.unbalancedCloses == 0) {
			cmd.text += lineIndent; 
			return; // finished
		}
		
		if(blockInfo.unbalancedOpens > 0) {
			cmd.text += addIndent(lineIndent, blockInfo.unbalancedOpens);
			
			boolean hasPendingTextAfterEdit = postWsEndPos != lineEnd;
			if (fCloseBlocks && !hasPendingTextAfterEdit){
				if(bhscanner.shouldCloseBlock(blockInfo.rightmostUnbalancedBlockOpenOffset)) {
					//close block
					cmd.caretOffset = cmd.offset + cmd.text.length();
					cmd.shiftsCaret = false;
					String delimiter = TextUtilities.getDefaultLineDelimiter(doc);
					char openChar = doc.getChar(blockInfo.rightmostUnbalancedBlockOpenOffset);
					char closeChar = bhscanner.getClosingPeer(openChar); 
					cmd.text += delimiter + addIndent(lineIndent, blockInfo.unbalancedOpens - 1) + closeChar;
				}
			}
			return;
		}
	}
	
	protected static String getLineIndent(IDocument doc, IRegion line) throws BadLocationException {
		int lineOffset = line.getOffset();
		int indentEnd = AutoEditUtils.findEndOfWhiteSpace(doc, lineOffset, lineOffset + line.getLength());
		return doc.get(lineOffset, indentEnd - lineOffset);
	}
	
	private String addIndent(String indentStr, int indentDelta) {
		return indentStr + fPreferences.getIndent(indentDelta);
	}
	
	/* ------------------------------------- */
	
	protected void smartIndentOnKeypress(IDocument doc, DocumentCommand cmd) throws BadLocationException {
		super.customizeDocumentCommand(doc, cmd);
	}
	
	protected void smartPaste(IDocument doc, DocumentCommand cmd) throws BadLocationException {
		super.customizeDocumentCommand(doc, cmd);
	}
}