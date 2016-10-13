Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test possible refs under TypeOrExp parse
#PARSE(TypeOrExp)     #@TYPE_REFS #?TYPE_IS_BROKEN{#@BREAK}
#STRUCTURE_EXPECTED:  #@TYPE_REFS
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test op category mismatch
#PARSE(TypeOrExp)      foo / * #@NO_EXP  #@BREAK
#STRUCTURE_EXPECTED:   ExpInfix(ExpReference(?) ExpPrefix(#@NO_EXP) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test with potential invalid qualifier
#PARSE(TypeOrExp)      foo * function(a) #@IGNORE_REST . init
#STRUCTURE_EXPECTED:   RefTypeFunction( #@_FOO_PTR NamelessParameter(?) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test bug case
#PARSE(TypeOrExp)      foo function(a) #@IGNORE_REST . init
#STRUCTURE_EXPECTED:   RefTypeFunction( RefIdentifier NamelessParameter(?) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(TypeOrExp)     #@EXP_NE #?MISSING_BRACKET{#@BREAK} #?_EXP_AMBIG{ #parser(AllowAnyErrors) }

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    foo
#STRUCTURE_EXPECTED:  ExpReference(RefIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)     #@INT_AS_EXP * 7 //Consume as much exp, even though it started as TypeRef parse
#STRUCTURE_EXPECTED:  ExpInfix(#@INT_AS_EXP Integer)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ // Test valid references that are being parsed as expressions
#PARSE(EXPRESSION)    #@_EXP_REFS_BREAK!《#@IGNORE_REST》 #@EXP_REFS #?EXP_RULE_BROKEN【#@BREAK】  // EXP_REFS 
#STRUCTURE_EXPECTED:  #@EXP_REFS 

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test rule break
#PARSE(EXPRESSION_ToE) foo!( ( #@NO_EXP #error(EXP_CLOSE_PARENS) #error(EXP_CLOSE_PARENS) #@BREAK = 3
#STRUCTURE_EXPECTED:   ExpReference(RefTemplateInstance(RefIdentifier ExpParentheses(#@NO_EXP)) #@BREAK )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test rule break
#PARSE(EXPRESSION)     typeof( #@NO_EXP#error(EXP_CLOSE_PARENS) #@BREAK= 3
#STRUCTURE_EXPECTED:   ExpReference(RefTypeof(*))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    typeof(bar)(123) // Test that no syntax errors occur

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ special case test precedence limit bug
#PARSE(EXPRESSION)    7 ^^ exp * 2
#STRUCTURE_EXPECTED:  ExpInfix(ExpInfix(Integer #@ExpIdentifier) Integer)

