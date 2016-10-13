▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
foo.foo dummy;
FooBar.Foo.FooBar dummy;

#STRUCTURE_EXPECTED:
DefVariable(RefQualified(RefIdentifier RefIdentifier) DefSymbol)
DefVariable(RefQualified(RefQualified(RefIdentifier RefIdentifier) RefIdentifier) DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     #@TYPE_REFS__QUALIFIER_ForDot•.foo
#STRUCTURE_EXPECTED:  RefQualified(#@TYPE_REFS__QUALIFIER_ForDot RefIdentifier)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test rule break with stuff like foo. .bar
#PARSE(REFERENCE)     foo. #@NO_ID #@BREAK .bar
#STRUCTURE_EXPECTED:  RefQualified(RefIdentifier #@NO_ID)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(REFERENCE)     foo. #@NO_ID #@BREAK int
#STRUCTURE_EXPECTED:  RefQualified(RefIdentifier #@NO_ID)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ bug #101 similar:
#PARSE(REFERENCE)     foo. #@NO_ID #@BREAK .n
#STRUCTURE_EXPECTED:  RefQualified(RefIdentifier #@NO_ID)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     #@TYPE_REFS__NO_BROKEN__INVALID_QUALIFIER #@IGNORE_REST .foo
#STRUCTURE_EXPECTED:  #@TYPE_REFS__NO_BROKEN__INVALID_QUALIFIER
