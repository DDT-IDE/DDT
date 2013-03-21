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

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.SourceRange;

/**
 * Parser lexing element with a main token and optional ignored channel tokens preceding it.
 */
public class LexElement {
	
	public final Token[] ignoredPrecedingTokens;
	public final Token token;
	
	public LexElement(Token[] ignoredPrecedingTokens, Token token) {
		this.ignoredPrecedingTokens = ignoredPrecedingTokens;
		this.token = assertNotNull_(token);
	}
	
	public LexElement(Token[] ignoredPrecedingTokens, DeeTokens expectedToken, int lookAheadStart) {
		this.ignoredPrecedingTokens = ignoredPrecedingTokens;
		this.token = new MissingToken(expectedToken, lookAheadStart);
	}
	
	public boolean isMissingElement() {
		return token instanceof LexElement.MissingToken;
	}
	
	public SourceRange getSourceRange() {
		return token.getSourceRange();
	}
	
	public final int getStartPos() {
		return token.getStartPos();
	}
	
	public final int getEndPos() {
		return token.getEndPos();
	}
	
	public final DeeTokens getType() {
		return token.type;
	}
	
	public int getFullRangeStartPos() {
		assertTrue(isMissingElement() == false);
		if(ignoredPrecedingTokens != null && ignoredPrecedingTokens.length > 0) {
			return ignoredPrecedingTokens[0].getStartPos();
		}
		return token.getStartPos();
	}
	
	protected static class MissingToken extends Token {
		public MissingToken(DeeTokens tokenType, int startPos) {
			super(tokenType, "", startPos);
		}
		
		@Override
		public int getLength() {
			return 0;
		}
		
		@Override
		public int getEndPos() {
			return startPos;
		}
	}
	
}