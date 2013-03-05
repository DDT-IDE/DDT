▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) 123 ^^ *foo
#AST_STRUCTURE_EXPECTED:
ExpInfix(Integer ExpPrefix(#@ExpIdentifier))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) foo + * 123 ^^ exp 
#AST_STRUCTURE_EXPECTED:
ExpInfix(#@ExpIdentifier 
  ExpPrefix(ExpInfix(Integer #@ExpIdentifier))
)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) foo + * 123 ^^ exp(this)
#AST_STRUCTURE_EXPECTED:
ExpInfix(#@ExpIdentifier 
  ExpPrefix( ExpInfix(Integer ExpCall(#@ExpIdentifier ExpThis))) 
)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) foo + * 123 ^^ exp ^^ this
#AST_STRUCTURE_EXPECTED:
ExpInfix(#@ExpIdentifier 
  ExpPrefix( ExpInfix(Integer ExpInfix(#@ExpIdentifier ExpThis))) 
)