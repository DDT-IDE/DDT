Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) !true
#AST_STRUCTURE_EXPECTED:
ExpPrefix(Bool)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) *&"asdfsd"
#AST_STRUCTURE_EXPECTED:
ExpPrefix(ExpPrefix(String))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@INT_OR_MISSING《
  ►#?AST_STRUCTURE_EXPECTED!【123●Integer】●
  ►#?AST_STRUCTURE_EXPECTED!【#error(EXPRULE_exp)●】●
¤》

#@OP《&●++●--●*●-●+●!●~●delete》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    #@OP #@^OP  #@INT_OR_MISSING
#AST_STRUCTURE_EXPECTED:
ExpPrefix(ExpPrefix( #@INT_OR_MISSING ))
	
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   +123 + + ++#@INT_OR_MISSING == "RIGHT"
#AST_STRUCTURE_EXPECTED:
ExpInfix(
  ExpInfix(
    ExpPrefix(Integer) 
    ExpPrefix(ExpPrefix(#@INT_OR_MISSING))
  )
  String
)
