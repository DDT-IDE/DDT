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
package mmrnmhrm.ui.internal.text;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import mmrnmhrm.ui.internal.text.BlockHeuristicsScannner.BlockBalanceResult;
import mmrnmhrm.ui.internal.text.BlockHeuristicsScannner.BlockTokenRule;

import org.eclipse.dltk.ruby.internal.ui.text.RubyPreferenceInterpreter;
import org.eclipse.dltk.ui.text.util.AutoEditUtils;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;

public class LangAutoEditStrategy extends DefaultIndentLineAutoEditStrategy {
	
	protected LangAutoEditsPreferencesAdapter fPreferences;
	
	protected boolean fIsSmartMode;
	protected boolean fCloseBlocks = true;
	
	public LangAutoEditStrategy(IPreferenceStore store) {
		fPreferences = new RubyPreferenceInterpreter(store);
	}
	
	protected boolean isSmartMode() {
		return fIsSmartMode;
	}
	
	protected void clearCachedValues() {
		fCloseBlocks = fPreferences.closeBlocks();
		fIsSmartMode = fPreferences.isSmartMode();
	}
	
	@Override
	public void customizeDocumentCommand(IDocument d, DocumentCommand c) {
		if (c.doit == false)
			return;
		
		clearCachedValues();
		if (!isSmartMode()) {
			super.customizeDocumentCommand(d, c); 
			return;
		}
		
		try {
			if (AutoEditUtils.isNewLineInsertionCommand(d, c))
				smartIndentAfterNewLine(d, c);
			else if (AutoEditUtils.isSingleCharactedInsertionOrReplaceCommand(c))
				smartIndentOnKeypress(d, c);
			else if (c.text.length() > 1 && fPreferences.isSmartPaste())
				smartPaste(d, c); // no smart backspace for paste
			else
				super.customizeDocumentCommand(d, c);
		} catch (BadLocationException e) {
			//DLTKUIPlugin.log(e);
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	protected BlockHeuristicsScannner createBlockHeuristicsScanner(IDocument doc) {
		// Default implementation
		return new BlockHeuristicsScannner(doc, new BlockTokenRule('{', '}'));
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
		
		if(blockInfo.unbalancedOpens == 0 && blockInfo.unbalancedCloses < 0) {
			int blockStartOffset = bhscanner.findBlockStart(blockInfo.rightmostUnbalancedBlockCloseOffset);
			int blockStartLine = doc.getLineOfOffset(blockStartOffset);
			// BUG here
			assertTrue(blockStartLine < doc.getLineOfOffset(lineRegion.getOffset()));
			cmd.text += AutoEditUtils.getLineIndent(doc, blockStartLine);
			return;
		}
		
		// The indent string to be added to the new line
		String indentStr = getLineIndent(doc, lineRegion);
		if(blockInfo.unbalancedOpens == 0 && blockInfo.unbalancedCloses == 0) {
			cmd.text += indentStr; 
			return; // finished
		}
		
		if(blockInfo.unbalancedOpens > 0) {
			cmd.text += addIndent(indentStr, blockInfo.unbalancedOpens);
			
			boolean hasPendingText = postWsEndPos != lineEnd;
			if (fCloseBlocks && !hasPendingText){
				if(!bhscanner.isBlockClosed(blockInfo.rightmostUnbalancedBlockOpenOffset)) {
					//close block
					cmd.caretOffset = cmd.offset + cmd.text.length();
					cmd.shiftsCaret = false; // BUG here
					String delimiter = TextUtilities.getDefaultLineDelimiter(doc);
					char openChar = doc.getChar(blockInfo.rightmostUnbalancedBlockOpenOffset);
					char closeChar = bhscanner.getClosingPeer(openChar); 
					cmd.text += delimiter + addIndent(indentStr, blockInfo.unbalancedOpens - 1) + closeChar;
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