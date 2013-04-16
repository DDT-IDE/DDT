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

import java.util.ArrayList;

import melnorme.utilbox.misc2.ArrayListDeque;
import dtool.parser.LexElement.MissingLexElement;
import dtool.util.NewUtils;

/**
 * A source of {@link LexElement}'s.
 * Maintains a lookhead queue of {@link LexElement}, that can be manipulated.
 */
public class LexerElementSource extends CommonLexElementSource {
	
	protected final AbstractLexer lexer;
	
	protected final ArrayListDeque<LexElement> lookAheadQueue = new ArrayListDeque<LexElement>();
	protected LexElement lastLexElement = new LexElement(null, new Token(DeeTokens.EOF, "", 0));
	protected int lexPosition = 0;
	
	public LexerElementSource(String source) {
		this.lexer = new DeeLexer(source);
	}
	
	public LexerElementSource(AbstractLexer deeLexer) {
		this.lexer = deeLexer;
	}
	
	@Override
	public String getSource() {
		return lexer.getSource();
	}
	
	@Override
	public LexElement lookAheadElement(int laIndex) {
		assertTrue(laIndex >= 0);
		
		while(lookAheadQueue.size() <= laIndex) {
			LexElement newLexElement = produceLexElement(lexer);
			lookAheadQueue.add(newLexElement);
		}
		
		return lookAheadQueue.get(laIndex);
	}
	
	static{ assertTrue(DeeTokens.EOF.isParserIgnored == false); }
	
	public static LexElement produceLexElement(AbstractLexer lexer) {
		ArrayList<Token> ignoredTokens = null;
		while(true) {
			Token token = lexer.next();
			
			DeeTokens tokenType = token.type;
			
			if(tokenType.isParserIgnored) {
				if(ignoredTokens == null)
					ignoredTokens = new ArrayList<Token>(1);
				ignoredTokens.add(token);
				continue;
			}
			return new LexElement(NewUtils.toArray(ignoredTokens, Token.class), token);
		}
	}
	
	@Override
	protected final LexElement consumeInput() {
		LexElement laElem = lookAheadElement(0); // Ensure there is at least one element in queue
		
		lastLexElement = laElem;
		lookAheadQueue.removeFirst();
		lexPosition = lastLexElement.getEndPos();
		lexElementConsumed(lastLexElement);
		return lastLexElement;
	}
	
	@Override
	public int getLexPosition() {
		return lexPosition;
	}
	
	@Override
	public final MissingLexElement consumeIgnoreTokens() {
		LexElement la = lookAheadElement();
		
		// Missing element will consume whitetokens ahead
		int lookAheadStart = la.getStartPos();
		MissingLexElement missingLexElement = new MissingLexElement(la.ignoredPrecedingTokens, lookAheadStart);
		lexPosition = missingLexElement.getEndPos();
		lookAheadQueue.set(0, new LexElement(null, la.token));
		
		lexElementConsumed(missingLexElement);
		return missingLexElement;
	}
	
	protected void lexElementConsumed(@SuppressWarnings("unused") BaseLexElement lastLexElement) {
		// Default implementation
	}
	
	@Override
	protected LexElement lastLexElement() {
		return lastLexElement;
	}
	
}