▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
[1, "2", 66]
#AST_STRUCTURE_EXPECTED:
ExpLiteralArray(Integer String Integer)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
[ 1, "2" #error(EXP_CLOSE_BRACKET)
#AST_STRUCTURE_EXPECTED:
ExpLiteralArray(Integer String)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
[ #error(EXP_CLOSE_BRACKET)
#AST_STRUCTURE_EXPECTED:
ExpLiteralArray()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
[ #@EXP1《1,● #@EXP_ASSIGN ,》   #@EXP2《 #error(EXPRULE_exp) ● 666》  #@EXP3《  ●, #@EXP_ASSIGN 》  
#@《]●#error(EXP_CLOSE_BRACKET)》  
#AST_STRUCTURE_EXPECTED:
ExpLiteralArray( #@《Integer●#@EXP_ASSIGN》(EXP1) #@《MissingExpression●Integer》(EXP2)  #@《●#@EXP_ASSIGN》(EXP3)  )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
[ #@EXP3《  ● #@EXP_ASSIGN 》  #@《]●#error(EXP_CLOSE_BRACKET)》  
#AST_STRUCTURE_EXPECTED:
ExpLiteralArray( #@《●#@EXP_ASSIGN》(EXP3)  )
