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
import static melnorme.utilbox.misc.StringUtil.collToString;
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
		assertTrue(ignoredPrecedingTokens == null || ignoredPrecedingTokens.length > 0);
	}
	
	public final boolean isMissingElement() {
		return token instanceof LexElement.MissingToken;
	}
	
	public final DeeTokens getType() {
		assertTrue(!isMissingElement());
		return token.type;
	}
	
	public final String getSourceValue() {
		return token.getSourceValue();
	}
	
	public final int getStartPos() {
		return token.getStartPos();
	}
	
	public final int getEndPos() {
		return token.getEndPos();
	}
	
	public final SourceRange getSourceRange() {
		return token.getSourceRange();
	}
	
	public final int getFullRangeStartPos() {
		if(ignoredPrecedingTokens != null && ignoredPrecedingTokens.length > 0) {
			return ignoredPrecedingTokens[0].getStartPos();
		}
		return token.getStartPos();
	}
	
	
	public static class MissingLexElement extends LexElement {
		
		public MissingLexElement(Token[] ignoredPrecedingTokens, DeeTokens expectedToken, int lookAheadStart) {
			super(ignoredPrecedingTokens, new MissingToken(expectedToken, lookAheadStart));
		}
		
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
		
		@Override
		public String toString() {
			return "◙";
		}
	}
	
	@Override
	public String toString() {
		String prefix = ignoredPrecedingTokens != null ? "【"+collToString(ignoredPrecedingTokens, "●")+"】" : "";
		return prefix + token;
	}
	
}