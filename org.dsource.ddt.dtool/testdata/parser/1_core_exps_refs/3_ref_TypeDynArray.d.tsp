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
#PARSE(REFERENCE)     #@TYPEREFS__NOBROKEN_APPENDABLE•[] 
#STRUCTURE_EXPECTED:
RefTypeDynArray(#@TYPEREFS__NOBROKEN_APPENDABLE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     #@TYPEREFS__NOBROKEN_APPENDABLE•[][]
#STRUCTURE_EXPECTED:  
RefTypeDynArray(RefTypeDynArray(#@TYPEREFS__NOBROKEN_APPENDABLE))
