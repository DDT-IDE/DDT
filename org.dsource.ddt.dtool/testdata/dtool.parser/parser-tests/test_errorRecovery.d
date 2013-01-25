▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#error(SE_decl){)}
#error(SE_decl) ]
#error(SE_decl) }

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo #error(EXP_IDENTIFIER) ;
#AST_STRUCTURE_EXPECTED:
InvalidSyntaxDeclaration(?)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo #error(EXP_IDENTIFIER) #error(EXP_SEMICOLON)
#AST_STRUCTURE_EXPECTED:
InvalidSyntaxDeclaration(?)
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

#AST_STRUCTURE_EXPECTED:
DeclarationLinkage()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
extern(C) #error(SE_decl) ;

#AST_STRUCTURE_EXPECTED:
DeclarationLinkage(InvalidSyntaxDeclaration)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
extern(C) #error(SE_decl) ] int foo;

#AST_STRUCTURE_EXPECTED:
DeclarationLinkage(InvalidSyntaxDeclaration)
DefinitionVariable(? ?)