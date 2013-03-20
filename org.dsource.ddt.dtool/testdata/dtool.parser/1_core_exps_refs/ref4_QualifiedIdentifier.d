▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo.foo dummy;
FooBar.Foo.FooBar dummy;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefQualified(RefIdentifier RefIdentifier) DefSymbol)
DefVariable(RefQualified(RefQualified(RefIdentifier RefIdentifier) RefIdentifier) DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
Foo. #error:EXP_ID int dummy;

FooBar.Bar. #error:EXP_ID int dummy; // Interaction with primitive reference

#AST_STRUCTURE_EXPECTED:
InvalidDeclaration( RefQualified(RefIdentifier RefIdentifier) )
DefVariable(RefPrimitive DefSymbol)
InvalidDeclaration( RefQualified(RefQualified(RefIdentifier RefIdentifier) RefIdentifier) )
DefVariable(RefPrimitive DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test rule break with stuff like foo..bar
#PARSE(REFERENCE)         foo. #@NO_ID #parser(IgnoreRest) .bar
#AST_STRUCTURE_EXPECTED:  RefQualified(RefIdentifier #@NO_ID)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)        #@TYPE_REFS_QUALIFIER•.foo
#AST_STRUCTURE_EXPECTED: RefQualified(#@TYPE_REFS_QUALIFIER RefIdentifier)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)        #error(INV_QUALIFIER)【#@TYPE_REFS_INVALID_QUALIFIER】  #parser(IgnoreRest) .foo dummy;
#AST_STRUCTURE_EXPECTED: #@TYPE_REFS_INVALID_QUALIFIER
