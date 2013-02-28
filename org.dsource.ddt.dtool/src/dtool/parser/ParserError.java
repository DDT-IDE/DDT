/*******************************************************************************
 * Copyright (c) 2012, 2013 IBM Corporation and others.
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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import melnorme.utilbox.misc.StringUtil;
import descent.core.compiler.Linkage;
import dtool.ast.ASTNeoNode;
import dtool.ast.SourceRange;

public class ParserError {
	
	public enum ParserErrorTypes {
		
		INVALID_TOKEN_CHARACTERS, // Lexer: invalid characters, cannot form token
		MALFORMED_TOKEN, // Lexer: recovered token has errors
		
		EXPECTED_TOKEN, // expected specific token
		EXPECTED_RULE, // expected valid token for rule
		SYNTAX_ERROR, // unexpected rule in rule start
		
		INVALID_EXTERN_ID, // specific error for extern declaration argument
		EXP_MUST_HAVE_PARENTHESES, // expression must have parentheses to parse
		TYPE_USED_AS_EXP_VALUE, // a built-in type is used as an expression value
		INVALID_QUALIFIER, // A composite built in type is used as qualifier in qualified ref
	}
	
	protected final ParserErrorTypes errorType;
	protected final SourceRange sourceRange;
	protected final String msgErrorSource;
	protected final Object msgData;
	public ASTNeoNode originNode;
	
	public ParserError(ParserErrorTypes errorType, SourceRange sourceRange, String msgErrorSource, Object msgData) {
		this.errorType = assertNotNull_(errorType);
		this.sourceRange = assertNotNull_(sourceRange);
		this.msgErrorSource = msgErrorSource;
		this.msgData = msgData;
	}
	
	public String getUserMessage() {
		switch(errorType) {
		case INVALID_TOKEN_CHARACTERS:
			return "Invalid token characters \"" + msgErrorSource + "\", delete these characters.";
		case MALFORMED_TOKEN:
			return "Error during tokenization: " + msgErrorSource;
		case EXPECTED_TOKEN:
			DeeTokens expToken = (DeeTokens) msgData;
			return "Syntax error on token \"" + msgErrorSource + "\", expected " + expToken + " after.";
		case EXPECTED_RULE:
			return "Unexpected token after \"" + msgErrorSource + "\", while trying to parse " + msgData + ".";
		case SYNTAX_ERROR:
			return "Unexpected token \"" + msgErrorSource + "\", while trying to parse " + msgData + ".";
		case INVALID_EXTERN_ID:
			return "Invalid linkage specifier \"" + msgErrorSource + "\", valid ones are: " +
				StringUtil.collToString(Linkage.values(), ",") + ".";
		case EXP_MUST_HAVE_PARENTHESES:
			return "Expression " + msgErrorSource + " must be parenthesized when next to operator: " + msgData + ".";
		case TYPE_USED_AS_EXP_VALUE:
			return "The type " + msgErrorSource + " cannot be used as an expression value.";
		case INVALID_QUALIFIER:
			return "The type " + msgErrorSource + " cannot directly be used as a qualifier in qualified reference.";
		}
		throw assertFail();
	}
	
	@Override
	public String toString() {
		return "ERROR:" + errorType + sourceRange.toString() +
			(msgErrorSource == null ? "" : ("【"+msgErrorSource+"】")) + "("+msgData+")";
	}
	
}