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
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.util.Comparator;

import melnorme.utilbox.misc.StringUtil;
import descent.core.ISourceRange;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclarationLinkage.Linkage;
import dtool.ast.statements.StatementScope.ScopeTypes;

public class ParserError implements ISourceRange {
	
	public enum ParserErrorTypes {
		
		INVALID_TOKEN_CHARACTERS, // Lexer: invalid characters, cannot form token
		MALFORMED_TOKEN, // Lexer: recovered token has errors
		
		EXPECTED_TOKEN, // expected specific token
		EXPECTED_RULE, // expected valid token for rule
		SYNTAX_ERROR, // unexpected rule in rule start
		
		EXP_MUST_HAVE_PARENTHESES, 
		TYPE_USED_AS_EXP_VALUE,
		INIT_USED_IN_EXP,
		NO_CHAINED_TPL_SINGLE_ARG,
		
		INVALID_EXTERN_ID,
		INVALID_SCOPE_ID,
		INVALID_TRAITS_ID,
		INVALID_ATTRIB_ID,
		LAST_CATCH,
		
	}
	
	protected final ParserErrorTypes errorType;
	protected final SourceRange sourceRange;
	protected final String msgErrorSource;
	protected final Object msgData;
	
	public ParserError(ParserErrorTypes errorType, SourceRange sourceRange, String msgErrorSource, Object msgData) {
		this.errorType = assertNotNull_(errorType);
		this.sourceRange = assertNotNull_(sourceRange);
		this.msgErrorSource = msgErrorSource;
		this.msgData = msgData;
	}
	
	@Override
	public int getOffset() {
		return sourceRange.getOffset();
	}
	
	@Override
	public int getLength() {
		return sourceRange.getLength();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ParserError))
			return false;
		
		ParserError other = (ParserError) obj;
		return errorType == other.errorType && areEqual(sourceRange, other.sourceRange) 
			&& areEqual(msgErrorSource, other.msgErrorSource) && areEqual(msgData, other.msgData);
	}
	
	public static final class ErrorSourceRangeComparator implements Comparator<ParserError> {
		@Override
		public int compare(ParserError o1, ParserError o2) {
			int compareResult = o1.sourceRange.compareTo(o2.sourceRange);
			return compareResult;
		}
	}
	
	@Override
	public String toString() {
		return "ERROR:" + errorType + sourceRange.toString() +
			(msgErrorSource == null ? "" : ("【"+msgErrorSource+"】")) + "("+msgData+")";
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
		case EXP_MUST_HAVE_PARENTHESES:
			return "Expression " + msgErrorSource + " must be parenthesized when next to operator: " + msgData + ".";
		case TYPE_USED_AS_EXP_VALUE:
			return "The type " + msgErrorSource + " cannot be used as an expression value.";
		case NO_CHAINED_TPL_SINGLE_ARG:
			return "The template '!' single argument " + msgErrorSource + 
				" cannot be used next to other template '!' single arguments.";
		case INIT_USED_IN_EXP:
			return "The initializer " + msgErrorSource + " cannot be used as part of an expression.";
		
		case INVALID_EXTERN_ID:
			return "Invalid linkage specifier \"" + msgErrorSource + "\", valid ones are: " +
				StringUtil.collToString(Linkage.values(), ",") + ".";
		case INVALID_SCOPE_ID:
			return "Invalid scope specifier \"" + msgErrorSource + "\", must be one of: " +
				StringUtil.collToString(ScopeTypes.values(), ",") + ".";
		case INVALID_TRAITS_ID:
			return "Invalid traits id \"" + msgErrorSource + "\".";
		case INVALID_ATTRIB_ID:
			return "Invalid @ attribute id \"" + msgErrorSource + "\".";
		case LAST_CATCH:
			return "Catch without parameter must be last catch (and only one per try statement). ";
			
		}
		throw assertFail();
	}
	
}