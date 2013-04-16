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

import java.util.ArrayList;

import dtool.util.NewUtils;

/**
 * Produces {@link LexElement}'s from a lexer to construct a {@link LexElementSource}.
 * The additional abstraction this classes creates allows for customization of {@link LexElement}'s, plus
 * potential performance optimizations (TODO) 
 */
public class LexElementProducer {
	
	public static LexElementSource createFromLexer(AbstractLexer lexer) {
		return new LexElementSource(new LexElementProducer().produceLexTokens(lexer));
	}
	
	protected ArrayList<LexElement> produceLexTokens(AbstractLexer lexer) {
		ArrayList<LexElement> lexElementList = new ArrayList<>();
		
		while(true) {
			LexElement lexElement = produceLexElement(lexer);
			lexElementList.add(lexElement);
			if(lexElement.isEOF()) {
				break;
			}
		}
		return lexElementList;
	}
	
	public LexElement produceLexElement(AbstractLexer lexer) {
		ArrayList<Token> ignoredTokens = null;
		while(true) {
			Token token = lexer.next();
			tokenCreated(token);
			
			if(token.isSubChannelToken()) {
				if(ignoredTokens == null)
					ignoredTokens = new ArrayList<Token>(1);
				ignoredTokens.add(token);
				continue;
			}
			return new LexElement(NewUtils.toArray(ignoredTokens, Token.class), token);
		}
	}
	
	@SuppressWarnings("unused")
	protected void tokenCreated( Token token) {
		// Default implementation
	}
	
}