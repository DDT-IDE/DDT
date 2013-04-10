▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo[] dummy;
int[] [] dummy2;

#STRUCTURE_EXPECTED:
DefVariable(RefTypeDynArray(RefIdentifier) DefSymbol)
DefVariable(RefTypeDynArray(RefTypeDynArray(RefPrimitive))  DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     foo[]
#STRUCTURE_EXPECTED:  RefTypeDynArray(RefIdentifier)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     foo[ #error:EXP_CLOSE_BRACKET #@BREAK
#STRUCTURE_EXPECTED:  RefTypeDynArray(RefIdentifier)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     #@TYPE_REFS__NO_BROKEN•[]
#STRUCTURE_EXPECTED:
RefTypeDynArray(#@TYPE_REFS__NO_BROKEN)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     #@TYPE_REFS__NO_BROKEN•[][]
#STRUCTURE_EXPECTED:  
RefTypeDynArray(RefTypeDynArray(#@TYPE_REFS__NO_BROKEN))
