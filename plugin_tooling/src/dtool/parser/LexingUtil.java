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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

public class LexingUtil {
	
	/** Find the token at given offset (end inclusive) of given parseResult.
	 * If offset is the boundary between two tokens, preference is given to non-subchannel tokens.
	 * Otherwise, if that is still ambiguous, return the first token.  
	 */
	public static IToken findTokenAtOffset(final int offset, DeeParserResult parseResult) {
		assertTrue(offset <= parseResult.source.length());
		
		for (LexElement lexToken : parseResult.tokenList) {
			assertTrue(lexToken.getFullRangeStartPos() <= offset);
			
			if(lexToken.getEndPos() < offset) {
				continue;
			}
			
			if(lexToken.getStartPos() <= offset) {
				return lexToken;
			} else {
				// Search in comments, token is in the subchannel range [FullRangeStartPos .. StartPos]
				int searchOffset = lexToken.getFullRangeStartPos();
				return LexingUtil.findTokenAtOffset(offset, parseResult.source, searchOffset);
			}
		}
		throw assertFail();
	}
	
	public static Token findTokenAtOffset(final int offset, String source, int startPos) {
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
	
	/* ----------------- D Identifiers: ----------------- */
	// Some stuff here breaks on UTF32 supplementary characters (we don't care much)
	
	public static boolean isValidDIdentifier(String text) {
		if(!LexingUtil.isValidDAlphaNumeric(text))
			return false;
		
		// Check for keywords
		DeeTokens keywordToken = DeeLexerKeywordHelper.getKeywordToken(text);
		if(keywordToken != null) 
			return false;
		
		return true;
	}
	
	public static boolean isValidDAlphaNumeric(String text) {
		if(text.length() == 0) 
			return false;
		
		if(!(Character.isLetter(text.charAt(0)) || text.charAt(0) == '_'))
			return false;
		
		int pos = 0;
		int length = text.length();
		for(pos = 1; pos < length; ++pos){
			if(!Character.isLetterOrDigit(text.charAt(pos)) && !(text.charAt(pos) == '_'))
				return false;
		}
		
		return true;
	}
	
}