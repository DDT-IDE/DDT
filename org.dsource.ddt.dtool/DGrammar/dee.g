grammar DParser;

options {
  language=Java;
  output=AST;
} 

tokens {
	PLUS 	= '+' ;
	MINUS	= '-' ;
	MULT	= '*' ; 
	DIV		= '/' ;
}


@header { 
/* PHOENIX test code 7  */
}

/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/


WHITESPACE : ( ' ' | '\t' | '\u000B'| '\u000C' | '\r' | '\n')+ 	{ channel = 99; } ;

/*--- COMMENTS ---*/
LINE_COMMENT	: '//' (~('\n'|'\r'))* '\r'? '\n' { channel = 99; } ;
MULTILINE_COMMENT	: ('/*'  ( options {greedy=false;} : . )* '*/') { channel = 99; } ;
NESTING_COMMENT	
	: ('/+'  ( options {greedy=false;} : NESTING_COMMENT | . )* '+/') { channel = 99; } ;


/*--- ENT NAME ---*/

IDENT :  IdStartChar (IdStartChar | '0'..'9')*;
fragment IdStartChar :  '_' | 'a'..'z' | 'A'..'Z'  ; // | '\u0080'..'\ufffe'

/*--- STRINGS ---*/

CHARLITERAL :	'\'' (~('\''|'\\') | EscapeChar) '\'';

// TODO: simplify
fragment EscapeChar
    :   '\\' ('\''|'\"'|'?'|'\\'|'a'|'b'|'f'|'n'|'r'|'t'|'v')
    |   OctalEscape 
    |   HexEscape 
    |	'\\' '&' //TODO \& NamedCharacterEntity ;
    ;
// TODO: 	\ EndOfFile  -> WTF??
    
fragment OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ;

fragment HexEscape
    :   '\\U' HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit
    |   '\\u' HexDigit HexDigit HexDigit HexDigit
    |   '\\x' HexDigit HexDigit ;

// is also Hex String
fragment RAW_STRING :   ('r' | 'x' ) '"' (~'"')* '"' ;
fragment RAW_STRING_ALT :   '`' (~'`')* '`' ;
fragment DQ_STRING   :  '"' (~('"'|'\\') | EscapeChar)* '"' ;

fragment StringPostfix  : 'c' | 'w' | 'd';
STRING	:	(RAW_STRING | RAW_STRING_ALT | DQ_STRING ) StringPostfix? ;


/*--- INTEGERS ---*/

INTLITERAL :  Integer IntSuffix? ;
fragment IntSuffix :  'L'|'u'|'U'|'Lu'|'LU'|'uL'|'UL' ;
fragment Integer :	Decimal| Binary| Octal| Hexadecimal ;

fragment Decimal :  '0' | '1'..'9' (DecimalDigit | '_')* ;
fragment Binary :  ('0b' | '0B') ('0' | '1' | '_')+ ;
fragment Octal :  '0' (OctalDigit | '_')+ ;
fragment Hexadecimal :  ('0x' | '0X') (HexDigit | '_')+;	

fragment DecimalDigit :  '0'..'9' ;
fragment OctalDigit	:  '0'..'7' ;
fragment HexDigit :  ('0'..'9'|'a'..'f'|'A'..'F') ;


/*--- FLOATS ---*/
FLOATLITERAL :  Float FloatTypeSuffix? ImaginarySuffix? ;

fragment Float
    :   DecimalDigits '.' (DecimalDigits DecimalExponent?)?
    |   '.' DecimalDigits DecimalExponent? 
    |   DecimalDigits DecimalExponent? 
	;

fragment DecimalExponent :  'e' | 'E' | 'e+' | 'E+' | 'e-' | 'E-' DecimalDigits;
fragment DecimalDigits :  ('0'..'9'|'_')+ ;
fragment FloatTypeSuffix :   'f' | 'F' | 'L';
fragment ImaginarySuffix :   'i';


//QNAME :	IDENT ('.' IDENT)* ;


/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/

/*-------- MODULE --------*/

dmodule : moduledeclaration? decldefs EOF;

moduledeclaration : 'module' modulename ';' ;
modulename : IDENT ; //('.' IDENT)*;


decldefs :  decldef*;

decldef 
	:  importdeclaration
	| declaration
//	| attributeSpecifier
/*	
	enumdeclaration
	classdeclaration
	interfacedeclaration
	aggregatedeclaration
	
	constructor
	destructor
	invariant
	unittest
	staticconstructor
	staticdestructor
	debugspecification
	versionspecification
*/	//| ';'
	;

importdeclaration : 'static'? 'import' importlist ;

importlist
	: singleimport (',' importlist)?
	| importbindings ;
	
singleimport
	: modulename
	| IDENT '=' modulename ;

importbindings:
	singleimport ':' importbind (',' importbind)* ; // ImportBindList = importbind+

importbind
	: IDENT
	| IDENT '=' IDENT; // FIXME: WTF?


/*------------ ATTRIBUTES ------------*/

attributeSpecifier 
    : attribute 
    | attribute declarationBlock ;

attribute :
    //linkageAttribute
    //| alignAttribute
    //pragma
    //| 'deprecated'
    | 'private' | 'package' | 'protected' | 'public' | 'export'
    //| 'static'
    //| 'final'
    //| 'override'
    //| 'abstract'
    //| 'const'
    //| 'auto' 
    ;

declarationBlock 
	:	decldef
    | '{' decldefs '}' ;

/*------------ DECLARATIONS ------------*/

/*** entities and names ***/

qname: (IDENT | templateInstance) ('.' qname)? ;

templateInstance : 'TEMPLATE INSTANCE' //TODO
	;

entityRef
	:	typeRef
	|	rootNameRef;

rootNameRef
	:  'typeof' '(' expression ')' ('.' qname)? 
	| '.' qname
	|  qname
	;

typeRef :  basicType modType?;

/*-------*/

basicType :  primitiveType | rootNameRef ;

primitiveType :  'void'
	|'bool'|'byte'|'ubyte'|'short'|'ushort'|'int'|'uint'|'long'|'ulong'
    |'float'|'double'|'real'|'ifloat'|'idouble'|'ireal'|'cfloat'|'cdouble'|'creal'
    |'char'|'wchar'|'dchar';

modType
	:	'*' modType?
	|	'[]' modType?
	|  '[' expression ']'
    |  '[' typeRef ']'
    |  'delegate' parameters
    |  'function' parameters
	;


exprEnt
	: basicType '.' qname // These be properties
	| rootNameRef ;


/*** declarations ***/

declaration
	: 'typedef' typeRef IDENT ';'
	| 'alias' rootNameRef IDENT ';'
	| varDeclaration ;

/** auto **/
autoDeclaration
	: 'auto' identifierInitializerList ';' //TODO
	;
	
storageclass: 'abstract'|'auto'|'const'|'deprecated'|'extern'|'final'|'override'|'static'|'synchronized';

varDeclaration 	:  storageclass* actualVarDeclaration ;

actualVarDeclaration options { k=2;}	:		
//    | type declaratorInitializerList ';' //C declarators NOT SUPPORTED
     ( typeRef identifierInitializerList ';' )
    | ( mytype functionDeclarator functionBody )
//      type ((identifierInitializerList ';') | (functionDeclarator functionBody))
	| autoDeclaration
	;

mytype
	: 'asdfagad'
	;
	


identifierInitializerList
	: IDENT ('=' initializer)? (',' identifierInitializerList)?
	;

singleDeclaration :  typeRef IDENT;


/*** function defs ***/

functionDeclarator : IDENT parameters;

parameters :  '(' parameterList ')' ;

parameterList : parameter (',' parameterList)? ;
	
parameter
	:  singleDeclaration ('=' assignExpression)?
	|  inout singleDeclaration ('=' assignExpression)? ;


inout: 'in'| 'out'|'inout'|'lazy';

functionBody
	: '{' '}' //TODO
	;



/***** Initializers *****/

initializer
	: 'void' //TODO
    | '=' ('*'|'+'|'-') assignExpression
/*        ArrayInitializer
        StructInitializer */
	;

/*

ArrayInitializer:
	[ ArrayMemberInitializations ]
	[ ]

ArrayMemberInitializations:
	ArrayMemberInitialization
	ArrayMemberInitialization ,
	ArrayMemberInitialization , ArrayMemberInitializations

ArrayMemberInitialization:
	AssignExpression
	AssignExpression : AssignExpression

StructInitializer:
	{  }
	{ StructMemberInitializers }

StructMemberInitializers:
	StructMemberInitializer
	StructMemberInitializer ,
	StructMemberInitializer , StructMemberInitializers

StructMemberInitializer:
	AssignExpression
	Identifier : AssignExpression
*/





expression
	: 'EXPRESSION'
	;
	

assignExpression
	: 'ASSIGN  EXPRESSION'
	;	
