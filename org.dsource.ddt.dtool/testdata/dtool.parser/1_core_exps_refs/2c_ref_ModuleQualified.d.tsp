▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
.foo dummy;
. FooBar dummy;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefModuleQualified(?) DefSymbol)
DefVariable(RefModuleQualified(?) DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test rule break with stuff like . .foo
#PARSE(REFERENCE)         . #@NO_ID #@BREAK .foo
#AST_STRUCTURE_EXPECTED:  RefModuleQualified(#@NO_ID)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(REFERENCE)         . #error:EXP_ID #@BREAK int
#AST_STRUCTURE_EXPECTED:  RefModuleQualified(#@NO_ID)
