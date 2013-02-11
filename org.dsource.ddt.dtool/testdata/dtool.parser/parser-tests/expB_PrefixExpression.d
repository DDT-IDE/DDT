Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) !true
#AST_STRUCTURE_EXPECTED:
PrefixExpression(Bool)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) *&"asdfsd"
#AST_STRUCTURE_EXPECTED:
PrefixExpression(PrefixExpression(String))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@INT_OR_MISSING《
  ►#?AST_STRUCTURE_EXPECTED!【123●Integer】●
  ►#?AST_STRUCTURE_EXPECTED!【#error(EXPRULE_exp)●】●
¤》

#@OP《&●++●--●*●-●+●!●~》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    #@OP #@^OP  #@INT_OR_MISSING
#AST_STRUCTURE_EXPECTED:
PrefixExpression(PrefixExpression( #@INT_OR_MISSING ))
	
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   +123 + + ++#@INT_OR_MISSING == "RIGHT"
#AST_STRUCTURE_EXPECTED:
InfixExpression(
  InfixExpression(
    PrefixExpression(Integer) 
    PrefixExpression(PrefixExpression(#@INT_OR_MISSING))
  )
  String
)
