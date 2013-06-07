▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo[int] dummy;
#STRUCTURE_EXPECTED:
DefVariable(RefIndexing(RefIdentifier RefPrimitive) DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int[123] [4] dummy2;
#STRUCTURE_EXPECTED:
DefVariable(RefIndexing(RefIndexing(RefPrimitive ExpLiteralInteger) ExpLiteralInteger)  DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ duplicate test case, here for clarity
#PARSE(REFERENCE)     foo[ #error(EXP_CLOSE_BRACKET) #@BREAK
#STRUCTURE_EXPECTED:  RefTypeDynArray(RefIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     #@TYPEREFS__NB_APPENDABLE[int]
#STRUCTURE_EXPECTED:  RefIndexing(#@TYPEREFS__NB_APPENDABLE RefPrimitive)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     Foo[#NO_BROKEN_BRACKETS(flag) #@TYPE_REFS #@CLOSEBRACKET_OR_BREAK
#STRUCTURE_EXPECTED:  RefIndexing(RefIdentifier #@TYPE_REFS)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     #@TYPEREFS__NB_APPENDABLE[#@EXP_ASSIGN__NO_POSSIBLE_REFS #@CLOSEBRACKET_OR_BREAK
#STRUCTURE_EXPECTED:  RefIndexing(#@TYPEREFS__NB_APPENDABLE #@EXP_ASSIGN__NO_POSSIBLE_REFS)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Dont allow comma exp (this could change in future)
#PARSE(REFERENCE)     #@TYPEREF_NBNE__LITE[123 #@BRACKET_BREAK , 2 ] 
#STRUCTURE_EXPECTED:  RefIndexing(#@TYPEREF_NBNE__LITE Integer)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     #@^TYPE_REFS__NO_BROKEN[#NO_BROKEN_BRACKETS(flag) #@^TYPE_REFS]
