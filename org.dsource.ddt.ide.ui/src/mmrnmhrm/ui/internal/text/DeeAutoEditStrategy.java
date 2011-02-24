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


import org.dsource.ddt.lang.text.BlockHeuristicsScannner;
import org.dsource.ddt.lang.text.LangAutoEditStrategy;
import org.dsource.ddt.lang.text.BlockHeuristicsScannner.BlockTokenRule;
import org.eclipse.dltk.ruby.internal.ui.text.RubyAutoEditStrategy;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;

public class DeeAutoEditStrategy extends LangAutoEditStrategy {
	
	private final RubyAutoEditStrategy rubyAutoEditStrategy;
	
	public DeeAutoEditStrategy(String partitioning, IPreferenceStore store) {
		super(store);
		this.rubyAutoEditStrategy = new RubyAutoEditStrategy(partitioning, store);
	}
	
//	@Override
//	public void customizeDocumentCommand(IDocument d, DocumentCommand c) {
//		rubyAutoEditStrategy.customizeDocumentCommand(d, c);
//	}
	
	@Override
	protected BlockHeuristicsScannner createBlockHeuristicsScanner(IDocument doc) {
		BlockHeuristicsScannner scanner = new BlockHeuristicsScannner(doc, 
				new BlockTokenRule('{', '}'),
				new BlockTokenRule('(', ')')
		);
		return scanner;
	}

	
	@Override
	protected void smartIndentOnKeypress(IDocument doc, DocumentCommand cmd) throws BadLocationException {
		rubyAutoEditStrategy.smartIndentOnKeypress(doc, cmd);
	}
	
	@Override
	protected void smartPaste(IDocument doc, DocumentCommand cmd) throws BadLocationException {
		rubyAutoEditStrategy.smartPaste(doc, cmd);
	}
	
}
