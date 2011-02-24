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



import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

public class LangHeuristicScanner implements ILangHeuristicSymbols {
	
	protected final IDocument document;
	
	protected final String partitioning;
	
	/** the current position. */
	protected int pos;
	/** the limit position, where the scanner will not scan beyond. */
	protected int limitPos;
	/** the last read token. */
	protected int token;
	
	
	protected LangHeuristicScanner(IDocument document, String partitioning) {
		Assert.isLegal(document != null);
		Assert.isLegal(partitioning != null);
		this.document = document;
		this.partitioning = partitioning;
	}
	
	public IDocument getDocument() {
		return document;
	}
	
	public int getPosition() {
		return pos;
	}
	
	protected void setPosition(int pos) {
		this.pos = pos;
	}
	
	protected final char getSourceChar(int pos) throws BadLocationException {
		return document.getChar(pos);
	}
	
	protected final int getSourceLength() {
		return document.getLength();
	}
	
}
