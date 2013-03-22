▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
.foo dummy;
. FooBar dummy;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefModuleQualified(?) DefSymbol)
DefVariable(RefModuleQualified(?) DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
. #error:EXP_ID int dummy;

#AST_STRUCTURE_EXPECTED:
InvalidDeclaration( RefModuleQualified(?) )
DefVariable(RefPrimitive DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test rule break with stuff like ..foo
#PARSE(REFERENCE)         . #@NO_ID #parser(IgnoreRest) .foo
#AST_STRUCTURE_EXPECTED:  RefModuleQualified(#@NO_ID)
