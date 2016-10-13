Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
http://code.google.com/p/ddt/issues/detail?id=17
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   ceil(num3 #@PARENS_BREAK : 0)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   cast(int) ceil(num3 #@PARENS_BREAK : 0)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   showanims ? cast(int) ceil(num3 #@NO_ClosePARENS : 0 #@IGNORE_REST )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) #@IGNORE_ALL_CHECKS  
showanims ? cast(int) ceil(cast(float)num2 / cast(float) num3 : 0)