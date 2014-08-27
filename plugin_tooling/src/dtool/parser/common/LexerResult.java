/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.parser.common;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.misc.NumberUtil.isInRange;

import java.util.Collections;
import java.util.List;

import dtool.parser.DeeLexer;
import dtool.parser.DeeTokens;
import dtool.parser.IToken;
import dtool.parser.LexElement;
import dtool.parser.Token;


public class LexerResult {
	
	public final String source;
	public final List<LexElement> tokenList;
	
	public LexerResult(String source, List<LexElement> tokenList) {
		this.source = source;
		this.tokenList = Collections.unmodifiableList(tokenList);
		
		assertTrue(tokenList.size() > 0);
		assertTrue(tokenList.get(tokenList.size()-1).isEOF());
	}
	
	/** Find the token at given offset (end inclusive) of given parseResult.
	 * If offset is the boundary between two tokens, preference is given to non-subchannel tokens.
	 * Otherwise, if that is still ambiguous, return the first token.  
	 */
	public IToken findTokenAtOffset(final int offset) {
		assertTrue(offset <= source.length());
		
		for (LexElement lexElement : tokenList) {
			assertTrue(lexElement.getFullRangeStartPos() <= offset);
			
			if(offset >= lexElement.getEndPos()) {
				if(lexElement.isEOF()) {
					return lexElement;
				}
				continue; // go to next token
			}
			assertTrue(isInRange(lexElement.getFullRangeStartPos(), offset, lexElement.getEndPos()));
			
			// We found the LexElement range where the offset is at, but we still need to figure out
			// if the offset is in a subchannel token or in the main token.
			
			if(offset >= lexElement.getStartPos()) {
				return lexElement;
			} else {
				// Search in comments, token is in the subchannel range [FullRangeStartPos .. StartPos]
				int searchStartPos = lexElement.getFullRangeStartPos();
				// We don't store subchannel tokens, so we have to reparse to find them:
				return findFirstTokenAtOffset(source, searchStartPos, offset);
			}
		}
		throw assertFail();
	}
	
	protected static Token findFirstTokenAtOffset(String source, int startPos, final int offset) {
		assertTrue(startPos <= offset);
		DeeLexer lexer = new DeeLexer(source);
		lexer.reset(startPos);
		while(true) {
			Token token = lexer.next();
			if(offset <= token.getEndPos())
				return token;
			assertTrue(token.type != DeeTokens.EOF);
		}
	}
	
}