▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo* dummy;
int** dummy2;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefTypePointer(RefIdentifier) DefSymbol)
DefVariable(RefTypePointer(RefTypePointer(RefPrimitive))  DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo* #error:EXP_ID ;
foo2* #error:EXP_ID #error:EXP_SEMICOLON

#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(RefTypePointer(RefIdentifier))
InvalidDeclaration(RefTypePointer(RefIdentifier))
#AST_SOURCE_EXPECTED:
foo* ;
foo2* ;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@(SP_TYPE_REF)* dummy;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefTypePointer(#@SPSE_TYPE_REF(SP_TYPE_REF)) DefSymbol)


