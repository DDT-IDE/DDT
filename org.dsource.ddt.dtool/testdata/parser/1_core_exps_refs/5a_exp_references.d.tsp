▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    foo
#STRUCTURE_EXPECTED:  ExpReference(RefIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ // Test valid references that are being parsed as expressions
#PARSE(EXPRESSION)    #@EXP_REFS #?EXP_RULE_BROKEN【#@BREAK】 // EXP_REFS 
#STRUCTURE_EXPECTED:  #@EXP_REFS 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(ExpOrType)     #@EXPS__NO_UNAMBIG_REFS #?EXP_RULE_BROKEN【#@BREAK】
#STRUCTURE_EXPECTED:  #@EXPS__NO_UNAMBIG_REFS

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)     #@INT_AS_EXP * 7 //Consume as much exp, even though it started as TypeRef parse
#STRUCTURE_EXPECTED:  ExpInfix(#@INT_AS_EXP Integer)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test rule break
#PARSE(EXPRESSION_ToE) foo!( ( #@NO_EXP #error(EXP_CLOSE_PARENS) #error(EXP_CLOSE_PARENS) #@BREAK = 3
#STRUCTURE_EXPECTED:   ExpReference(RefTemplateInstance(RefIdentifier ExpParentheses(#@NO_EXP)) #@BREAK )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test rule break
#PARSE(EXPRESSION)     typeof( #@NO_EXP#error(EXP_CLOSE_PARENS) #@BREAK= 3
#STRUCTURE_EXPECTED:   ExpReference(RefTypeof(*))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    #@INT_AS_EXP (123) 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    typeof(bar)(123) // Test that no syntax errors occur

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ special case test precedence limit bug
#PARSE(EXPRESSION)    7 ^^ exp * 2
#STRUCTURE_EXPECTED:  ExpInfix(ExpInfix(Integer #@ExpIdentifier) Integer)
