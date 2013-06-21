▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Invalid elements in module decl list:
#error(SE_decl) ) 
#error(SE_decl) ]
#error(SE_decl) while
;

#STRUCTURE_EXPECTED:
InvalidSyntaxElement 
InvalidSyntaxElement
InvalidSyntaxElement
DeclarationEmpty

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ brace recovery
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ decl list parsing
#PARSE(DECLARATION)   public {  pure: #error(SE_decl)【else】  }  
#STRUCTURE_EXPECTED:  DeclarationAttrib(AttribProtection 
  DeclBlock( DeclarationAttrib(AttribBasic DeclList(InvalidSyntaxElement)) )
)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)     {        ; #error(SE_Statement)【else】 #error(EXP_CLOSE_BRACE) #@BREAK #@《)●]》
#STRUCTURE_EXPECTED:  BlockStatement(? InvalidSyntaxElement)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)     default: ; #error(SE_Statement)【else】  #@IGNORE_REST }
#STRUCTURE_EXPECTED:  StatementDefault(ScopedStatementList(? InvalidSyntaxElement))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)    ( { ; #error(EXP_CLOSE_BRACE) )  
#STRUCTURE_EXPECTED:  ExpParentheses(*)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ this case may change in the future 
#PARSE(EXPRESSION)    ( { ; #error(EXP_CLOSE_BRACE) #error(EXP_CLOSE_PARENS) #@BREAK ]
#STRUCTURE_EXPECTED:  ExpParentheses(*)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ statement only
#PARSE(DECLARATION)   #@NULL while (true) ;
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  recovery of reference start in DECLARATION
foo #error(EXP_IDENTIFIER) ;
#STRUCTURE_EXPECTED: IncompleteDeclarator(?)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo #error(EXP_IDENTIFIER) #@NO_SEMICOLON
#STRUCTURE_EXPECTED: IncompleteDeclarator(?)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  recovery of ref identifiers: 
import#error(EXP_ID);
import #error(EXP_ID) ;
pragma(#error(EXP_ID));
pragma( #error(EXP_ID) );

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ recovery of missing DefUnit
#PARSE(DECLARATION)   alias #@NO_DEFID ; 
#STRUCTURE_EXPECTED:  DefinitionAlias(AliasFragment(#@NO_DEFID))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ balance recovery of syntax:  KEYWORD(ARGUMENT) DECL;
// No close brackets/parentheses cause rule to quit parsing.
align(16  #error:EXP_CLOSE_PARENS   int foo;
#STRUCTURE_EXPECTED:
DeclarationAttrib(AttribAlign)  
DefinitionVariable(RefPrimitive DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
extern(C) #@NO_DECLorBLOCK
#STRUCTURE_EXPECTED:
DeclarationAttrib(AttribLinkage #@NO_DECLorBLOCK)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
extern(C) #error(SE_decl) ;
#STRUCTURE_EXPECTED:
DeclarationAttrib(AttribLinkage DeclarationEmpty)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
extern(C) #@NO_DECLorBLOCK #error(SE_decl) ] int foo;
#STRUCTURE_EXPECTED:
DeclarationAttrib(AttribLinkage #@NO_DECLorBLOCK)
InvalidSyntaxElement
DefinitionVariable(? ?)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  var Initializer
var xx = #error(EXPRULE_Initializer) ;
#STRUCTURE_EXPECTED: 
DefinitionVariable(? DefSymbol MissingExpression)
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  recovery of expressions: 

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    new   (foo)  #@NO_TYPEREF  #@BREAK (123) 
#STRUCTURE_EXPECTED:  ExpNew( #@ExpIdentifier  #@NO_TYPEREF )  
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    new  ( #@EXP_ASSIGN__LITE #error(EXP_CLOSE_PARENS)  #@BREAK foo (456)
#STRUCTURE_EXPECTED:  ExpNew( #@EXP_ASSIGN__LITE )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ pragma tokens →◙
int #line 6 "foo\bar"
x;  // this is now line 6 of file foo\bar
