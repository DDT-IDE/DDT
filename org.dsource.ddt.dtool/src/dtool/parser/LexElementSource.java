/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.AbstractList;

import dtool.parser.LexElement.MissingLexElement;

/**
 * A list-like indexed source of {@link LexElement}'s.
 * Supports backtracking by means of saving and restoring state.
 */
public class LexElementSource {
	
	public static final LexElement START_TOKEN = new LexElement(null, new Token(DeeTokens.EOF, "", 0));
	
	protected final AbstractList<LexElement> lexElementList; // Immutable
	protected LexElement lastLexElement = START_TOKEN;
	protected int lexElementPosition = 0;
	protected int sourcePosition = 0;
	
	protected LexElementSource(AbstractList<LexElement> lexElementList) {
		this.lexElementList = lexElementList;
	}
	
	public final LexElement lastLexElement() {
		return lastLexElement;
	}
	
	public final int getLexElementPosition() {
		return lexElementPosition;
	}
	
	public final int getSourcePosition() {
		return sourcePosition;
	}
	
	public final void setSourcePosition(int sourcePosition) {
		this.sourcePosition = sourcePosition;
	}
	
	public LexElement lookAheadElement(int laIndex) {
		assertTrue(laIndex >= 0);
		int listIndex = Math.min(lexElementPosition + laIndex, lexElementList.size() - 1);
		return lexElementList.get(listIndex);
	}
	
	public LexElementSource saveState() {
		LexElementSource savedState = new LexElementSource(lexElementList);
		savedState.lastLexElement = lastLexElement;
		savedState.lexElementPosition = lexElementPosition;
		savedState.sourcePosition = sourcePosition;
		return savedState;
	}
	
	public void resetState(LexElementSource savedState) {
		assertTrue(savedState.lexElementList == lexElementList);
		lastLexElement = savedState.lastLexElement;
		lexElementPosition = savedState.lexElementPosition;
		sourcePosition = savedState.sourcePosition;
	}
	
	public final LexElement consumeInput() {
		lastLexElement = lookAheadElement(0);
		lexElementPosition++;
		sourcePosition = lastLexElement.getEndPos();
		
		return lastLexElement;
	}
	
	
	public final MissingLexElement consumeSubChannelTokens() {
		LexElement la = lookAheadElement(0);
		
		// Missing element will consume whitetokens ahead
		int lookAheadStart = la.getStartPos();
		MissingLexElement missingLexElement = new MissingLexElement(la.precedingSubChannelTokens, lookAheadStart);
		sourcePosition = missingLexElement.getEndPos(); // advance LexSourcePosition
		
		return missingLexElement;
	}
	
}