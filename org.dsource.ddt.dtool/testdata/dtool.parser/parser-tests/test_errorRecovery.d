▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#error(SE_decl){)} ;
#error(SE_decl) ]
#error(SE_decl) }

#AST_STRUCTURE_EXPECTED:
InvalidSyntaxElement DeclarationEmpty
InvalidSyntaxElement
InvalidSyntaxElement
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  recovery of reference start
foo #error(EXP_IDENTIFIER) ;
#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(?)
#AST_SOURCE_EXPECTED:
foo ;
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo #error(EXP_IDENTIFIER) #error(EXP_SEMICOLON)
#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(?)
#AST_SOURCE_EXPECTED:
foo
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
. #error:EXP_ID int dummy;
foo. #error:EXP_ID int dummy;
#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(RefModuleQualified(?)) DefVariable(RefPrimitive DefSymbol)
InvalidDeclaration(RefQualified(* ?)) DefVariable(RefPrimitive DefSymbol)
#AST_SOURCE_EXPECTED:
.  int dummy;
foo.  int dummy;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  recovery of identifiers: ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import#error(EXP_ID);
import #error(EXP_ID) ;
pragma(#error(EXP_ID));
pragma( #error(EXP_ID) );

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  recovery of identifier list: ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

import foo, #error:EXP_ID ;
int    foo, #error:EXP_ID ;
//int[]foo;

import foo : #error:EXP_ID , foo2 ;
int    foo = #error:EXPRULE_INITIALIZER , foo2 ;

#AST_STRUCTURE_EXPECTED:
DeclarationImport(ImportContent(?) ImportContent(?))
DefinitionVariable(? DefSymbol DefVarFragment(?))

DeclarationImport(ImportSelective(ImportContent(?) RefImportSelection RefImportSelection))
DefinitionVariable(? DefSymbol InitializerExp(MissingExpression) DefVarFragment(?))



▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ :recovery of  KEYWORD(ARGUMENT);  format
mixin #error(EXP_OPEN_PARENS) #error(EXP_SEMICOLON)
mixin #error(EXP_OPEN_PARENS) ;
#AST_SOURCE_EXPECTED:
mixin(); mixin(); 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ :recovery of  KEYWORD(ARGUMENT) DECL;  format
extern(C) #error(EXPRULE_decl)

#AST_SOURCE_EXPECTED:
extern(C)
#AST_STRUCTURE_EXPECTED:
DeclarationLinkage()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
extern(C) #error(SE_decl) ;

#AST_STRUCTURE_EXPECTED:
DeclarationLinkage(InvalidSyntaxElement)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
extern(C) #error(SE_decl) ] int foo;

#AST_STRUCTURE_EXPECTED:
DeclarationLinkage(InvalidSyntaxElement)
DefinitionVariable(? ?)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
// No close brackets/parentheses cause rule to quit parsing.
align(16                  #error:EXP_CLOSE_PARENS int foo;
align(16                  #error:EXP_CLOSE_PARENS ;
align( #error:EXP_INTEGER_DECIMAL #error:EXP_CLOSE_PARENS int foo;
align( #error:EXP_INTEGER_DECIMAL ) int bar;

foo[ #error:EXP_CLOSE_BRACKET public int dummyB1;
foo[1 #error:EXP_CLOSE_BRACKET   dummyB2 #error:EXP_ID;
foo[int #error:EXP_CLOSE_BRACKET dummyB3 #error:EXP_ID;

#AST_SOURCE_EXPECTED:
align(16) /*;*/ int foo;
align(16) /*;*/ ;
align()   /*;*/ int foo;
align() int bar;

foo[] public int dummyB1;
foo[1] dummyB2;
foo[int] dummyB3;

#AST_STRUCTURE_EXPECTED:
DeclarationAlign  DefinitionVariable(RefPrimitive DefSymbol)
DeclarationAlign  DeclarationEmpty
DeclarationAlign  DefinitionVariable(RefPrimitive DefSymbol)
DeclarationAlign( DefinitionVariable(RefPrimitive DefSymbol) )

InvalidDeclaration(RefTypeDynArray(RefIdentifier))                ?(DefinitionVariable(RefPrimitive DefSymbol))
InvalidDeclaration(RefIndexing(RefIdentifier ExpLiteralInteger))  InvalidDeclaration(RefIdentifier)
InvalidDeclaration(RefIndexing(RefIdentifier RefPrimitive))       InvalidDeclaration(RefIdentifier)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  var Initializer
var xx = #error:EXPRULE_INITIALIZER ;

#AST_STRUCTURE_EXPECTED:
DefinitionVariable(? DefSymbol InitializerExp(MissingExpression))
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  recovery of expressions: ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
The current policy for recovery of expression parsing is to 
not quit expression parsing call stack when a syntax errors occurs.
 
(XXX: Review this in the future, may not be best policy)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)        new   (foo)  #@MISSING_REF  (123) 
#AST_STRUCTURE_EXPECTED:  ExpCall( ExpNew( #@ExpIdentifier  #@MISSING_REF ) Integer)  
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)        new  ( #@EXP_ASSIGN__LITE #error(EXP_CLOSE_PARENS)  #parser(IgnoreRest) foo (456)
#AST_STRUCTURE_EXPECTED:  ExpNew( #@EXP_ASSIGN__LITE )
  