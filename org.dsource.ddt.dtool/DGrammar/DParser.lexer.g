lexer grammar DParserLexer;
options {
  language=Java;

}

PLUS : '+' ;
MINUS : '-' ;
MULT : '*' ;
DIV : '/' ;
T39 : 'module' ;
T40 : ';' ;
T41 : 'static' ;
T42 : 'import' ;
T43 : ',' ;
T44 : '=' ;
T45 : ':' ;
T46 : 'private' ;
T47 : 'package' ;
T48 : 'protected' ;
T49 : 'public' ;
T50 : 'export' ;
T51 : '{' ;
T52 : '}' ;
T53 : '.' ;
T54 : 'TEMPLATE INSTANCE' ;
T55 : 'typeof' ;
T56 : '(' ;
T57 : ')' ;
T58 : 'void' ;
T59 : 'bool' ;
T60 : 'byte' ;
T61 : 'ubyte' ;
T62 : 'short' ;
T63 : 'ushort' ;
T64 : 'int' ;
T65 : 'uint' ;
T66 : 'long' ;
T67 : 'ulong' ;
T68 : 'float' ;
T69 : 'double' ;
T70 : 'real' ;
T71 : 'ifloat' ;
T72 : 'idouble' ;
T73 : 'ireal' ;
T74 : 'cfloat' ;
T75 : 'cdouble' ;
T76 : 'creal' ;
T77 : 'char' ;
T78 : 'wchar' ;
T79 : 'dchar' ;
T80 : '[]' ;
T81 : '[' ;
T82 : ']' ;
T83 : 'delegate' ;
T84 : 'function' ;
T85 : 'typedef' ;
T86 : 'alias' ;
T87 : 'auto' ;
T88 : 'abstract' ;
T89 : 'const' ;
T90 : 'deprecated' ;
T91 : 'extern' ;
T92 : 'final' ;
T93 : 'override' ;
T94 : 'synchronized' ;
T95 : 'asdfagad' ;
T96 : 'in' ;
T97 : 'out' ;
T98 : 'inout' ;
T99 : 'lazy' ;
T100 : 'EXPRESSION' ;
T101 : 'ASSIGN  EXPRESSION' ;

// $ANTLR src "dee.g" 25
WHITESPACE : ( ' ' | '\t' | '\u000B'| '\u000C' | '\r' | '\n')+ 	{ channel = 99; } ;

/*--- COMMENTS ---*/
// $ANTLR src "dee.g" 28
LINE_COMMENT	: '//' (~('\n'|'\r'))* '\r'? '\n' { channel = 99; } ;
// $ANTLR src "dee.g" 29
MULTILINE_COMMENT	: ('/*'  ( options {greedy=false;} : . )* '*/') { channel = 99; } ;
// $ANTLR src "dee.g" 30
NESTING_COMMENT	
	: ('/+'  ( options {greedy=false;} : NESTING_COMMENT | . )* '+/') { channel = 99; } ;


/*--- ENT NAME ---*/

// $ANTLR src "dee.g" 36
IDENT :  IdStartChar (IdStartChar | '0'..'9')*;
// $ANTLR src "dee.g" 37
fragment IdStartChar :  '_' | 'a'..'z' | 'A'..'Z'  ; // | '\u0080'..'\ufffe'

/*--- STRINGS ---*/

// $ANTLR src "dee.g" 41
CHARLITERAL :	'\'' (~('\''|'\\') | EscapeChar) '\'';

// TODO: simplify
// $ANTLR src "dee.g" 44
fragment EscapeChar
    :   '\\' ('\''|'\"'|'?'|'\\'|'a'|'b'|'f'|'n'|'r'|'t'|'v')
    |   OctalEscape 
    |   HexEscape 
    |	'\\' '&' //TODO \& NamedCharacterEntity ;
    ;
// TODO: 	\ EndOfFile  -> WTF??
    
// $ANTLR src "dee.g" 52
fragment OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ;

// $ANTLR src "dee.g" 57
fragment HexEscape
    :   '\\U' HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit
    |   '\\u' HexDigit HexDigit HexDigit HexDigit
    |   '\\x' HexDigit HexDigit ;

// is also Hex String
// $ANTLR src "dee.g" 63
fragment RAW_STRING :   ('r' | 'x' ) '"' (~'"')* '"' ;
// $ANTLR src "dee.g" 64
fragment RAW_STRING_ALT :   '`' (~'`')* '`' ;
// $ANTLR src "dee.g" 65
fragment DQ_STRING   :  '"' (~('"'|'\\') | EscapeChar)* '"' ;

// $ANTLR src "dee.g" 67
fragment StringPostfix  : 'c' | 'w' | 'd';
// $ANTLR src "dee.g" 68
STRING	:	(RAW_STRING | RAW_STRING_ALT | DQ_STRING ) StringPostfix? ;


/*--- INTEGERS ---*/

// $ANTLR src "dee.g" 73
INTLITERAL :  Integer IntSuffix? ;
// $ANTLR src "dee.g" 74
fragment IntSuffix :  'L'|'u'|'U'|'Lu'|'LU'|'uL'|'UL' ;
// $ANTLR src "dee.g" 75
fragment Integer :	Decimal| Binary| Octal| Hexadecimal ;

// $ANTLR src "dee.g" 77
fragment Decimal :  '0' | '1'..'9' (DecimalDigit | '_')* ;
// $ANTLR src "dee.g" 78
fragment Binary :  ('0b' | '0B') ('0' | '1' | '_')+ ;
// $ANTLR src "dee.g" 79
fragment Octal :  '0' (OctalDigit | '_')+ ;
// $ANTLR src "dee.g" 80
fragment Hexadecimal :  ('0x' | '0X') (HexDigit | '_')+;	

// $ANTLR src "dee.g" 82
fragment DecimalDigit :  '0'..'9' ;
// $ANTLR src "dee.g" 83
fragment OctalDigit	:  '0'..'7' ;
// $ANTLR src "dee.g" 84
fragment HexDigit :  ('0'..'9'|'a'..'f'|'A'..'F') ;


/*--- FLOATS ---*/
// $ANTLR src "dee.g" 88
FLOATLITERAL :  Float FloatTypeSuffix? ImaginarySuffix? ;

// $ANTLR src "dee.g" 90
fragment Float
    :   DecimalDigits '.' (DecimalDigits DecimalExponent?)?
    |   '.' DecimalDigits DecimalExponent? 
    |   DecimalDigits DecimalExponent? 
	;

// $ANTLR src "dee.g" 96
fragment DecimalExponent :  'e' | 'E' | 'e+' | 'E+' | 'e-' | 'E-' DecimalDigits;
// $ANTLR src "dee.g" 97
fragment DecimalDigits :  ('0'..'9'|'_')+ ;
// $ANTLR src "dee.g" 98
fragment FloatTypeSuffix :   'f' | 'F' | 'L';
// $ANTLR src "dee.g" 99
fragment ImaginarySuffix :   'i';


//QNAME :	IDENT ('.' IDENT)* ;


/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/

/*-------- MODULE --------*/

