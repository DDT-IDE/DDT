▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo* dummy;
int** dummy2;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefTypePointer(RefIdentifier) DefSymbol)
DefVariable(RefTypePointer(RefTypePointer(RefPrimitive))  DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo* #error:EXP_ID ;
foo2* #error:EXP_ID #@INVALID_DECL__NO_SEMICOLON

#AST_STRUCTURE_EXPECTED:
InvalidDeclaration(RefTypePointer(RefIdentifier))
InvalidDeclaration(RefTypePointer(RefIdentifier))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@TYPE_REFS__NO_BROKEN•* dummy;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefTypePointer(#@TYPE_REFS__NO_BROKEN) DefSymbol)


