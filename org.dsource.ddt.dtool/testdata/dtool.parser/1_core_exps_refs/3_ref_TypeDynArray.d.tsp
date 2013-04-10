▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo[] dummy;
int[] [] dummy2;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefTypeDynArray(RefIdentifier) DefSymbol)
DefVariable(RefTypeDynArray(RefTypeDynArray(RefPrimitive))  DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo[] #error:EXP_ID ;
foo2[] #error:EXP_ID #@INVALID_DECL__NO_SEMICOLON

#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(RefTypeDynArray(RefIdentifier))
InvalidDeclaration(RefTypeDynArray(RefIdentifier))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo[ #error:EXP_CLOSE_BRACKET public int dummy ;
foo2[ #error:EXP_CLOSE_BRACKET ;
foo3[ #error:EXP_CLOSE_BRACKET 

#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(RefTypeDynArray(RefIdentifier)) DeclarationProtection(DefinitionVariable(RefPrimitive DefSymbol))
InvalidDeclaration(RefTypeDynArray(RefIdentifier))  DeclarationEmpty
InvalidDeclaration(RefTypeDynArray(RefIdentifier))  


▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@TYPE_REFS__NO_BROKEN•[] dummy;
#@TYPE_REFS__NO_BROKEN•[][] dummy2;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefTypeDynArray(#@TYPE_REFS__NO_BROKEN) DefSymbol)
DefVariable(RefTypeDynArray(RefTypeDynArray(#@TYPE_REFS__NO_BROKEN)) DefSymbol)
