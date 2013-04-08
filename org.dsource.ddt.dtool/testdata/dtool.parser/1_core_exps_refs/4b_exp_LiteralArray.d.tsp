▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)        [123]
#AST_STRUCTURE_EXPECTED:  ExpLiteralArray(Integer)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)        [1, "2", 66]
#AST_STRUCTURE_EXPECTED:  ExpLiteralArray(Integer String Integer)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)        [ 1, "2" #error(EXP_CLOSE_BRACKET) #@BREAK
#AST_STRUCTURE_EXPECTED:  ExpLiteralArray(Integer String)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)        [ 1, #@NO_EXP #error(EXP_CLOSE_BRACKET) #@BREAK = 2
#AST_STRUCTURE_EXPECTED:  ExpLiteralArray(Integer #@NO_EXP)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)        [ #error(EXP_CLOSE_BRACKET) #@BREAK = 2
#AST_STRUCTURE_EXPECTED:  ExpLiteralArray()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   
[ #@EXP1《1,● #@EXP_ASSIGN ,》   #@EXP2《 #@NO_EXP ● 666》  #@EXP3《  ●, #@EXP_ASSIGN 》  #@BkCLOSE_OR_BREAK 
#AST_STRUCTURE_EXPECTED:
ExpLiteralArray( #@《Integer●#@EXP_ASSIGN》(EXP1) #@《#@NO_EXP●Integer》(EXP2)  #@《●#@EXP_ASSIGN》(EXP3)  )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)        [ #@EXP0《  /*MISSING*/ ● #@EXP_ASSIGN ● #error(EXPRULE_exp) , 123》  #@BkCLOSE_OR_BREAK  
#AST_STRUCTURE_EXPECTED:  ExpLiteralArray( #@《●#@EXP_ASSIGN●MissingExpression Integer》(EXP0)  )
