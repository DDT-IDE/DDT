Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    !true
#STRUCTURE_EXPECTED:  ExpPrefix(Bool)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    *&"asdfsd"
#STRUCTURE_EXPECTED:  ExpPrefix(ExpPrefix(String))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@INT_OR_MISSING《
  ►#?STRUCTURE_EXPECTED!【123●Integer】●
  ►#@NO_EXP #@BREAK #LAST_INFIX_BROKEN(flag)●
¤》

#@OP《&●++●--●*●-●+●!●~●delete》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    #@OP #@^OP  #@INT_OR_MISSING
#STRUCTURE_EXPECTED:  ExpPrefix(ExpPrefix( #@INT_OR_MISSING ))
	
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   +123 + + ++#@INT_OR_MISSING == "RIGHT"
#STRUCTURE_EXPECTED:
#?LAST_INFIX_BROKEN!【ExpInfix(】
  ExpInfix(
    ExpPrefix(Integer) 
    ExpPrefix(ExpPrefix(#@INT_OR_MISSING))
  )
  String
)
