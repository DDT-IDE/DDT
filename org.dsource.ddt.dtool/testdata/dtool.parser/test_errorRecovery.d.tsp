▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Invalid elements in declaration:
#error(SE_decl) ) ;
#error(SE_decl) ]
#error(SE_decl) }

#STRUCTURE_EXPECTED:
InvalidSyntaxElement DeclarationEmpty
InvalidSyntaxElement
InvalidSyntaxElement
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  recovery of reference start in declaration
foo #error(EXP_IDENTIFIER) ;
#STRUCTURE_EXPECTED:
InvalidDeclaration(?)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo #error(EXP_IDENTIFIER) #@INVALID_DECL__NO_SEMICOLON【】
#STRUCTURE_EXPECTED:
InvalidDeclaration(?)

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
DeclarationAlign  
DefinitionVariable(RefPrimitive DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
extern(C) #error(EXPRULE_decl)
#STRUCTURE_EXPECTED:
DeclarationLinkage()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
extern(C) #error(SE_decl) ;
#STRUCTURE_EXPECTED:
DeclarationLinkage(DeclarationEmpty)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
extern(C) #error(SE_decl) ] int foo;
#STRUCTURE_EXPECTED:
DeclarationLinkage(InvalidSyntaxElement)
DefinitionVariable(? ?)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  var Initializer
var xx = #error(EXPRULE_Initializer) ;

#STRUCTURE_EXPECTED:
DefinitionVariable(? DefSymbol InitializerExp(MissingExpression))
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  recovery of expressions: 

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    new   (foo)  #@NO_REF  (123) 
#STRUCTURE_EXPECTED:  ExpCall( ExpNew( #@ExpIdentifier  #@NO_REF ) Integer)  
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    new  ( #@EXP_ASSIGN__LITE #error(EXP_CLOSE_PARENS)  #parser(IgnoreRest) foo (456)
#STRUCTURE_EXPECTED:  ExpNew( #@EXP_ASSIGN__LITE )
  