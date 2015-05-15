/*******************************************************************************
 * Copyright (c) 2011, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.ast;

import melnorme.lang.tooling.LANG_SPECIFIC;
import melnorme.lang.tooling.ast.ParserError;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.declarations.AttribLinkage.Linkage;
import dtool.ast.statements.StatementScope.ScopeTypes;
import dtool.parser.DeeTokens;

@LANG_SPECIFIC
public enum ParserErrorTypes {
	
	INVALID_TOKEN_CHARACTERS {
		@Override
		public String getUserMessage(ParserError pe) {
			return "Invalid token characters \"" + pe.msgErrorSource + "\", delete these characters.";
		}
	},
	
	MALFORMED_TOKEN {
		@Override
		public String getUserMessage(ParserError pe) {
			return "Error during tokenization: " + pe.msgErrorSource;
		}
	}, 
	
	
	EXPECTED_TOKEN {
		@Override
		public String getUserMessage(ParserError pe) {
			DeeTokens expToken = (DeeTokens) pe.msgData;
			return "Syntax error on token \"" + pe.msgErrorSource + "\", expected " + expToken + " after.";
		}
	}, 
	
	EXPECTED_RULE {
		@Override
		public String getUserMessage(ParserError pe) {
			return "Unexpected token after \"" + pe.msgErrorSource + "\", while trying to parse " + pe.msgData + ".";
		}
	}, 
	
	
	SYNTAX_ERROR {
		@Override
		public String getUserMessage(ParserError pe) {
			return "Unexpected token \"" + pe.msgErrorSource + "\", while trying to parse " + pe.msgData + ".";
		}
	}, 
	
	
	EXP_MUST_HAVE_PARENTHESES {
		@Override
		public String getUserMessage(ParserError pe) {
			return "Expression " + pe.msgErrorSource + " must be parenthesized when next to operator: " + pe.msgData + ".";
		}
	}, 
	
	TYPE_USED_AS_EXP_VALUE {
		@Override
		public String getUserMessage(ParserError pe) {
			return "The type " + pe.msgErrorSource + " cannot be used as an expression value.";
		}
	}, 
	
	INIT_USED_IN_EXP {
		@Override
		public String getUserMessage(ParserError pe) {
			return "The initializer " + pe.msgErrorSource + " cannot be used as part of an expression.";
		}
	}, 
	
	NO_CHAINED_TPL_SINGLE_ARG {
		@Override
		public String getUserMessage(ParserError pe) {
			return "The template '!' single argument " + pe.msgErrorSource + 
					" cannot be used next to other template '!' single arguments.";
		}
	}, 
	
	
	INVALID_EXTERN_ID {
		@Override
		public String getUserMessage(ParserError pe) {
			return "Invalid linkage specifier \"" + pe.msgErrorSource + "\", valid ones are: " +
					StringUtil.collToString(Linkage.values(), ",") + ".";
		}
	}, 
	
	INVALID_SCOPE_ID {
		@Override
		public String getUserMessage(ParserError pe) {
			return "Invalid scope specifier \"" + pe.msgErrorSource + "\", must be one of: " +
					StringUtil.collToString(ScopeTypes.values(), ",") + ".";
		}
	}, 

	INVALID_TRAITS_ID {
		@Override
		public String getUserMessage(ParserError pe) {
			return "Invalid traits id \"" + pe.msgErrorSource + "\".";
		}
	}, 
	
	LAST_CATCH {
		@Override
		public String getUserMessage(ParserError pe) {
			return "Catch without parameter must be last catch (and only one per try statement). ";
		}
	}, 
	;
	
	public abstract String getUserMessage(ParserError pe);
	
}