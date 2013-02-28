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
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@TYPE_REFS_QUALIFIER•.foo dummy;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefQualified(#@TYPE_REFS_QUALIFIER RefIdentifier) DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#error(INV_QUALIFIER)【#@TYPE_REFS_INVALID_QUALIFIER】.foo dummy;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefQualified(#@TYPE_REFS_INVALID_QUALIFIER RefIdentifier) DefSymbol)
