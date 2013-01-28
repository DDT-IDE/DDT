▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#error(SE_decl){)}
#error(SE_decl) ]
#error(SE_decl) }

#AST_STRUCTURE_EXPECTED:
InvalidSyntaxElement
InvalidSyntaxElement
InvalidSyntaxElement
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo #error(EXP_IDENTIFIER) ;
#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(?)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo #error(EXP_IDENTIFIER) #error(EXP_SEMICOLON)
#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(?)
#AST_EXPECTED:
foo ;
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

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  recovery of expressions: ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
var xx = #error:EXPRULE_INITIALIZER ;

// TODO rest of expressions

#AST_STRUCTURE_EXPECTED:
DefinitionVariable(? DefSymbol InitializerExp(MissingExpression))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ :recovery of  KEYWORD(ARGUMENT);  format
mixin #error(EXP_OPEN_PARENS) #error(EXP_SEMICOLON)
mixin #error(EXP_OPEN_PARENS) ;
#AST_EXPECTED:
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
align( #error:EXP_INTEGER #error:EXP_CLOSE_PARENS int foo;
align( #error:EXP_INTEGER ) int bar;

foo[ #error:EXP_CLOSE_BRACKET int dummyB;
//foo[1 dummyB2; TODO
//foo[int dummyB2; TODO

#AST_SOURCE_EXPECTED:
align(16) /*;*/ int foo;
align(16) /*;*/ ;
align()   /*;*/ int foo;
align() int bar;

foo[]; int dummyB;

#AST_STRUCTURE_EXPECTED:
DeclarationAlign  DefinitionVariable(RefPrimitive DefSymbol)
DeclarationAlign  DeclarationEmpty
DeclarationAlign  DefinitionVariable(RefPrimitive DefSymbol)
DeclarationAlign( DefinitionVariable(RefPrimitive DefSymbol) )

InvalidDeclaration(RefTypeDynArray(RefIdentifier))  DefinitionVariable(RefPrimitive DefSymbol)