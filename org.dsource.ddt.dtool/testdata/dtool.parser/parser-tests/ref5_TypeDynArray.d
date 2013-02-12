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
foo2[] 

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo[ #error:EXP_CLOSE_BRACKET public int dummy ;
foo2[ #error:EXP_CLOSE_BRACKET ;
foo3[ #error:EXP_CLOSE_BRACKET 

#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(RefTypeDynArray(RefIdentifier)) DeclarationProtection(DefinitionVariable(RefPrimitive DefSymbol))
InvalidDeclaration(RefTypeDynArray(RefIdentifier))  DeclarationEmpty
InvalidDeclaration(RefTypeDynArray(RefIdentifier))  
#AST_SOURCE_EXPECTED:
foo[]  public int dummy;
foo2[]  ;
foo3[] 

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@TYPE_REFS•[] dummy;

#@TYPE_REFS•[][] dummy2;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefTypeDynArray(#@TYPE_REFS) DefSymbol)
DefVariable(RefTypeDynArray(RefTypeDynArray(#@TYPE_REFS)) DefSymbol)
