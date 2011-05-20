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
package mmrnmhrm.ui.editor.text;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.CoreUtil.array;
import melnorme.utilbox.misc.ArrayUtil;
import mmrnmhrm.ui.DeeUIPreferenceConstants;
import mmrnmhrm.ui.text.DeePartitions;

import org.dsource.ddt.lang.text.BlockHeuristicsScannner;
import org.dsource.ddt.lang.text.BlockHeuristicsScannner.BlockTokenRule;
import org.dsource.ddt.lang.text.LangAutoEditStrategy;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITextViewer;

public class DeeAutoEditStrategy extends LangAutoEditStrategy {
	
	private final class AssertNoChangesDocumentListener implements IDocumentListener {
		@Override
		public void documentChanged(DocumentEvent event) {
			assertFail();
		}
		
		@Override
		public void documentAboutToBeChanged(DocumentEvent event) {
		}
	}
	
	protected final String partitioning;
	protected final String contentType;
	protected final IPreferenceStore store;
	protected boolean parenthesesAsBlocks;
	
	public DeeAutoEditStrategy(IPreferenceStore store) {
		this(store, DeePartitions.DEE_CODE, null);
	}
	
	public DeeAutoEditStrategy(IPreferenceStore store, String contentType, ITextViewer viewer) {
		super(store, viewer);
		this.store = store;
		this.contentType = contentType;
		this.partitioning = DeePartitions.DEE_PARTITIONING;
	}
	
	@Override
	public void customizeDocumentCommand(IDocument doc, DocumentCommand cmd) {
		parenthesesAsBlocks = store.getBoolean(DeeUIPreferenceConstants.AE_PARENTHESES_AS_BLOCKS);
		
		IDocumentListener listener = new AssertNoChangesDocumentListener();
		doc.addDocumentListener(listener);
		try {
			super.customizeDocumentCommand(doc, cmd);
		} finally {
			doc.removeDocumentListener(listener);
		}
	}
	
	@Override
	protected BlockHeuristicsScannner createBlockHeuristicsScanner(IDocument doc) {
		BlockTokenRule[] blockTokens = array(new BlockTokenRule('{', '}'));
		if(parenthesesAsBlocks) {
			blockTokens = ArrayUtil.concat(blockTokens, new BlockTokenRule('(', ')'));
		}
		BlockHeuristicsScannner scanner = new BlockHeuristicsScannner(doc, partitioning, contentType, blockTokens);
		return scanner;
	}
	
	@Override
	protected void smartIndentOnKeypress(IDocument doc, DocumentCommand cmd) throws BadLocationException {
//		rubyAutoEditStrategy.smartIndentOnKeypress(doc, cmd);
	}
	
	@Override
	protected void smartPaste(IDocument doc, DocumentCommand cmd) throws BadLocationException {
		//rubyAutoEditStrategy.smartPaste(doc, cmd);
	}
	
}
