/*******************************************************************************
 * Copyright (c) 2012, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.parser;

public enum DeeTokens {
	
	EOF,
	ERROR,
	
	EOL,
	WHITESPACE,
	SCRIPT_LINE_INTRO,
	
	COMMENT_MULTI,
	COMMENT_NESTED,
	COMMENT_LINE,
	
	IDENTIFIER,
	
	STRING_WYSIWYG,
	STRING_DQ,
	STRING_HEX,
	STRING_DELIM,
	STRING_TOKENS,
	
	OPEN_PARENS,
	CLOSE_PARENS,
	OPEN_BRACE,
	CLOSE_BRACE,
	OPEN_BRACKET,
	CLOSE_BRACKET,
	
	CHAR_LITERAL,
	
	QUESTION,
	COMMA,
	SEMICOLON,
	COLON,
	DOLLAR,
	AT,
	
	DOT, DOUBLE_DOT, TRIPLE_DOT,
	
	MINUS,MINUS_ASSIGN,DECREMENT, 
	PLUS,PLUS_ASSIGN,INCREMENT,	
	DIV,DIV_ASSIGN, 
	STAR, MULT_ASSIGN, 
	MOD, MOD_ASSIGN, 
	
	AND, AND_ASSIGN, LOGICAL_AND, OR, OR_ASSIGN, LOGICAL_OR, XOR, XOR_ASSIGN,
	ASSIGN, EQUALS,
	CONCAT, CONCAT_ASSIGN,
	
	LAMBDA,
	
	NOT, NOT_EQUAL,
	LESS_THAN, LESS_EQUAL, GREATER_THAN, GREATER_EQUAL,
	
	LEFT_SHIFT, LEFT_SHIFT_ASSIGN, RIGHT_SHIFT, RIGHT_SHIFT_ASSIGN, TRIPLE_RSHIFT, TRIPLE_RSHIFT_ASSIGN,
	LESS_GREATER, LESS_GREATER_EQUAL,
	UNORDERED_E, UNORDERED, UNORDERED_GE, UNORDERED_G, UNORDERED_LE, UNORDERED_L,
	
	INTEGER,
	
	;
	
}