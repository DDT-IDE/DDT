▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
.foo dummy;
. FooBar dummy;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefModuleQualified(?) DefSymbol)
DefVariable(RefModuleQualified(?) DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
. #error:EXP_ID int dummy;

#AST_STRUCTURE_EXPECTED:
InvalidSyntaxElement( RefModuleQualified(?) )
DefVariable(RefPrimitive DefSymbol)

#AST_SOURCE_EXPECTED:
. 
int dummy;
