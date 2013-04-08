▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo.foo dummy;
FooBar.Foo.FooBar dummy;

#AST_STRUCTURE_EXPECTED:
DefVariable(RefQualified(RefIdentifier RefIdentifier) DefSymbol)
DefVariable(RefQualified(RefQualified(RefIdentifier RefIdentifier) RefIdentifier) DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)        #@TYPE_REFS__QUALIFIER•.foo
#AST_STRUCTURE_EXPECTED: RefQualified(#@TYPE_REFS__QUALIFIER RefIdentifier)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test rule break with stuff like foo. .bar
#PARSE(REFERENCE)         foo. #@NO_ID #@BREAK .bar
#AST_STRUCTURE_EXPECTED:  RefQualified(RefIdentifier #@NO_ID)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(REFERENCE)         foo. #@NO_ID #@BREAK int
#AST_STRUCTURE_EXPECTED:  RefQualified(RefIdentifier #@NO_ID)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)        #@TYPE_REFS__INVALID_QUALIFIER #@PARSE_RULE_END .foo
#AST_STRUCTURE_EXPECTED: #@TYPE_REFS__INVALID_QUALIFIER
