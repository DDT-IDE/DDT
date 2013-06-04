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

/**
 * Tokens produced by the D Lexer.
 * Some of these tokens are synthetic - not actually produced by the Lexer - 
 * but used by the parser to group other tokens into categories. 
 */
public enum DeeTokens {
	
	EOF,
	INVALID_TOKEN(null, true),
	
	EOL(null, true),
	WHITESPACE(null, true),
	SCRIPT_LINE_INTRO(null, true),
	
	COMMENT(null, true),
	COMMENT_MULTI (COMMENT), 
	COMMENT_NESTED(COMMENT), 
	COMMENT_LINE  (COMMENT),
	
	IDENTIFIER,
	
	OPEN_PARENS("("),
	CLOSE_PARENS(")"),
	OPEN_BRACE("{"),
	CLOSE_BRACE("}"),
	OPEN_BRACKET("["),
	CLOSE_BRACKET("]"),
	
	STRING(), // Note: special keyword token also have this category
	STRING_WYSIWYG(STRING),
	STRING_DQ     (STRING), 
	STRING_HEX    (STRING), 
	STRING_DELIM  (STRING), 
	STRING_TOKENS (STRING),
	
	CHARACTER,
	
	INTEGER(), // Note: special keyword token also have this category
	INTEGER_DECIMAL(INTEGER), 
	INTEGER_BINARY (INTEGER), 
	INTEGER_OCTAL  (INTEGER), 
	INTEGER_HEX    (INTEGER),
	
	FLOAT(),
	FLOAT_DECIMAL(FLOAT), 
	FLOAT_HEX    (FLOAT),
	
	QUESTION("?"),
	COMMA(","),
	SEMICOLON(";"),
	COLON(":"),
	DOLLAR("$"),
	AT("@"),
	
	DOT("."), DOUBLE_DOT(".."), TRIPLE_DOT("..."),
	
	MINUS("-"), MINUS_ASSIGN("-="), DECREMENT("--"), 
	PLUS("+"), PLUS_ASSIGN("+="), INCREMENT("++"),	
	DIV("/"), DIV_ASSIGN("/="), 
	STAR("*"), MULT_ASSIGN("*="), 
	MOD("%"), MOD_ASSIGN("%="),
	POW("^^"), POW_ASSIGN("^^="), 
	
	AND("&"), AND_ASSIGN("&="), LOGICAL_AND("&&"), OR("|"), OR_ASSIGN("|="), LOGICAL_OR("||"), 
	XOR("^"), XOR_ASSIGN("^="),
	CONCAT("~"), CONCAT_ASSIGN("~="),
	
	LAMBDA("=>"),
	
	ASSIGN("="), EQUALS("=="),
	NOT("!"), NOT_EQUAL("!="),
	
	LESS_THAN("<"), LESS_EQUAL("<="), GREATER_THAN(">"), GREATER_EQUAL(">="),
	LESS_GREATER("<>"), LESS_GREATER_EQUAL("<>="),
	UNORDERED_E("!<>"), UNORDERED("!<>="), 
	UNORDERED_GE("!<"), UNORDERED_G("!<="), UNORDERED_LE("!>"), UNORDERED_L("!>="),
	
	LEFT_SHIFT("<<"), LEFT_SHIFT_ASSIGN("<<="), RIGHT_SHIFT(">>"), RIGHT_SHIFT_ASSIGN(">>="), 
	TRIPLE_RSHIFT(">>>"), TRIPLE_RSHIFT_ASSIGN(">>>="),
	
	
	PRIMITIVE_KW(),
	KW_BOOL("bool",     PRIMITIVE_KW),
	KW_VOID("void",     PRIMITIVE_KW),
	KW_BYTE("byte",     PRIMITIVE_KW), KW_UBYTE("ubyte",     PRIMITIVE_KW),
	KW_SHORT("short",   PRIMITIVE_KW), KW_USHORT("ushort",   PRIMITIVE_KW),
	KW_INT("int",       PRIMITIVE_KW), KW_UINT("uint",       PRIMITIVE_KW), 
	KW_LONG("long",     PRIMITIVE_KW), KW_ULONG("ulong",     PRIMITIVE_KW),
	KW_CHAR("char",     PRIMITIVE_KW), KW_WCHAR("wchar",     PRIMITIVE_KW), KW_DCHAR("dchar", PRIMITIVE_KW),
	KW_FLOAT("float",   PRIMITIVE_KW), KW_DOUBLE("double",   PRIMITIVE_KW), KW_REAL("real",   PRIMITIVE_KW),
	KW_IFLOAT("ifloat", PRIMITIVE_KW), KW_IDOUBLE("idouble", PRIMITIVE_KW), KW_IREAL("ireal", PRIMITIVE_KW),  
	KW_CFLOAT("cfloat", PRIMITIVE_KW), KW_CDOUBLE("cdouble", PRIMITIVE_KW), KW_CREAL("creal", PRIMITIVE_KW),
	// These are keywords for an integer type, but are not implemented
	KW_CENT("cent",     PRIMITIVE_KW), KW_UCENT("ucent",     PRIMITIVE_KW),  
	
	PROTECTION_KW(),
	KW_PRIVATE("private",     PROTECTION_KW), 
	KW_PACKAGE("package",     PROTECTION_KW), 
	KW_PROTECTED("protected", PROTECTION_KW),
	KW_PUBLIC("public",       PROTECTION_KW), 
	KW_EXPORT("export",       PROTECTION_KW),
	
	ATTRIBUTE_KW(),
	KW_ABSTRACT("abstract",         ATTRIBUTE_KW),
	
	KW_CONST("const",               ATTRIBUTE_KW),
	KW_IMMUTABLE("immutable",       ATTRIBUTE_KW),
	KW_INOUT("inout",               ATTRIBUTE_KW), 
	KW_SHARED("shared",             ATTRIBUTE_KW),
	
	KW_DEPRECATED("deprecated",     ATTRIBUTE_KW), 
	KW_FINAL("final",               ATTRIBUTE_KW), 
	KW_NOTHROW("nothrow",           ATTRIBUTE_KW), 
	KW_OVERRIDE("override",         ATTRIBUTE_KW), 
	KW_PURE("pure",                 ATTRIBUTE_KW), 
	KW___GSHARED("__gshared",       ATTRIBUTE_KW), 
	KW_SCOPE("scope",               ATTRIBUTE_KW), 
	KW_STATIC("static",             ATTRIBUTE_KW), 
	KW_SYNCHRONIZED("synchronized", ATTRIBUTE_KW),
	
	KW_REF("ref"                  , ATTRIBUTE_KW),
	
	KW_AUTO("auto"),
	
	KW_ALIAS("alias"), KW_ALIGN("align"), 
	KW_ASM("asm"), KW_ASSERT("assert"),
	KW_BODY("body"), KW_BREAK("break"), 
	KW_CASE("case"), KW_CAST("cast"), KW_CATCH("catch"), KW_CLASS("class"), KW_CONTINUE("continue"),
	
	KW_DEBUG("debug"), KW_DEFAULT("default"), KW_DELEGATE("delegate"), 
	KW_DELETE("delete"),  KW_DO("do"), 
	KW_ELSE("else"), KW_ENUM("enum"), KW_EXTERN("extern"),
	KW_FALSE("false"), KW_FINALLY("finally"), KW_FOR("for"),
	KW_FOREACH("foreach"), KW_FOREACH_REVERSE("foreach_reverse"), KW_FUNCTION("function"), KW_GOTO("goto"),
	KW_IF("if"), KW_IMPORT("import"), 
	KW_IN("in"), KW_INTERFACE("interface"), KW_INVARIANT("invariant"), 
	KW_IS("is"), KW_LAZY("lazy"),
	KW_MACRO("macro"), KW_MIXIN("mixin"), KW_MODULE("module"), 
	KW_NEW("new"), KW_NULL("null"), KW_OUT("out"), 
	KW_PRAGMA("pragma"), 
	KW_RETURN("return"), 
	
	KW_STRUCT("struct"), KW_SUPER("super"), KW_SWITCH("switch"), 
	KW_TEMPLATE("template"), KW_THIS("this"), KW_THROW("throw"), KW_TRUE("true"), KW_TRY("try"), 
	KW_TYPEDEF("typedef"), KW_TYPEID("typeid"), KW_TYPEOF("typeof"),
	KW_UNION("union"), KW_UNITTEST("unittest"), 
	KW_VERSION("version"), KW_VOLATILE("volatile"), 
	KW_WHILE("while"), KW_WITH("with"),
	
	KW___FILE__("__FILE__", STRING), 
	KW___LINE__("__LINE__", INTEGER), 
	KW___THREAD("__thread"), 
	KW___TRAITS("__traits"),
	
	KW___DATE__("__DATE__", STRING),
	KW___EOF__("__EOF__"), // This token is actually never returned by the lexer, it gets transformed into EOF
	KW___TIME__("__TIME__", STRING),
	KW___TIMESTAMP__("__TIMESTAMP__", STRING),
	KW___VENDOR__("__VENDOR__", STRING),
	KW___VERSION__("__VERSION__", INTEGER),
	
	SPECIAL_TOKEN_LINE,
	
	;
	
	protected final String sourceValue;
	protected final boolean isSubChannel; // Flag for tokens like whitespace and comments, that are mostly ignored
	protected final DeeTokens groupToken;
	
	private DeeTokens(String sourceValue, boolean isSubChannel, DeeTokens groupToken) {
		this.sourceValue = sourceValue;
		this.isSubChannel = isSubChannel;
		this.groupToken = groupToken;
	}
	
	private DeeTokens(String sourceValue, boolean isParserIgnored) {
		this(sourceValue, isParserIgnored, null);
	}
	
	private DeeTokens(String sourceValue) {
		this(sourceValue, false);
	}
	
	private DeeTokens() {
		this(null, false);
	}
	
	private DeeTokens(DeeTokens groupToken) {
		this(null, groupToken.isSubChannel, groupToken);
	}
	
	private DeeTokens(String sourceValue, DeeTokens groupToken) {
		this(sourceValue, false, groupToken);
	}
	
	public final String getSourceValue() {
		return sourceValue;
	}
	
	public DeeTokens getGroupingToken() {
		return groupToken == null ? this : groupToken;
	}
	
}