▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#error(SE_decl){)}
#error(SE_decl) ]
#error(SE_decl) }
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ recovery of identifiers:
import#error(EXP_ID);
import #error(EXP_ID) ;
pragma(#error(EXP_ID));
pragma( #error(EXP_ID) );

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ hum, not sure this is the error recovery behavior
mixin #error(EXP_OPEN_PARENS) #error(EXP_SEMICOLON)
mixin #error(EXP_OPEN_PARENS) ;
mixin("blah") #error(EXP_SEMICOLON) #error(SE_decl){)} ;
#AST_EXPECTED:
mixin(); mixin(); mixin("blah"); ) ;
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
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
MiscDeclaration