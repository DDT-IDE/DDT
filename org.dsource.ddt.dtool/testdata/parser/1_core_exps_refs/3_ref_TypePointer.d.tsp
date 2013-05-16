▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo* dummy;
int** dummy2;
#STRUCTURE_EXPECTED:
DefVariable(RefTypePointer(RefIdentifier) DefSymbol)
DefVariable(RefTypePointer(RefTypePointer(RefPrimitive))  DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     foo*
#STRUCTURE_EXPECTED:  RefTypePointer(RefIdentifier)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     #@TYPE_REFS__NO_BROKEN•* 
#STRUCTURE_EXPECTED:  RefTypePointer(#@TYPE_REFS__NO_BROKEN)

