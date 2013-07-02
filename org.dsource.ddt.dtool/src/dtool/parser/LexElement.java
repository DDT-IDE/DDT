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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

/**
 * Parser lexing element with a main token and optional ignored channel tokens preceding it.
 */
public class LexElement extends BaseLexElement {
	
	public final DeeTokens type;
	
	public LexElement(Token[] precedingSubChannelTokens, Token token) {
		super(precedingSubChannelTokens, token.source, token.startPos);
		this.type = assertNotNull(token.type);
		if(type.hasSourceValue()) {
			assertEquals(type.getSourceValue(), source);
		}
	}
	
	@Override
	public final boolean isMissingElement() {
		return false;
	}
	
	public final boolean isEOF() {
		return type == DeeTokens.EOF;
	}
	
	@Override
	public ParserError getError() {
		return null;
	}
	
	@Override
	public String toString() {
		return super.toString() + type +"►"+ source;
	}
	
	public final static class MissingLexElement extends BaseLexElement {
		
		public ParserError error;
		
		public MissingLexElement(Token[] ignoredPrecedingTokens, int lookAheadStart) {
			super(ignoredPrecedingTokens, "", lookAheadStart);
		}
		
		@Override
		public final boolean isMissingElement() {
			return true;
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