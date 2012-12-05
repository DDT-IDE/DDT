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
	
	OPEN_PARENS("("),
	CLOSE_PARENS(")"),
	OPEN_BRACE("{"),
	CLOSE_BRACE("}"),
	OPEN_BRACKET("["),
	CLOSE_BRACKET("]"),
	
	CHAR_LITERAL,
	
	QUESTION("?"),
	COMMA(","),
	SEMICOLON(";"),
	COLON(":"),
	DOLLAR("$"),
	AT("@"),
	
	DOT("."), DOUBLE_DOT(".."), TRIPLE_DOT("..."),
	
	MINUS("-"),MINUS_ASSIGN("-="),DECREMENT("--"), 
	PLUS("+"),PLUS_ASSIGN("+="),INCREMENT("++"),	
	DIV("/"),DIV_ASSIGN("/="), 
	STAR("*"), MULT_ASSIGN("*="), 
	MOD("%"), MOD_ASSIGN("%="), 
	
	AND("&"), AND_ASSIGN("&="), LOGICAL_AND("&&"), OR("|"), OR_ASSIGN("|="), LOGICAL_OR("||"), 
	XOR("^"), XOR_ASSIGN("^="),
	ASSIGN("="), EQUALS("=="),
	CONCAT("~"), CONCAT_ASSIGN("~="),
	
	LAMBDA("=>"),
	
	NOT("!"), NOT_EQUAL("!="),
	LESS_THAN("<"), LESS_EQUAL("<="), GREATER_THAN(">"), GREATER_EQUAL(">="),
	
	LEFT_SHIFT("<<"), LEFT_SHIFT_ASSIGN("<<="), RIGHT_SHIFT(">>"), RIGHT_SHIFT_ASSIGN(">>="), 
	TRIPLE_RSHIFT(">>>"), TRIPLE_RSHIFT_ASSIGN(">>>="),
	LESS_GREATER("<>"), LESS_GREATER_EQUAL("<>="),
	UNORDERED_E("!<>"), UNORDERED("!<>="), 
	UNORDERED_GE("!<"), UNORDERED_G("!<="), UNORDERED_LE("!>"), UNORDERED_L("!>="),
	
	INTEGER, INTEGER_BINARY, INTEGER_OCTAL, INTEGER_HEX,
	FLOAT, FLOAT_HEX,
	
	KW_ABSTRACT("abstract"), KW_ALIAS("alias"), KW_ALIGN("align"), 
	KW_ASM("asm"), KW_ASSERT("assert"), KW_AUTO("auto"),
	KW_BODY("body"), KW_BOOL("bool"), KW_BREAK("break"), KW_BYTE("byte"),
	KW_CASE("case"), KW_CAST("cast"), KW_CATCH("catch"), KW_CDOUBLE("cdouble"), KW_CENT("cent"), KW_CFLOAT("cfloat"),
	KW_CHAR("char"), KW_CLASS("class"), KW_CONST("const"), KW_CONTINUE("continue"), KW_CREAL("creal"),
	KW_DCHAR("dchar"), KW_DEBUG("debug"), KW_DEFAULT("default"), KW_DELEGATE("delegate"), 
	KW_DELETE("delete"), KW_DEPRECATED("deprecated"), KW_DO("do"), KW_DOUBLE("double"),
	KW_ELSE("else"), KW_ENUM("enum"), KW_EXPORT("export"), KW_EXTERN("extern"),
	KW_FALSE("false"), KW_FINAL("final"), KW_FINALLY("finally"), KW_FLOAT("float"), KW_FOR("for"),
	KW_FOREACH("foreach"), KW_FOREACH_REVERSE("foreach_reverse"), KW_FUNCTION("function"), KW_GOTO("goto"),
	KW_IDOUBLE("idouble"), KW_IF("if"), KW_IFLOAT("ifloat"), KW_IMMUTABLE("immutable"), KW_IMPORT("import"), 
	KW_IN("in"), KW_INOUT("inout"), KW_INT("int"), KW_INTERFACE("interface"), KW_INVARIANT("invariant"), 
	KW_IREAL("ireal"), KW_IS("is"), KW_LAZY("lazy"), KW_LONG("long"),
	KW_MACRO("macro"), KW_MIXIN("mixin"), KW_MODULE("module"), 
	KW_NEW("new"), KW_NOTHROW("nothrow"), KW_NULL("null"), KW_OUT("out"), KW_OVERRIDE("override"),
	KW_PACKAGE("package"), KW_PRAGMA("pragma"), 
	KW_PRIVATE("private"), KW_PROTECTED("protected"), KW_PUBLIC("public"), KW_PURE("pure"),
	KW_REAL("real"), KW_REF("ref"), KW_RETURN("return"), 
	KW_SCOPE("scope"), KW_SHARED("shared"), KW_SHORT("short"), KW_STATIC("static"),
	KW_STRUCT("struct"), KW_SUPER("super"), KW_SWITCH("switch"), KW_SYNCHRONIZED("synchronized"),
	KW_TEMPLATE("template"), KW_THIS("this"), KW_THROW("throw"), KW_TRUE("true"), KW_TRY("try"), 
	KW_TYPEDEF("typedef"), KW_TYPEID("typeid"), KW_TYPEOF("typeof"),
	KW_UBYTE("ubyte"), KW_UCENT("ucent"), KW_UINT("uint"), KW_ULONG("ulong"), 
	KW_UNION("union"), KW_UNITTEST("unittest"), KW_USHORT("ushort"),
	KW_VERSION("version"), KW_VOID("void"), KW_VOLATILE("volatile"), 
	KW_WCHAR("wchar"), KW_WHILE("while"), KW_WITH("with"),
	
	KW___FILE__("__FILE__"), 
	KW___LINE__("__LINE__"), 
	KW___GSHARED("__gshared"), 
	KW___THREAD("__thread"), 
	KW___TRAITS("__traits"),
	
	KW___DATE__("__DATE__"),
	KW___EOF__("__EOF__"), // This token is actually never produced in practice, it gets transformed into EOF
	KW___TIME__("__TIME__"),
	KW___TIMESTAMP__("__TIMESTAMP__"),
	KW___VENDOR__("__VENDOR__"),
	KW___VERSION__("__VERSION__"),
	
	SPECIAL_TOKEN_LINE,
	
	;
	protected final String sourceValue;
	
	private DeeTokens() {
		this(null);
	}
	private DeeTokens(String sourceValue) {
		this.sourceValue = sourceValue;
	}
	
	public final String getSourceValue() {
		return sourceValue;
	}
}