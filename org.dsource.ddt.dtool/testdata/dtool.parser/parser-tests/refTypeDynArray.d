▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo[] dummy;
int[] [] dummy2;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefTypeDynArray(RefIdentifier) DefSymbol)
DefVariable(RefTypeDynArray(RefTypeDynArray(RefPrimitive))  DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo[] #error:EXP_ID ;
foo2[] #error:EXP_ID #error:EXP_SEMICOLON

#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(RefTypeDynArray(RefIdentifier))
InvalidDeclaration(RefTypeDynArray(RefIdentifier))
#AST_SOURCE_EXPECTED:
foo[] ;
foo2[] ;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo[ #error:EXP_CLOSE_BRACKET int dummy ;
foo2[ #error:EXP_CLOSE_BRACKET ;
foo3[ #error:EXP_CLOSE_BRACKET 

#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(RefTypeDynArray(RefIdentifier)) DefinitionVariable(RefPrimitive DefSymbol)
InvalidDeclaration(RefTypeDynArray(RefIdentifier))  DeclarationEmpty
InvalidDeclaration(RefTypeDynArray(RefIdentifier))  
#AST_SOURCE_EXPECTED:
foo[] ; int dummy;
foo2[] ; ;
foo3[] ;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@(SP_TYPE_REF)[] dummy;

#@(SP_TYPE_REF)[][] dummy2;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefTypeDynArray(#@SPSE_TYPE_REF(SP_TYPE_REF)) DefSymbol)
DefVariable(RefTypeDynArray(RefTypeDynArray(#@SPSE_TYPE_REF(SP_TYPE_REF))) DefSymbol)
