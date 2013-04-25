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
public class LexElement extends BaseLexElement {
	
	public final Token token;
	
	public LexElement(Token[] precedingSubChannelTokens, Token token) {
		super(precedingSubChannelTokens);
		this.token = assertNotNull_(token);
		assertTrue(precedingSubChannelTokens == null || precedingSubChannelTokens.length > 0);
	}
	
	@Override
	public final boolean isMissingElement() {
		return false;
	}
	
	@Override
	public final Token getToken(boolean failOnMissing) {
		return token;
	}
	
	@Override
	public final String getSourceValue() {
		return token.getSourceValue();
	}
	
	@Override
	public final int getStartPos() {
		return token.getStartPos();
	}
	
	@Override
	public final int getEndPos() {
		return token.getEndPos();
	}
	
	@Override
	public final SourceRange getSourceRange() {
		return token.getSourceRange();
	}
	
	public final boolean isEOF() {
		return token.type == DeeTokens.EOF;
	}
	
	@Override
	public ParserError getError() {
		return null;
	}
	
	@Override
	public String toString() {
		return super.toString() + token.toString();
	}
	
	public final static class MissingLexElement extends BaseLexElement {
		
		public final int startPos;
		public ParserError error;
		
		public MissingLexElement(Token[] ignoredPrecedingTokens, int lookAheadStart) {
			super(ignoredPrecedingTokens);
			this.startPos = lookAheadStart;
		}
		
		@Override
		public final boolean isMissingElement() {
			return true;
		}
		
		@Override
		public final Token getToken(boolean failOnMissing) {
			assertTrue(!failOnMissing);
			return null;
		}
		
		@Override
		public final String getSourceValue() {
			return "";
		}
		
		@Override
		public final int getStartPos() {
			return startPos;
		}
		
		@Override
		public final int getEndPos() {
			return startPos;
		}
		
		@Override
		public final SourceRange getSourceRange() {
			return SourceRange.srStartToEnd(startPos, startPos);
		}
		
		@Override
		public ParserError getError() {
			return error;
		}
		
		@Override
		public String toString() {
			return super.toString() + "◙";
		}
		
	}
	
}