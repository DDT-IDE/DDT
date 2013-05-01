▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo[int] dummy;
int[foo][Bar] dummy2;

#STRUCTURE_EXPECTED:
DefVariable(RefIndexing(RefIdentifier RefPrimitive) DefSymbol)
DefVariable(RefIndexing(RefIndexing(RefPrimitive RefIdentifier) RefIdentifier)  DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo[1] dummy;
int[123] [4] dummy2;

#STRUCTURE_EXPECTED:
DefVariable(RefIndexing(RefIdentifier ExpLiteralInteger) DefSymbol)
DefVariable(RefIndexing(RefIndexing(RefPrimitive ExpLiteralInteger) ExpLiteralInteger)  DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ duplicate test case, here for clarity
#PARSE(REFERENCE)     foo[ #error(EXP_CLOSE_BRACKET) #@BREAK
#STRUCTURE_EXPECTED:  RefTypeDynArray(RefIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     #@TYPE_REFS__NO_BROKEN[int]
#STRUCTURE_EXPECTED:  RefIndexing(#@TYPE_REFS__NO_BROKEN RefPrimitive)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     Foo[#NO_BROKEN_BRACKETS(flag) #@TYPE_REFS #@CLOSEBRACKET_OR_BREAK
#STRUCTURE_EXPECTED:  RefIndexing(RefIdentifier #@TYPE_REFS)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     #@TYPE_REFS__NO_BROKEN[#@EXPS__NO_POSSIBLE_REFS #@CLOSEBRACKET_OR_BREAK
#STRUCTURE_EXPECTED:  RefIndexing(#@TYPE_REFS__NO_BROKEN #@EXPS__NO_POSSIBLE_REFS)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)         #@^TYPE_REFS__NO_BROKEN[#NO_BROKEN_BRACKETS(flag) #@^TYPE_REFS]
